package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.FlowableProcessor
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.CompletableUseCase
import ru.nekit.android.domain.interactor.ParameterlessFlowableUseCase
import ru.nekit.android.domain.interactor.ParameterlessSingleUseCase
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.qls.domain.model.PupilStatistics
import ru.nekit.android.qls.domain.repository.IRepositoryHolder

internal class InternalGetCurrentPupilStatisticsUseCase(private val repository: IRepositoryHolder,
                                                        scheduler: ISchedulerProvider? = null) :
        ParameterlessSingleUseCase<PupilStatistics>(scheduler) {

    override fun build(): Single<PupilStatistics> =
            pupil(repository) {
                repository.getPupilStatisticsRepository().get(it)
            }

}

class GetCurrentPupilStatisticsUseCase(private val repository: IRepositoryHolder,
                                       private val scheduler: ISchedulerProvider? = null) :
        ParameterlessFlowableUseCase<PupilStatistics>(scheduler) {

    override fun build(): Flowable<PupilStatistics> = PupilStatisticsHolder.publisher.doOnSubscribe {
        if (PupilStatisticsHolder.statistics == null) {
            InternalGetCurrentPupilStatisticsUseCase(repository, scheduler).use {
                PupilStatisticsHolder.statistics = it
            }
        }
    }
}

class UpdateCurrentPupilStatisticsUseCase(private val repository: IRepositoryHolder, scheduler: ISchedulerProvider? = null) :
        CompletableUseCase<PupilStatistics>(scheduler) {

    override fun build(parameter: PupilStatistics): Completable =
            pupilAsCompletable(repository) {
                repository.getPupilStatisticsRepository().update(it, parameter)
            }.doOnComplete {
                PupilStatisticsHolder.statistics = parameter
            }

}


private object PupilStatisticsHolder {

    var statistics: PupilStatistics? = null
        set(value) {
            field = value
            publisher.onNext(value)
        }

    val publisher: FlowableProcessor<PupilStatistics> = BehaviorProcessor.create<PupilStatistics>().toSerialized()

}