package ru.nekit.android.qls.domain.useCases

import io.reactivex.Single
import io.reactivex.functions.Function4
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.ParameterlessSingleUseCase
import ru.nekit.android.domain.interactor.SingleUseCase
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.*
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.qls.domain.useCases.QuestTrainingProgramUseCasesHelper.BASE_CHANCE
import ru.nekit.android.qls.domain.useCases.QuestTrainingProgramUseCasesHelper.START_PRIORITY
import ru.nekit.android.qls.domain.useCases.QuestTrainingProgramUseCasesHelper.computeChanceWeight
import ru.nekit.android.qls.domain.useCases.QuestTrainingProgramUseCasesHelper.getExceptedQuestAndQuestionTypeByRightAnswerCount
import ru.nekit.android.qls.domain.useCases.QuestTrainingProgramUseCasesHelper.getExceptedQuestAndQuestionTypeByWrongAnswerCount
import ru.nekit.android.qls.domain.useCases.QuestTrainingProgramUseCasesHelper.getPupilAndCurrentLevel
import ru.nekit.android.qls.domain.useCases.QuestTrainingProgramUseCasesHelper.getPupilAndStatisticsAndAllLevels
import ru.nekit.android.qls.domain.useCases.QuestTrainingProgramUseCasesHelper.getUnplayedQuestAndQuestionTypes
import ru.nekit.android.qls.shared.model.Pupil
import ru.nekit.android.utils.MathUtils

private class InternalCreateQuestTrainingProgramUseCase(private val repository: IRepositoryHolder,
                                                        scheduler: ISchedulerProvider? = null) :
        SingleUseCase<Boolean, Boolean>(scheduler) {

    override fun build(parameter: Boolean): Single<Boolean> = pupil(repository) {
        repository.getQuestTrainingProgramRepository().create(it.sex!!, it.complexity!!, parameter).doOnEvent { result, _ ->
            if (result) {
                //do work on qtp update
            }
        }
    }

}

class CreateQuestTrainingProgramUseCase(private val repository: IRepositoryHolder,
                                        scheduler: ISchedulerProvider? = null) :
        ParameterlessSingleUseCase<Boolean>(scheduler) {
    override fun build(): Single<Boolean> = InternalCreateQuestTrainingProgramUseCase(repository).build(false)
}

class ForceCreateQuestTrainingProgramUseCase(private val repository: IRepositoryHolder,
                                             scheduler: ISchedulerProvider? = null) :
        ParameterlessSingleUseCase<Boolean>(scheduler) {
    override fun build(): Single<Boolean> = InternalCreateQuestTrainingProgramUseCase(repository).build(true)
}

class GetQuestTrainingProgramPriorityRule(private val repository: IRepositoryHolder,
                                          scheduler: ISchedulerProvider? = null) :
        SingleUseCase<Optional<QuestTrainingProgramRulePriority>, QuestAndQuestionType>(scheduler) {

    override fun build(parameter: QuestAndQuestionType): Single<Optional<QuestTrainingProgramRulePriority>> =
            pupil(repository) {
                repository.getQuestTrainingProgramRepository().getPriorityRule(
                        it.sex!!,
                        it.complexity!!,
                        parameter.questType,
                        parameter.questionType
                )
            }

}

class GetCurrentQuestTrainingProgramAllPriorityRule(private val repository: IRepositoryHolder,
                                                    scheduler: ISchedulerProvider? = null) :
        ParameterlessSingleUseCase<List<QuestTrainingProgramRulePriority>>(scheduler) {

    override fun build(): Single<List<QuestTrainingProgramRulePriority>> =
            pupil(repository) {
                repository.getQuestTrainingProgramRepository().getAllPriorityRule(it.sex!!, it.complexity!!)
            }

}

class GetQuestTrainingProgramRulesByLevelIndexUseCase(private val repository: IRepositoryHolder,
                                                      scheduler: ISchedulerProvider? = null) :
        SingleUseCase<List<QuestTrainingProgramRule>, Int>(scheduler) {

    private val questTrainingProgramRepository = repository.getQuestTrainingProgramRepository()

    override fun build(parameter: Int): Single<List<QuestTrainingProgramRule>> =
            pupil(repository) { pupil ->
                questTrainingProgramRepository.getLevel(pupil.sex!!, pupil.complexity!!, parameter).flatMap {
                    if (it.isEmpty())
                        Single.just(ArrayList())
                    else
                        questTrainingProgramRepository.getQuestRules(pupil.sex!!,
                                pupil.complexity!!,
                                it.nonNullData)
                }
            }

}

