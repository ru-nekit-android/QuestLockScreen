package ru.nekit.android.domain.qls.useCases

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.CompletableUseCase
import ru.nekit.android.domain.interactor.FlowableUserCase
import ru.nekit.android.domain.interactor.SingleUseCase
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.domain.qls.model.ktPupil
import ru.nekit.android.domain.qls.model.ktQuestStatisticsReport
import ru.nekit.android.domain.qls.repository.IQuestStatisticsReportRepository
import ru.nekit.android.domain.shared.model.ktQuestType
import ru.nekit.android.domain.shared.model.ktQuestionType

class FetchQuestStatisticsReportAsync(private val repository: IQuestStatisticsReportRepository,
                                      scheduler: ISchedulerProvider? = null
) : SingleUseCase<Optional<ktQuestStatisticsReport>, GetParameter>(scheduler) {

    override fun buildUseCase(parameter: GetParameter?): Single<Optional<ktQuestStatisticsReport>> =
            repository.fetchAsyncBy(parameter!!.pupil, parameter.questType, parameter.questionType)

}

class ListenForChangesQuestStatisticsReport(private val repository: IQuestStatisticsReportRepository,
                                            scheduler: ISchedulerProvider? = null
) : FlowableUserCase<Optional<ktQuestStatisticsReport>, GetParameter>(scheduler) {

    override fun buildUseCase(parameter: GetParameter?): Flowable<Optional<ktQuestStatisticsReport>> =
            repository.listenForChanges(parameter!!.pupil, parameter.questType, parameter.questionType)

}

class FetchQuestStatisticsReport(private val repository: IQuestStatisticsReportRepository,
                                 scheduler: ISchedulerProvider? = null
) : SingleUseCase<Optional<ktQuestStatisticsReport>, GetParameter>(scheduler) {

    override fun buildUseCase(parameter: GetParameter?): Single<Optional<ktQuestStatisticsReport>> =
            repository.fetchBy(parameter!!.pupil, parameter.questType, parameter.questionType)
}

open class GetParameter(val pupil: ktPupil, val questType: ktQuestType, val questionType: ktQuestionType)

class CreateOrUpdateQuestStatisticsReport(private val repository: IQuestStatisticsReportRepository,
                                          scheduler: ISchedulerProvider? = null
) : CompletableUseCase<ktQuestStatisticsReport>(scheduler) {

    override fun buildUseCase(parameter: ktQuestStatisticsReport?): Completable =
            repository.updateOrCreate(parameter!!)

}

class FetchAllQuestStatisticsReports(private val repository: IQuestStatisticsReportRepository,
                                     scheduler: ISchedulerProvider? = null
) : SingleUseCase<List<ktQuestStatisticsReport>, ktPupil>(scheduler) {

    override fun buildUseCase(parameter: ktPupil?): Single<List<ktQuestStatisticsReport>> =
            repository.fetchAll(parameter!!)

}