package ru.nekit.android.domain.qls.useCases

import io.reactivex.Completable
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.CompletableUseCase
import ru.nekit.android.domain.qls.model.ktPupil
import ru.nekit.android.domain.qls.model.ktQuestStatisticsReport
import ru.nekit.android.domain.qls.repository.IQuestStatisticsReportRepository
import ru.nekit.android.domain.shared.model.ktQuestType
import ru.nekit.android.domain.shared.model.ktQuestionType

class MakeRightAnswer(private val repository: IQuestStatisticsReportRepository,
                      scheduler: ISchedulerProvider? = null) : CompletableUseCase<AnswerParameter>(scheduler) {

    override fun buildUseCase(parameter: AnswerParameter?): Completable {
        return FetchQuestStatisticsReportAsync(repository).buildUseCase(parameter)
                .map {
                    val answerTime: Long = parameter!!.sessionTime
                    val report: ktQuestStatisticsReport = it.data ?: ktQuestStatisticsReport(
                            parameter.pupil.uuid,
                            parameter.questType,
                            parameter.questionType,
                            0,
                            0,
                            0,
                            Long.MAX_VALUE,
                            0,
                            0
                    )
                    with(report) {
                        rightAnswerCount++
                        rightAnswerSeriesCounter++
                        bestAnswerTime = Math.min(bestAnswerTime, answerTime)
                        worseAnswerTime = Math.max(worseAnswerTime, answerTime)
                        rightAnswerSummandTime += answerTime
                    }
                    report
                }.flatMapCompletable { CreateOrUpdateQuestStatisticsReport(repository).buildUseCase(it) }
    }
}

class AnswerParameter(pupil: ktPupil, questType: ktQuestType, questionType: ktQuestionType, val sessionTime: Long) : GetParameter(pupil, questType, questionType)