class GetQuestTrainingProgramRuleByQuestAndQuestionType(private val repository: IRepositoryHolder,
                                                        scheduler: ISchedulerProvider? = null) :
        SingleUseCase<Optional<QuestTrainingProgramRule>, QuestAndQuestionType>(scheduler) {

    private val questTrainingProgramRepository = repository.getQuestTrainingProgramRepository()

    override fun build(parameter: QuestAndQuestionType): Single<Optional<QuestTrainingProgramRule>> =
            QuestTrainingProgramUseCasesHelper.getPupilAndCurrentLevelPair(repository).flatMap {
                questTrainingProgramRepository.getQuestRule(it.first.sex!!,
                        it.first.complexity!!,
                        it.second,
                        parameter.questType,
                        parameter.questionType)
            }.map { it }
}

class GetCurrentQuestTrainingProgramRule(private val repository: IRepositoryHolder,
                                         scheduler: ISchedulerProvider? = null) :
        ParameterlessSingleUseCase<QuestTrainingProgramRule>(scheduler) {

    override fun build(): Single<QuestTrainingProgramRule> =
            GetCurrentQuestUseCase()
                    .build()
                    .flatMap { quest ->
                        GetQuestTrainingProgramRuleByQuestAndQuestionType(repository)
                                .build(quest.questAndQuestionType())
                                .map { it.data }
                    }
}


class GetQuestTrainingProgramRulesByLevelUseCase(private val repository: IRepositoryHolder,
                                                 scheduler: ISchedulerProvider? = null) :
        SingleUseCase<List<QuestTrainingProgramRule>, QuestTrainingProgramLevel>(scheduler) {

    override fun build(parameter: QuestTrainingProgramLevel): Single<List<QuestTrainingProgramRule>> =
            GetQuestTrainingProgramRulesByLevelIndexUseCase(repository).build(parameter.index)

}

class GetCurrentQuestTrainingProgramLevelUseCase(private val repository: IRepositoryHolder,
                                                 scheduler: ISchedulerProvider? = null) :
        ParameterlessSingleUseCase<QuestTrainingProgramLevel>(scheduler) {

    override fun build(): Single<QuestTrainingProgramLevel> =
            getPupilAndStatisticsAndAllLevels(repository) { pupil, statistics, levels ->
                getPupilAndCurrentLevel(pupil, statistics, levels) { _, currentLevel ->
                    currentLevel
                }
            }

}

class GetCurrentQuestTrainingProgramRulesUseCase(private val repository: IRepositoryHolder,
                                                 scheduler: ISchedulerProvider? = null) :
        ParameterlessSingleUseCase<List<QuestTrainingProgramRule>>(scheduler) {

    override fun build(): Single<List<QuestTrainingProgramRule>> =
            QuestTrainingProgramUseCasesHelper.getPupilAndCurrentLevelPair(repository).flatMap {
                repository.getQuestTrainingProgramRepository().getQuestRules(it.first.sex!!, it.first.complexity!!, it.second)
            }

}

class GetBeforeCurrentQuestTrainingProgramLevelAllPointsUseCase(private val repository: IRepositoryHolder,
                                                                scheduler: ISchedulerProvider? = null) : ParameterlessSingleUseCase<Int>(scheduler) {

    override fun build(): Single<Int> =
            getPupilAndStatisticsAndAllLevels(repository) { pupil, statistics, levels ->
                getPupilAndCurrentLevel(pupil, statistics, levels) { _, currentLevel ->
                    var pointsWeight = 0
                    if (currentLevel.index > 0) {
                        for (level in levels) {
                            pointsWeight += level.pointsWeight
                            if (level.index == currentLevel.index - 1) {
                                break
                            }
                        }
                    }
                    pointsWeight
                }
            }

}

data class AppropriateQuestParameter(val appropriateType: AppropriateType,
                                     val rightAnswerCountForSkip: Int,
                                     val wrongAnswerCountForSkip: Int,
                                     val highestPriorityForUnplayedQuests: Boolean)

