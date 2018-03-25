package ru.nekit.android.qls.domain.model.quest

import ru.nekit.android.qls.shared.model.QuestionType
import ru.nekit.android.qls.shared.model.QuestionType.COMPARISON
import ru.nekit.android.qls.shared.model.QuestionType.SOLUTION

class MetricsQuest internal constructor(type: QuestionType) : NumberSummandQuest() {

    init {
        questionType = type
    }

    companion object {

        val METRICS_ITEM_COUNT = 3
        val METRICS_CENTIMETER_IN_METER = 100
        val METRICS_CENTIMETER_IN_DECIMETER = 10

        fun convert(quest: Quest): Quest {
            val inQuest = quest as NumberSummandQuest
            if (quest.questionType == SOLUTION) {
                avoidSimpleSolution(inQuest.leftNode)
            } else if (quest.questionType == COMPARISON) {
                avoidSimpleSolution(inQuest.leftNode)
                avoidSimpleSolution(inQuest.rightNode)
            }
            return quest
        }

        private fun avoidSimpleSolution(value: IntArray?) {
            if (value != null && value[0] == 0 && value[1] == 0) {
                val addValue = METRICS_CENTIMETER_IN_DECIMETER
                if (value[2] < addValue) {
                    value[2] += addValue
                }
                value[1] = value[2] / addValue
                value[2] = value[2] % addValue
            }
        }
    }
}