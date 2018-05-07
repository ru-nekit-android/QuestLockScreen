package ru.nekit.android.qls.domain.useCases

import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.FlowableProcessor
import ru.nekit.android.domain.interactor.buildFlowableUseCase
import ru.nekit.android.qls.domain.providers.UseCaseSupport
import java.util.concurrent.TimeUnit.MILLISECONDS

object AdsUseCases : UseCaseSupport() {

    fun listenAdsSkipTimer(body: (Long, Long) -> Unit): Disposable = buildFlowableUseCase(schedulerProvider, {
        AdsTimer.publisher.map {
            it / AdsTimer.TIME_RESOLUTION to
                    repositoryHolder.getQuestSetupWizardSettingRepository().adsSkipTimeout / AdsTimer.TIME_RESOLUTION
        }
    }).doOnSubscribe {
        AdsTimer.start()
    }.doOnCancel {
        AdsTimer.stop()
    }.subscribe {
        body(it.first, it.second)
    }

}

internal object AdsTimer {

    internal const val TIME_RESOLUTION = 1000L

    fun start() {
        stop()
        publisher.onNext(0)
        disposable = Flowable.interval(TIME_RESOLUTION, MILLISECONDS).timeInterval()
                .onBackpressureDrop().map { it.value() * TIME_RESOLUTION }.subscribe {
                    publisher.onNext(it)
                }
    }

    fun stop() {
        disposable?.dispose()
    }

    private var disposable: Disposable? = null

    val publisher: FlowableProcessor<Long> = BehaviorProcessor.create<Long>().toSerialized()

}