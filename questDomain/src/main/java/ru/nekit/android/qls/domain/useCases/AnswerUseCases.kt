package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.CompletableUseCase
import ru.nekit.android.domain.interactor.ParameterlessCompletableUseCase
import ru.nekit.android.domain.interactor.SingleUseCase
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.answerChecker.*
import ru.nekit.android.qls.domain.answerChecker.common.IAnswerChecker
import ru.nekit.android.qls.domain.answerChecker.common.QuestAnswerChecker
import ru.nekit.android.qls.domain.model.*
import ru.nekit.android.qls.domain.model.QuestState.*
import ru.nekit.android.qls.domain.model.RecordType.RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD
import ru.nekit.android.qls.domain.model.RecordType.RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD
import ru.nekit.android.qls.domain.model.quest.FruitArithmeticQuest
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.model.quest.TimeQuest
import ru.nekit.android.qls.domain.providers.ITimeProvider
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.qls.domain.useCases.QuestHolder.quest
import ru.nekit.android.qls.shared.model.QuestType.*
import ru.nekit.android.qls.shared.model.QuestionType
import ru.nekit.android.utils.doIfOrComplete
import ru.nekit.android.utils.doIfOrNever

private class UpdateStatisticsReportOnRightAnswerUseCase(private val repository: IRepositoryHolder,
                                                         private val scheduler: ISchedulerProvider? = null) :
        CompletableUseCase<AnswerParameter>(scheduler) {

    override fun build(parameter: AnswerParameter): Completable =
            Single.zip(GetCurrentQuestStatisticsReportUseCase(repository)
                    .build(),
                    LockScreenUseCases.getLastStartType(),
                    BiFunction<QuestStatisticsReport,
                            Optional<LockScreenStartType>,
                            Pair<QuestStatisticsReport, QuestHistory>> { report, startTypeOptional ->
                        val startType = startTypeOptional.nonNullData
                        val sessionTime: Long = SessionTimer.getTime(repository.getSettingsRepository(), parameter.currentTime)
                        var rightAnswerSeriesLengthUpdated = false
                        var recordType = 0
                        with(report) {
                            rightAnswerCount++
                            rightAnswerSummandTime += sessionTime
                            rightAnswerSeriesCounter++
                            val maxRightAnswerSeriesCount = Math.max(rightAnswerSeriesCount, rightAnswerSeriesCounter)
                            rightAnswerSeriesLengthUpdated = maxRightAnswerSeriesCount > 1 && maxRightAnswerSeriesCount > rightAnswerSeriesCount
                            rightAnswerSeriesCount = maxRightAnswerSeriesCount
                            if (bestAnswerTime == 0L) {
                                bestAnswerTime = sessionTime
                            }
                            if (sessionTime < bestAnswerTime) {
                                recordType = recordType or RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD.value
                                bestAnswerTime = sessionTime
                            }
                            worseAnswerTime = Math.max(worseAnswerTime, sessionTime)
                            wrongAnswerSeriesCounter = 0
                        }
                        if (rightAnswerSeriesLengthUpdated) {
                            recordType = recordType or RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD.value
                        }
                        Pair(report,
                                QuestHistory(parameter.questAndQuestionType,
                                        parameter.score,
                                        startType,
                                        AnswerType.RIGHT,
                                        sessionTime,
                                        ArrayList(),
                                        recordType,
                                        parameter.levelUp,
                                        parameter.currentTime))
                    }).flatMapCompletable { reportAndHistory ->
                val report = reportAndHistory.first
                val history = reportAndHistory.second
                SaveStatisticsReportUseCase(repository).build(report)
                        .concatWith(AddHistoryUseCase(repository).build(history)
                                .andThen(GetReachedRewardsUseCase(repository, scheduler)
                                        .build(report.questAndQuestionType)
                                        .flatMapCompletable { rewards ->
                                            UpdateLastHistoryItemUseCase(repository)
                                                    .build(history.apply {
                                                        this.rewards = rewards
                                                    })
                                                    .andThen(Flowable.fromIterable(rewards)
                                                            .flatMapCompletable {
                                                                AddRewardUseCase(repository)
                                                                        .build(it)
                                                            }
                                                    ).concatWith(Completable.fromCallable {
                                                        RewardHolder.getAndNotify(null, repository, scheduler)
                                                    }.doIfOrComplete { rewards.isNotEmpty() })
                                        }.concatWith(LockScreenUseCases.updateLastStartType())
                                )
                        )
            }
}