class GetAppropriateQuestAndQuestionType(private val repository: IRepositoryHolder,
                                         scheduler: ISchedulerProvider? = null) :
        SingleUseCase<QuestAndQuestionType, AppropriateQuestParameter>(scheduler) {

    override fun build(parameter: AppropriateQuestParameter): Single<QuestAndQuestionType> =
            Single.zip(GetCurrentQuestTrainingProgramRulesUseCase(repository).build(),
                    GetCurrentQuestTrainingProgramAllPriorityRule(repository).build(),
                    GetAllStatisticsReportsUseCase(repository).build(),
                    GetLastHistoryByLimitUseCase(repository).build(
                            Math.max(parameter.rightAnswerCountForSkip,
                                    parameter.wrongAnswerCountForSkip).toLong() + 1),
                    Function4<List<QuestTrainingProgramRule>,
                            List<QuestTrainingProgramRulePriority>,
                            List<QuestStatisticsReport>,
                            List<QuestHistory>,
                            QuestAndQuestionType> { qtpRuleList,
                                                    qtpRulePriorityList,
                                                    questStatisticsReportList,
                                                    questHistoryList ->

                        val questAndQuestionList: MutableList<QuestAndQuestionType> = ArrayList()
                        qtpRuleList.flatMapTo(questAndQuestionList) { item ->
                            item.questionTypes.flatMap { listOf(item.questType + it) }
                        }
                        var result: QuestAndQuestionType? = null
                        if (parameter.highestPriorityForUnplayedQuests) {
                            val unplayedList = getUnplayedQuestAndQuestionTypes(
                                    questAndQuestionList, questStatisticsReportList)
                            if (unplayedList.isNotEmpty())
                                result = MathUtils.randItem(unplayedList)
                        }
                        if (result == null) {
                            val exceptQuestAndQuestionTypeList: MutableList<QuestAndQuestionType> = ArrayList()
                            if (parameter.rightAnswerCountForSkip > 0)
                                getExceptedQuestAndQuestionTypeByRightAnswerCount(
                                        parameter.rightAnswerCountForSkip, questHistoryList)?.let {
                                    exceptQuestAndQuestionTypeList.add(it)
                                }
                            if (parameter.wrongAnswerCountForSkip > 0)
                                getExceptedQuestAndQuestionTypeByWrongAnswerCount(
                                        parameter.wrongAnswerCountForSkip, questHistoryList)?.let {
                                    exceptQuestAndQuestionTypeList.add(it)
                                }

                            val filteredQuestAndQuestionList: List<QuestAndQuestionType> =
                                    if (exceptQuestAndQuestionTypeList.size != questAndQuestionList.size &&
                                            exceptQuestAndQuestionTypeList.isNotEmpty())
                                        questAndQuestionList.filter { item ->
                                            exceptQuestAndQuestionTypeList.firstOrNull {
                                                it == item
                                            } == null
                                        } else questAndQuestionList

                            val appropriateList = filteredQuestAndQuestionList.map { AppropriateQuestAndQuestionType(it) }
                            appropriateList.forEach { appropriateItem ->
                                appropriateItem.chanceValue = (questStatisticsReportList.find {
                                    it.questAndQuestionType == appropriateItem.questAndQuestionType
                                }?.let { report ->
                                            val wrongAnswerCount = report.wrongAnswerCount
                                            val rightAnswerCount = report.rightAnswerCount
                                            val rightAnswerSeriesCounter = report.rightAnswerSeriesCounter
                                            val searchPriorityRule = qtpRulePriorityList.find {
                                                it.questType == it.questType
                                                        && it.questionTypes.contains(
                                                        report.questAndQuestionType.questionType)
                                            }
                                            computeChanceWeight(
                                                    searchPriorityRule?.startPriority
                                                            ?: START_PRIORITY,
                                                    searchPriorityRule?.wrongAnswerPriority
                                                            ?: START_PRIORITY,
                                                    rightAnswerCount,
                                                    wrongAnswerCount,
                                                    rightAnswerSeriesCounter,
                                                    questStatisticsReportList
                                                            .map { it.rightAnswerSeriesCounter }
                                                            .max()
                                                            ?: 0
                                            )
                                        } ?: START_PRIORITY) * BASE_CHANCE
                            }
                            result = if (appropriateList.size == 1)
                                appropriateList[0].questAndQuestionType
                            else {
                                when (parameter.appropriateType) {

                                    AppropriateType.BY_CHANCE ->
                                        appropriateList.sortedBy { it.chanceValue }.reversed()[0]

                                    AppropriateType.BY_RANDOM_CHANCE -> {
                                        var totalChanceWeight = 0.0
                                        appropriateList.forEach { appropriateItem ->
                                            appropriateItem.lowerValue = totalChanceWeight
                                            totalChanceWeight += appropriateItem.chanceValue
                                            appropriateItem.upperValue = totalChanceWeight - 1
                                        }
                                        val randomValue = Math.random() % totalChanceWeight * totalChanceWeight
                                        appropriateList.find {
                                            randomValue >= it.lowerValue && randomValue <= it.upperValue
                                        }
                                    }
                                }?.questAndQuestionType
                            }
                            result = result ?: filteredQuestAndQuestionList[0]
                        }
                        result
                    }
            )
}

