package ru.nekit.android.qls.domain.model

import ru.nekit.android.qls.shared.model.QuestType
import ru.nekit.android.qls.shared.model.QuestionType

data class QuestStatisticsReport(val questAndQuestionType: QuestAndQuestionType,
                                 var rightAnswerCount: Int = 0,
                                 var rightAnswerSeriesCount: Int = 0,
                                 var rightAnswerSeriesCounter: Int = 0,
                                 var wrongAnswerCount: Int = 0,
                                 var wrongAnswerSeriesCounter: Int = 0,
                                 var bestAnswerTime: Long = 0,
                                 var worseAnswerTime: Long = 0,
                                 var rightAnswerSummandTime: Long = 0
)

data class QuestHistory(
        val questAndQuestionType: QuestAndQuestionType,
        val score: Int,
        val lockScreenStartType: LockScreenStartType,
        val answerType: AnswerType,
        val sessionTime: Long,
        var rewards: List<Reward>,
        val recordTypes: Int,
        val levelUp: Boolean,
        val timeStamp: Long
)

data class QuestHistoryCriteria(
        val limitByLastItem: Boolean = false,
        val timestampGreaterThan: Boolean? = null,
        val questType: QuestType? = null,
        val questionType: QuestionType? = null,
        val score: Int? = null,
        val lockScreenStartType: LockScreenStartType? = null,
        val answerType: AnswerType? = null,
        val sessionTime: Long? = null,
        var rewards: List<Reward>? = null,
        val recordTypes: Int? = null,
        val levelUp: Boolean? = null,
        val timestamp: Long? = null
)

data class PupilStatistics(var score: Int)

data class Statistics(
        val periodNumber: Int,
        val statisticsPeriodType: StatisticsPeriodType,
        val periodInterval: Pair<Long, Long>,
        val answerCount: Int = 0,
        val rightAnswerCount: Int = 0,
        val history: List<QuestHistory> = ArrayList(),
        val statisticsByQuestAndQuestionType: Map<QuestAndQuestionType, StatisticsByQuestAndQuestionType> = HashMap(),
        var averageAnswerTime: Long = 0,
        val rewardList: Map<Reward, Int> = HashMap(),
        val isCurrentPeriod: Boolean = false,
        val isReachedPeriod: Boolean = false)

data class StatisticsByQuestAndQuestionType(var history: MutableList<QuestHistory>) {
    var answerCount: Int = 0
    var rightAnswerCount: Int = -1
    var bestAnswerTime: Long = -1
    var averageAnswerTime: Long = -1
    var worseAnswerTime: Long = -1
}

enum class StatisticsPeriodType {

    HOURLY,
    DAILY,
    WEEKLY,
    MONTHLY;

}