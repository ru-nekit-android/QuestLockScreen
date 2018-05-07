package ru.nekit.android.qls.domain.useCases

import io.reactivex.Single
import io.reactivex.functions.Function4
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.ParameterlessSingleUseCase
import ru.nekit.android.domain.interactor.SingleUseCase
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.*
import ru.nekit.android.qls.domain.providers.UseCaseSupport
import ru.nekit.android.qls.domain.repository.DataSourceType
import ru.nekit.android.qls.domain.repository.DataSourceType.LOCAL
import ru.nekit.android.qls.domain.repository.DataSourceType.REMOTE
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.qls.domain.useCases.QuestTrainingProgramUseCases.getPupilAndCurrentLevel
import ru.nekit.android.qls.domain.useCases.QuestTrainingProgramUseCases.getPupilAndStatisticsAndAllLevels
import ru.nekit.android.qls.shared.model.Pupil
import ru.nekit.android.utils.MathUtils

class GetQuestTrainingProgramPriorityRule(private val repository: IRepositoryHolder,
                                          scheduler: ISchedulerProvider? = null) :
        SingleUseCase<Optional<QuestTrainingProgramRulePriority>, QuestAndQuestionType>(scheduler) {

    override fun build(parameter: QuestAndQuestionType): Single<Optional<QuestTrainingProgramRulePriority>> =
            pupilFlatMap {
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
            pupilFlatMap {
                repository.getQuestTrainingProgramRepository().getAllPriorityRule(it.sex!!, it.complexity!!)
            }

}

class GetQuestTrainingProgramRulesByLevelIndexUseCase(private val repository: IRepositoryHolder,
                                                      scheduler: ISchedulerProvider? = null) :
        SingleUseCase<List<QuestTrainingProgramRule>, Int>(scheduler) {

    private val questTrainingProgramRepository = repository.getQuestTrainingProgramRepository()

    override fun build(parameter: Int): Single<List<QuestTrainingProgramRule>> =
            pupilFlatMap { pupil ->
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
            QuestTrainingProgramUseCases.getPupilAndCurrentLevelPair().flatMap {
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
            getPupilAndStatisticsAndAllLevels { pupil, statistics, levels ->
                getPupilAndCurrentLevel(pupil, statistics, levels) { _, currentLevel ->
                    currentLevel
                }
            }

}

class GetBeforeCurrentQuestTrainingProgramLevelAllPointsUseCase(private val repository: IRepositoryHolder,
                                                                scheduler: ISchedulerProvider? = null) : ParameterlessSingleUseCase<Int>(scheduler) {

    override fun build(): Single<Int> =
            getPupilAndStatisticsAndAllLevels { pupil, statistics, levels ->
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


enum class AppropriateType {
    BY_CHANCE,
    BY_RANDOM_CHANCE
}

object QuestTrainingProgramUseCases : UseCaseSupport() {

    private var BASE_CHANCE = 100
    private var START_PRIORITY: Double = 1.0

    private val questTrainingProgramRepository
        get() = repositoryHolder.getQuestTrainingProgramRepository()

    private val pupilStatisticsRepository
        get() = repositoryHolder.getPupilStatisticsRepository()

    private fun createQuestTrainingProgramWithDataSource(type: DataSourceType,
                                                         force: Boolean) = singleUseCase {
        if (type != REMOTE ||
                repositoryHolder.getQuestSetupWizardSettingRepository().useRemoteQTP) {
            pupilFlatMap {
                val sex = it.sex!!
                val complexity = it.complexity!!
                repositoryHolder.getQuestTrainingProgramDataSource(type).create(sex, complexity).flatMap { dataOpt ->
                    if (dataOpt.isNotEmpty())
                        questTrainingProgramRepository.create(dataOpt.nonNullData, sex,
                                complexity,
                                force).doOnEvent { result, _ ->
                            if (result) {
                                //do work on qtp update
                            }
                        }
                    else
                        Single.just(false)
                }
            }
        } else Single.just(false)
    }

    private fun createLocalQuestTrainingProgram(force: Boolean) =
            createQuestTrainingProgramWithDataSource(LOCAL, force)

    private fun createLocalQuestTrainingProgram() =
            createLocalQuestTrainingProgram(false)

    fun forceCreateLocalQuestTrainingProgram() =
            createLocalQuestTrainingProgram(true)

    fun createRemoteQuestTrainingProgram() =
            createQuestTrainingProgramWithDataSource(REMOTE, true)

    fun createQuestTrainingProgram(): Single<Boolean> = createLocalQuestTrainingProgram().buildAsync().doOnSubscribe {
        createRemoteQuestTrainingProgram().use()
    }

    private fun getCurrentQuestTrainingProgramRules() = buildSingleUseCase {
        getPupilAndCurrentLevelPair().flatMap {
            questTrainingProgramRepository.getQuestRules(it.first.sex!!, it.first.complexity!!, it.second)
        }
    }

    fun getVersion(body: (Float) -> Unit) = useSingleUseCase({
        pupilFlatMap {
            questTrainingProgramRepository.getVersion(it.sex!!, it.complexity!!)
        }
    }, body)

    fun getAppropriateQuestAndQuestionType(parameter: AppropriateQuestParameter) = singleUseCase {
        Single.zip(QuestTrainingProgramUseCases.getCurrentQuestTrainingProgramRules(),
                GetCurrentQuestTrainingProgramAllPriorityRule(repositoryHolder).build(),
                GetAllStatisticsReportsUseCase(repositoryHolder).build(),
                GetLastHistoryByLimitUseCase(repositoryHolder).build(
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

    private fun getUnplayedQuestAndQuestionTypes(
            questAndQuestionList: List<QuestAndQuestionType>,
            questStatisticsReportList: List<QuestStatisticsReport>):
            List<QuestAndQuestionType> =
            questAndQuestionList.filter { item ->
                questStatisticsReportList.any {
                    it.questAndQuestionType == item && it.rightAnswerCount == 0
                } || !questStatisticsReportList.any { it.questAndQuestionType == item }
            }

    private fun getExceptedQuestAndQuestionTypeByRightAnswerCount(rightAnswerCountForSkip: Int,
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

    private fun getExceptedQuestAndQuestionTypeByWrongAnswerCount(wrongAnswerCountForSkip: Int,
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

    private fun computeChanceWeight(rightAnswerPriority: Double,
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

    internal fun getPupilAndCurrentLevelPair() =
            getPupilAndStatisticsAndAllLevels { pupil, statistics, levels ->
                getPupilAndCurrentLevel(pupil, statistics, levels) { _, currentLevel ->
                    Pair(pupil, currentLevel)
                }
            }

    internal fun <R> getPupilAndCurrentLevel(pupil: Pupil, statistics: PupilStatistics,
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

    internal fun <R> getPupilAndStatisticsAndAllLevels(body: (Pupil, PupilStatistics, List<QuestTrainingProgramLevel>) -> R):
            Single<R> {
        return pupilFlatMap { pupil ->
            pupilStatisticsRepository.get(pupil).flatMap { statistics ->
                questTrainingProgramRepository.getAllLevels(pupil.sex!!, pupil.complexity!!).flatMap { levels ->
                    Single.fromCallable {
                        body(pupil, statistics, levels)
                    }
                }
            }
        }
    }
}