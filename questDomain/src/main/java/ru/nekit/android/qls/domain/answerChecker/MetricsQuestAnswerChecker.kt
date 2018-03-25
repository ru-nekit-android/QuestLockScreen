package ru.nekit.android.qls.domain.answerChecker

import ru.nekit.android.qls.domain.answerChecker.common.QuestAnswerChecker
import ru.nekit.android.qls.domain.model.math.MathematicalSignComparison
import ru.nekit.android.qls.domain.model.quest.MetricsQuest
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.shared.model.QuestionType

class MetricsQuestAnswerChecker : QuestAnswerChecker<NumberSummandQuest>() {

    override fun checkAlternativeInput(quest: NumberSummandQuest,
                                       answer: Any): Boolean {
        return answer == when (quest.questionType) {

            QuestionType.COMPARISON -> {

                val leftMetricsSum = quest.leftNodeSum
                val rightMetricsSum = quest.rightNodeSum
                when {
                    leftMetricsSum > rightMetricsSum -> MathematicalSignComparison.GREATER
                    leftMetricsSum < rightMetricsSum -> MathematicalSignComparison.LESS
                    else -> MathematicalSignComparison.EQUAL
                }
            }

            else -> null

        }
    }

    private fun getMetricsSum(node: IntArray): Int {
        val converterValues = intArrayOf(MetricsQuest.METRICS_CENTIMETER_IN_METER,
                MetricsQuest.METRICS_CENTIMETER_IN_DECIMETER,
                1)
        return (0 until MetricsQuest.METRICS_ITEM_COUNT).sumBy { converterValues[it] * node[it] }
    }

    override fun checkStringInput(quest: NumberSummandQuest, value: String): Boolean {
        return when (quest.questionType) {
            QuestionType.SOLUTION -> getMetricsSum(quest.leftNode) == Integer.valueOf(value)
            else -> false
        }
    }
}