package ru.nekit.android.qls.setupWizard.view.viewModel

import android.app.Activity
import android.support.annotation.ColorRes
import android.text.format.DateFormat
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.PeriodTime
import ru.nekit.android.qls.domain.model.SKU
import ru.nekit.android.qls.domain.useCases.AccessUseCases
import ru.nekit.android.qls.domain.useCases.PremiumAccessUseCases
import ru.nekit.android.qls.setupWizard.QuestSetupWizard
import ru.nekit.android.utils.IAutoDispose

class AccessInfoViewModel(private val setupWizard: QuestSetupWizard, private val activity: Activity) : IAutoDispose {

    override var disposableMap: MutableMap<String, CompositeDisposable> = HashMap()

    val actionListener = PublishSubject.create<SKU>().toSerialized()

    init {
        autoDispose {
            actionListener.subscribe {
                setupWizard.initiateSKUPurchaseFlow(activity, it)
            }
        }
    }

    fun listenAvailableSubscriptions(body: (List<SubscriptionViewItem>) -> Unit) = autoDispose {
        PremiumAccessUseCases.listenAvailableSubscriptions()
                .map { data ->
                    data.first.map {
                        it.let {
                            SubscriptionViewItem(
                                    it.sku,
                                    it.title,
                                    it.description,
                                    it.price,
                                    if (data.second) "Сменить подписку" else "Оформить подписку")

                        }
                    }
                }
                .subscribe(body)
    }

    fun listenAccessInfo(body: (AccessInfoViewItem) -> Unit) = autoDispose {
        AccessUseCases.listenAccessInfo().map { info ->
            when {
                info.premium != null -> info.premium?.let {
                    val expiredTime = DateFormat.format("d MMMM yyyy",
                            it.skuPurchase.time + setupWizard.timeProvider.getPeriodTime(
                                    PeriodTime.valueOf(it.skuDetails.subscriptionPeriod)))
                    AccessInfoViewItem(it.skuDetails.description, if (it.skuPurchase.autoRenewing)
                        "Следующий платеж: $expiredTime"
                    else
                        "Подписка будет отменена $expiredTime",
                            if (it.skuPurchase.autoRenewing) R.color.gold else R.color.silver)
                }
                info.trial != null -> info.trial?.let { trial ->
                    val expiredTime = DateFormat.format("d MMMM yyyy",
                            (trial.startTimestamp
                                    ?: setupWizard.timeProvider.getCurrentTime()) +
                                    setupWizard.timeProvider.getPeriodTime(trial.periodTime))
                    AccessInfoViewItem("Пробный период",
                            "Доступен до $expiredTime",
                            R.color.green_light)
                }
                else -> AccessInfoViewItem("Пробный период закончился", "", R.color.red)
            }
        }.subscribe { body(it!!) }
    }

}

data class SubscriptionViewItem(val sku: SKU,
                                val title: String,
                                val description: String,
                                val price: String,
                                val actionButtonLabel: String)

data class AccessInfoViewItem(val title: String,
                              val expiredTimeText: String,
                              @ColorRes val backgroundColor: Int)