enum class AppropriateType {
    BY_CHANCE,
    BY_RANDOM_CHANCE
}

private object QuestTrainingProgramUseCasesHelper {

    internal var BASE_CHANCE = 100
    internal var START_PRIORITY: Double = 1.0

    fun getUnplayedQuestAndQuestionTypes(
            questAndQuestionList: List<QuestAndQuestionType>,
            questStatisticsReportList: List<QuestStatisticsReport>):
            List<QuestAndQuestionType> =
            questAndQuestionList.filter { item ->
                questStatisticsReportList.any {
                    it.questAndQuestionType == item && it.rightAnswerCount == 0
                } || !questStatisticsReportList.any { it.questAndQuestionType == item }
            }

    fun getExceptedQuestAndQuestionTypeByRightAnswerCount(rightAnswerCountForSkip: Int,
                                                          questHistoryList: List<QuestHistory>):
            QuestAndQuestionType? = if (questHistoryList.isNotEmpty())
        if (questHistoryList.size >= rightAnswerCountForSkip) {
            val lastQuestHistory = questHistoryList.last()
            if (questHistoryList.all {
                        it.questAndQuestionType == lastQuestHistory.questAndQuestionType &&
                                it.answerType == AnswerType.RIGHT
                    }) lastQuestHistory.questAndQuestionType
            else null
        } else null
    else null

    fun getExceptedQuestAndQuestionTypeByWrongAnswerCount(wrongAnswerCountForSkip: Int,
                                                          questHistoryList: List<QuestHistory>):
            QuestAndQuestionType? {
        val localQuestHistoryList = questHistoryList.toMutableList()
        return if (localQuestHistoryList.isNotEmpty()) {
            val lastQuestHistory = localQuestHistoryList.last()
            val lastQuestHistoryIsRightAnswer = lastQuestHistory.answerType == AnswerType.RIGHT
            if (lastQuestHistoryIsRightAnswer) {
                localQuestHistoryList.remove(lastQuestHistory)
            }
            if (localQuestHistoryList.size >= wrongAnswerCountForSkip)
            //lastHistoryItem is right answer
                if (localQuestHistoryList.all {
                            it.questAndQuestionType == lastQuestHistory.questAndQuestionType &&
                                    it.answerType == AnswerType.WRONG
                        }) lastQuestHistory.questAndQuestionType
                else null
            else null
        } else null
    }

    fun computeChanceWeight(rightAnswerPriority: Double,
                            wrongAnswerPriority: Double,
                            rightAnswerCount: Int,
                            wrongAnswerCount: Int,
                            rightAnswerSeriesCounter: Int,
                            maxRightAnswerSeriesCounter: Int): Double =
            if (wrongAnswerCount == 0) {
                rightAnswerPriority * (1 - rightAnswerSeriesCounter.toDouble() /
                        Math.max(1, maxRightAnswerSeriesCounter))
            } else {
                (rightAnswerPriority + wrongAnswerPriority * wrongAnswerCount /
                        Math.pow(Math.max(1, rightAnswerCount).toDouble(), 2.0))
            }

    fun getPupilAndCurrentLevelPair(repository: IRepositoryHolder) =
            getPupilAndStatisticsAndAllLevels(repository) { pupil, statistics, levels ->
                getPupilAndCurrentLevel(pupil, statistics, levels) { _, currentLevel ->
                    Pair(pupil, currentLevel)
                }
            }

    fun <R> getPupilAndCurrentLevel(pupil: Pupil, statistics: PupilStatistics,
                                    levels: List<QuestTrainingProgramLevel>, body:
                                    (Pupil, QuestTrainingProgramLevel) -> R): R {
        var pointsWeight = 0
        var currentLevel: QuestTrainingProgramLevel = levels[0]
        for (level in levels) {
            currentLevel = level
            pointsWeight += level.pointsWeight
            if (pointsWeight >= statistics.score) {
                currentLevel = level
                break
            }
        }
        return body(pupil, currentLevel)
    }

    fun <R> getPupilAndStatisticsAndAllLevels(repository: IRepositoryHolder, body: (Pupil, PupilStatistics, List<QuestTrainingProgramLevel>) -> R):
            Single<R> {
        return pupil(repository) { pupil ->
            repository.getPupilStatisticsRepository().get(pupil).flatMap { statistics ->
                repository.getQuestTrainingProgramRepository().getAllLevels(pupil.sex!!, pupil.complexity!!).flatMap { levels ->
                    Single.fromCallable {
                        body(pupil, statistics, levels)
                    }
                }
            }
        }
    }
}