private class UpdateStatisticsReportOnWrongAnswerUseCase(private val repository: IRepositoryHolder,
                                                         scheduler: ISchedulerProvider? = null) :
        CompletableUseCase<AnswerParameter>(scheduler) {
    override fun build(parameter: AnswerParameter): Completable =
            Single.zip(GetCurrentQuestStatisticsReportUseCase(repository).build(),
                    LockScreenUseCases.getLastStartType(),
                    BiFunction<QuestStatisticsReport,
                            Optional<LockScreenStartType>,
                            Pair<QuestStatisticsReport, QuestHistory>> { report, lockScreenStartTypeOptional ->
                        val sessionTime: Long = SessionTimer.getTime(
                                repository.getSettingsRepository(),
                                parameter.currentTime)
                        val lockScreenStartType = lockScreenStartTypeOptional.nonNullData
                        with(report) {
                            wrongAnswerCount++
                            wrongAnswerSeriesCounter++
                            rightAnswerSeriesCounter = 0
                        }
                        Pair(report,
                                QuestHistory(parameter.questAndQuestionType,
                                        0,
                                        lockScreenStartType,
                                        AnswerType.WRONG,
                                        sessionTime,
                                        ArrayList(),
                                        0,
                                        false,
                                        parameter.currentTime))
                    }).flatMapCompletable { reportAndHistory ->
                val report = reportAndHistory.first
                val history = reportAndHistory.second
                SaveStatisticsReportUseCase(repository).build(report).concatWith(
                        AddHistoryUseCase(repository).build(history)
                )
            }
}

private data class AnswerParameter(val questAndQuestionType: QuestAndQuestionType,
                                   val currentTime: Long, val score: Int = 0, val levelUp: Boolean = false)

private object ScoreHelper {

    fun get(rule: QuestTrainingProgramRule, currentLevel: QuestTrainingProgramLevel): Int =
            (rule.reward * currentLevel.pointsMultiplier).toInt()

}

class RightAnswerUseCase(private val repository: IRepositoryHolder,
                         private val timeProvider: ITimeProvider,
                         private val scheduler: ISchedulerProvider? = null) :
        ParameterlessCompletableUseCase(scheduler) {
    private val questStateRepository = repository.getQuestStateRepository()

    override fun build(): Completable = Single.fromCallable {
        SessionTimer.stop()
        timeProvider.getCurrentTime()
    }.flatMapCompletable { currentTime ->
        Single.zip(
                GetCurrentQuestTrainingProgramLevelUseCase(repository).build(),
                GetCurrentQuestTrainingProgramRule(repository).build(),
                InternalGetCurrentPupilStatisticsUseCase(repository).build(),
                Function3<QuestTrainingProgramLevel,
                        QuestTrainingProgramRule,
                        PupilStatistics,
                        Pair<PupilStatistics, QuestTrainingProgramLevel>> { currentLevel,
                                                                            rule,
                                                                            statistics ->
                    statistics.score += ScoreHelper.get(rule, currentLevel)
                    Pair(statistics, currentLevel)
                }).flatMap { pair ->
            Single.zip(
                    UpdateCurrentPupilStatisticsUseCase(repository).build(pair.first).toSingleDefault(true),
                    GetCurrentQuestTrainingProgramLevelUseCase(repository).build(),
                    GetCurrentQuestTrainingProgramRule(repository).build(),
                    Function3<Boolean, QuestTrainingProgramLevel, QuestTrainingProgramRule, AnswerParameter> { _, currentLevel, rule ->
                        val isLevelUp = currentLevel.index > pair.second.index
                        AnswerParameter(
                                quest!!.questAndQuestionType(),
                                currentTime, ScoreHelper.get(rule, currentLevel), isLevelUp)
                    })
        }.flatMapCompletable { answer ->
            UpdateStatisticsReportOnRightAnswerUseCase(repository, scheduler).build(answer)
        }.concatWith(questStateRepository.has(ANSWERED).flatMapCompletable {
            questStateRepository.replace(PLAYED, ANSWERED).doIfOrNever { !it }
        })
                .concatWith(pupilFlatMapCompletable {
                    repository.getQuestRepository().clear(it)
                })
                .concatWith(questStateRepository.clear())
                .concatWith(TransitionChoreographUseCases.generateNextTransition())
    }
}

