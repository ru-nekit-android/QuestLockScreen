package ru.nekit.android.qls.domain.useCases

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.FlowableProcessor
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.*
import ru.nekit.android.qls.domain.model.SKUType.PRESENT
import ru.nekit.android.qls.domain.model.SKUType.SUBSCRIPTION
import ru.nekit.android.qls.domain.providers.UseCaseSupport
import ru.nekit.android.qls.domain.repository.ISKURepository
import ru.nekit.android.qls.domain.useCases.SKUUseCases.get
import ru.nekit.android.qls.domain.useCases.SKUUseCases.skuPurchaseRepository

object SKUUseCases : UseCaseSupport() {

    lateinit var billing: IBilling

    private val skuDetailsRepository
        get() = repository.getSKUDetailsRepository()

    internal val skuPurchaseRepository
        get() = repository.getSKUPurchaseRepository()

    private fun List<ISKUHolder>.notify(flowable: FlowableProcessor<List<SKU>>, skuType: SKUType) =
            map { it.sku }.filter { it.skuType == skuType }.let {
                log("notify: $it of $skuType")
                flowable.onNext(it)
            }

    internal fun <T : ISKUHolder> List<T>.save(repository: ISKURepository<T>) =
            repository.clear().andThen(
                    Flowable.fromIterable(
                            map {
                                repository.add(it)
                            })
                            .flatMapCompletable { task ->
                                task.subscribeOn(schedulerProvider.newThread())
                            })

    internal fun <T : ISKUHolder> List<SKU>.get(repository: ISKURepository<T>) =
            Flowable.fromIterable(
                    map {
                        repository.getBy(it).map { it.nonNullData }
                    }
            ).flatMapSingle { task ->
                task.subscribeOn(schedulerProvider.newThread())
            }.toList().toFlowable()

    fun saveSKUDetails(list: List<SKUDetails>) = completableUseCase {
        list.save(skuDetailsRepository)
    }.use {
        list.apply {
            notify(SKUPublisherHolder.subscriptionDetails, SUBSCRIPTION)
            notify(SKUPublisherHolder.presentDetails, PRESENT)
        }
    }

    fun saveSKUPurchases(list: List<SKUPurchase>) = completableUseCase {
        list.save(skuPurchaseRepository)
    }.use {
        log("save purchases: $list")
        AccessUseCases.checkAccess(false)
        list.apply {
            notify(SKUPublisherHolder.presentPurchases, PRESENT)
            notify(SKUPublisherHolder.subscriptionPurchases, SUBSCRIPTION)
        }
    }

    internal fun getSKUPurchasesByType(skuType: SKUType) = buildSingleUseCase {
        skuPurchaseRepository.getAll().map {
            it.filter {
                it.sku.skuType == skuType
            }
        }
    }


    fun getSKUDetails(sku: SKU) = buildSingleUseCase {
        skuDetailsRepository.getBy(sku)
    }

    internal fun listenSubscriptionsDetails() = buildFlowableUseCase {
        SKUPublisherHolder.subscriptionDetails.flatMap {
            it.get(skuDetailsRepository)
        }
    }

    internal fun listenPurchasedSubscriptions() = buildFlowableUseCase {
        SKUPublisherHolder.subscriptionPurchases.flatMap {
            it.get(skuPurchaseRepository)
        }
    }

    fun purchasedSubscription() = singleUseCase {
        skuPurchaseRepository.getAll().map { Optional(if (it.isEmpty()) null else it.first()) }
    }

    internal fun checkStatus() = billing.start()
}

object PremiumAccessUseCases : UseCaseSupport() {

    fun checkAccess(checkOnline: Boolean): Single<Boolean> = buildSingleUseCase {
        SKUUseCases.getSKUPurchasesByType(SUBSCRIPTION).flatMap {
            Flowable.fromIterable(it).flatMapSingle {
                Single.zip(Single.just(it),
                        SKUUseCases.getSKUDetails(it.sku),
                        BiFunction<SKUPurchase, Optional<SKUDetails>,
                                Pair<SKUPurchase, SKUDetails>> { t1, t2 -> t1 to t2.nonNullData })
            }.toList()
        }.map {
            it.any {
                timeProvider.getCurrentTime() - it.first.time <
                        timeProvider.getPeriodTime(PeriodTime.valueOf(it.second.subscriptionPeriod))
            }
        }
    }.doOnSubscribe {
        if (checkOnline) SKUUseCases.checkStatus()
    }

    fun listenAvailableSubscriptions(): Flowable<Pair<List<SKUDetails>, Boolean>> = buildFlowableUseCase {
        Flowable.combineLatest(SKUUseCases.listenSubscriptionsDetails(),
                SKUUseCases.listenPurchasedSubscriptions(),
                BiFunction<List<SKUDetails>, List<SKUPurchase>,
                        Pair<List<SKUDetails>, Boolean>> { ds, ps ->
                    val hasPurchased = ps.isNotEmpty()
                    val actualSubscription = if (hasPurchased) ps.first() else null
                    val actualDetailsList = when {
                        actualSubscription == null -> ds
                        actualSubscription.autoRenewing -> ds.filter { it.sku != actualSubscription.sku }
                        else -> ds
                    }
                    actualDetailsList to hasPurchased
                }
        )
    }

