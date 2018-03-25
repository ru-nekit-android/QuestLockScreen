package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.CompletableUseCase
import ru.nekit.android.domain.interactor.ParameterlessSingleUseCase
import ru.nekit.android.domain.interactor.SingleUseCase
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.*
import ru.nekit.android.qls.domain.providers.ITimeProvider
import ru.nekit.android.qls.domain.repository.IRepositoryHolder

class GetCurrentQuestStatisticsReportUseCase(private val repository: IRepositoryHolder,
                                             scheduler: ISchedulerProvider? = null
) : ParameterlessSingleUseCase<QuestStatisticsReport>(scheduler) {

    override fun build(): Single<QuestStatisticsReport> =
            InternalGetCurrentQuestUseCase()
                    .build()
                    .flatMap { quest ->
                        pupil(repository) {
                            repository
                                    .getQuestStatisticsReportRepository()
                                    .getOrCreate(it, quest.questAndQuestionType())
                        }
                    }
}

class SaveStatisticsReportUseCase(private val repository: IRepositoryHolder,
                                  scheduler: ISchedulerProvider? = null
) : CompletableUseCase<QuestStatisticsReport>(scheduler) {

    override fun build(parameter: QuestStatisticsReport): Completable =
            pupilCompletable(repository) {
                repository.getQuestStatisticsReportRepository().save(it, parameter)
            }
}

class AddHistoryUseCase(private val repository: IRepositoryHolder,
                        scheduler: ISchedulerProvider? = null
) : CompletableUseCase<QuestHistory>(scheduler) {

    override fun build(parameter: QuestHistory): Completable =
            pupilCompletable(repository) {
                repository.getQuestHistoryRepository().add(it, parameter)
            }

}

class GetAllStatisticsReportsUseCase(private val repository: IRepositoryHolder,
                                     scheduler: ISchedulerProvider? = null
) : ParameterlessSingleUseCase<List<QuestStatisticsReport>>(scheduler) {

    override fun build(): Single<List<QuestStatisticsReport>> =
            pupil(repository) {
                repository.getQuestStatisticsReportRepository()
                        .getAll(it)
            }

}

class GetLastHistoryByLimitUseCase(private val repository: IRepositoryHolder,
                                   scheduler: ISchedulerProvider? = null
) : SingleUseCase<List<QuestHistory>, Long>(scheduler) {

    override fun build(parameter: Long): Single<List<QuestHistory>> = pupil(repository) {
        repository.getQuestHistoryRepository().getLastHistoryByLimit(it, parameter)
    }

}

class FetchFirstResultableHistoryByCriteriaListUseCase(private val repository: IRepositoryHolder,
                                                       scheduler: ISchedulerProvider? = null
) : SingleUseCase<List<QuestHistory>, List<QuestHistoryCriteria>>(scheduler) {

    override fun build(parameter: List<QuestHistoryCriteria>): Single<List<QuestHistory>> =
            pupil(repository) {
                repository.getQuestHistoryRepository()
                        .getHistoriesByCriteriaList(it, parameter)
            }

}

class GetLastHistoryUseCase(private val repository: IRepositoryHolder,
                            scheduler: ISchedulerProvider? = null
) : ParameterlessSingleUseCase<Optional<QuestHistory>>(scheduler) {

    override fun build(): Single<Optional<QuestHistory>> =
            pupil(repository) {
                repository.getQuestHistoryRepository()
                        .getLastHistoryByLimit(it, 1)
                        .map {
                            Optional(if (it.isNotEmpty()) it[0] else null)
                        }
            }

}

class GetPreviousHistoryWithBestSessionTimeUseCase(private val repository: IRepositoryHolder,
                                                   scheduler: ISchedulerProvider? = null
) : SingleUseCase<Optional<QuestHistory>, QuestAndQuestionType>(scheduler) {

    override fun build(parameter: QuestAndQuestionType): Single<Optional<QuestHistory>> =
            pupil(repository) {
                repository.getQuestHistoryRepository()
                        .getPreviousHistoryItemWithBestSessionTime(it, parameter)
            }

}

class GetHistoryByStatisticsPeriodType(private val repository: IRepositoryHolder,
                                       private val timeProvider: ITimeProvider,
                                       scheduler: ISchedulerProvider? = null) :
        SingleUseCase<List<QuestHistory>, StatisticPeriodType>(scheduler) {

    override fun build(parameter: StatisticPeriodType): Single<List<QuestHistory>> =
            pupil(repository) {
                repository.getQuestHistoryRepository().getHistoryByPeriod(it,
                        timeProvider.getTimestampBy(parameter))
            }
}

class UpdateLastHistoryItemUseCase(private val repository: IRepositoryHolder,
                                   scheduler: ISchedulerProvider? = null
) : CompletableUseCase<QuestHistory>(scheduler) {

    override fun build(parameter: QuestHistory): Completable =
            pupilCompletable(repository) {
                repository.getQuestHistoryRepository().updateLastHistoryItem(it, parameter)
            }

}