private class WrongAnswerUseCase(private val repository: IRepositoryHolder,
                                 private val timeProvider: ITimeProvider,
                                 scheduler: ISchedulerProvider? = null) :
        ParameterlessCompletableUseCase(scheduler) {
    override fun build(): Completable = GetCurrentQuestUseCase().build().map { quest ->
        SessionTimer.stop()
        AnswerParameter(quest.questAndQuestionType(), timeProvider.getCurrentTime())
    }.flatMapCompletable { answer ->
        UpdateStatisticsReportOnWrongAnswerUseCase(repository).build(answer)
    }
}

private class EmptyAnswerUseCase(private val repository: IRepositoryHolder,
                                 scheduler: ISchedulerProvider? = null) :
        ParameterlessCompletableUseCase(scheduler) {

    override fun build(): Completable = Completable.fromRunnable {

    }

}

private class WrongStringInputFormatUseCase(private val repository: IRepositoryHolder,
                                            scheduler: ISchedulerProvider? = null) :
        ParameterlessCompletableUseCase(scheduler) {

    override fun build(): Completable = Completable.fromRunnable {

    }

}

class AnswerCallbackUseCase(private val repository: IRepositoryHolder,
                            private val timeProvider: ITimeProvider,
                            private val scheduler: ISchedulerProvider? = null) :
        SingleUseCase<AnswerType, Any?>(scheduler) {

    override fun build(parameter: Any?): Single<AnswerType> =
            Single.zip(QuestHasStateUseCase(repository).build(PAUSED),
                    QuestHasStateUseCase(repository).build(ANSWERED),
                    BiFunction<Boolean, Boolean, Boolean> { b0, b1 ->
                        b0 || b1
                    })
                    .flatMap {
                        if (!it) {
                            GetCurrentQuestUseCase().build().flatMap { quest ->
                                Single.fromCallable {
                                    if (parameter == null || parameter is String && parameter.isEmpty())
                                        AnswerType.EMPTY
                                    else {
                                        @Suppress("UNCHECKED_CAST")
                                        val answerChecker: IAnswerChecker<Quest> = when (quest.questType) {
                                            COINS -> CoinQuestAnswerChecker()
                                            SIMPLE_EXAMPLE -> SimpleExampleAnswerChecker()
                                            METRICS -> MetricsQuestAnswerChecker()
                                            PERIMETER -> QuestAnswerChecker<NumberSummandQuest>()
                                            TRAFFIC_LIGHT -> TrafficLightQuestAnswerChecker()
                                            FRUIT_ARITHMETIC -> {
                                                val isSolution = quest.questionType == QuestionType.SOLUTION
                                                if (isSolution)
                                                    QuestAnswerChecker<FruitArithmeticQuest>()
                                                else
                                                    GroupWeightComparisonQuestAnswerChecker()
                                            }
                                            TIME -> QuestAnswerChecker<TimeQuest>()
                                            CURRENT_TIME -> QuestAnswerChecker<TimeQuest>()
                                            CHOICE, MISMATCH, CURRENT_SEASON ->
                                                QuestAnswerChecker<NumberSummandQuest>()
                                            COLORS -> QuestAnswerChecker<NumberSummandQuest>()
                                            DIRECTION -> QuestAnswerChecker<NumberSummandQuest>()
                                            else -> throw NotImplementedError()
                                        /*QuestType.TEXT_CAMOUFLAGE -> {QuestAnswerChecker<TextQuest>()}*/
                                        } as IAnswerChecker<Quest>
                                        if (parameter is String && !answerChecker.checkStringInputFormat(quest, parameter)) {
                                            AnswerType.WRONG_INPUT_FORMAT
                                        } else {
                                            val checkResult = when (parameter) {
                                                is String -> answerChecker.checkStringInput(quest, parameter)
                                                else -> answerChecker.checkAlternativeInput(quest, parameter)
                                            }
                                            if (checkResult) AnswerType.RIGHT else AnswerType.WRONG
                                        }
                                    }
                                }
                            }
                        } else
                            Single.never()
                    }.flatMap {
                        (when (it) {
                            AnswerType.RIGHT -> RightAnswerUseCase(repository,
                                    timeProvider, scheduler)
                            AnswerType.WRONG -> WrongAnswerUseCase(repository,
                                    timeProvider, scheduler)
                            AnswerType.EMPTY -> EmptyAnswerUseCase(repository)
                            AnswerType.WRONG_INPUT_FORMAT ->
                                WrongStringInputFormatUseCase(repository, scheduler)
                        }).build().toSingleDefault(it)
                    }
}