    fun listenPurchasedSubscription(): Flowable<Optional<SKUPurchase>> = buildFlowableUseCase {
        SKUPublisherHolder.subscriptionPurchases.map {
            Optional(if (it.isEmpty()) null else it.first())
        }.flatMap {
            if (it.isNotEmpty())
                listOf(it.nonNullData).get(skuPurchaseRepository).map {
                    Optional(if (it.isEmpty()) null else it.first())
                }
            else Flowable.fromCallable { Optional(null) }
        }
    }

}

object TrialAccessUseCases : UseCaseSupport() {

    fun checkAccess(): Single<Boolean> =
            LockScreenUseCases.firstStartTimestamp().map {
                if (it.isEmpty()) true else
                    timeProvider.getCurrentTime() - it.nonNullData < timeProvider.getPeriodTime(getPeriodTime())
            }


    fun getPeriodTime(): PeriodTime = PeriodTime.P1M

}

object AccessUseCases : UseCaseSupport() {

    fun checkAccess(checkOnline: Boolean = true): Unit = useSingleUseCase({
        Single.zip(TrialAccessUseCases.checkAccess(),
                PremiumAccessUseCases.checkAccess(checkOnline),
                BiFunction<Boolean, Boolean, Unit> { trial, premium ->
                    applyAccessStatus(trial, premium)
                })
    })

    fun listenAccessInfo() = buildFlowableUseCase {
        PremiumAccessUseCases.listenPurchasedSubscription().flatMapSingle { purchaseOpt ->
            Single.zip(if (purchaseOpt.isEmpty())
                Single.fromCallable { Optional(null) }
            else
                SKUUseCases.getSKUDetails(purchaseOpt.nonNullData.sku).map {
                    Optional(it.nonNullData to purchaseOpt.nonNullData)
                },
                    TrialAccessUseCases.checkAccess(),
                    LockScreenUseCases.firstStartTimestamp(),
                    Function3<Optional<Pair<SKUDetails, SKUPurchase>>, Boolean, Optional<Long>, AccessInfo> { skuPairOpt, trial, trialStartTimestampOpt ->
                        log("listen access info")
                        AccessInfo(if (trial) TrialAccessInfo(trialStartTimestampOpt.data,
                                if (trialStartTimestampOpt.isEmpty()) null else
                                    trialStartTimestampOpt.nonNullData +
                                            timeProvider.getPeriodTime(TrialAccessUseCases.getPeriodTime()),
                                TrialAccessUseCases.getPeriodTime()) else null,
                                if (skuPairOpt.isNotEmpty()) {
                                    val skuPair = skuPairOpt.nonNullData
                                    PremiumAccessInfo(skuPair.first, skuPair.second,
                                            skuPair.second.time,
                                            skuPair.second.time +
                                                    timeProvider.getPeriodTime(PeriodTime.valueOf(skuPair.first.subscriptionPeriod)),
                                            PeriodTime.valueOf(skuPair.first.subscriptionPeriod))
                                } else
                                    null)
                    })

        }
    }

    private fun applyAccessStatus(trial: Boolean, premium: Boolean) {
        val showAdvert = when {
            premium -> {
                false
            }
            trial -> {
                true
            }
            else -> {
                true
            }
        }
        TransitionChoreographUseCases.advertIsPresent(showAdvert)
    }
}

enum class AccessType {

    TRIAL,
    PREMIUM,
    EXPIRED

}

data class AccessInfo(val trial: TrialAccessInfo?, val premium: PremiumAccessInfo?)

data class TrialAccessInfo(val startTimestamp: Long?,
                           val stopTimestamp: Long?,
                           val periodTime: PeriodTime)

data class PremiumAccessInfo(val skuDetails: SKUDetails,
                             val skuPurchase: SKUPurchase,
                             val startTimestamp: Long,
                             val stopTimestamp: Long,
                             val periodTime: PeriodTime
)

private object SKUPublisherHolder {

    fun createPublisher() = BehaviorProcessor.create<List<SKU>>().toSerialized()

    val subscriptionPurchases: FlowableProcessor<List<SKU>> = createPublisher()
    val presentPurchases: FlowableProcessor<List<SKU>> = createPublisher()

    val subscriptionDetails: FlowableProcessor<List<SKU>> = createPublisher()
    val presentDetails: FlowableProcessor<List<SKU>> = createPublisher()

}

interface IBilling {

    fun start()

    fun destroy()

    fun querySKUPurchases()

    fun querySKUDetails()

}

enum class PeriodTime {

    P1W,
    P1M,
    P3M,
    P6M,
    P1Y;
}