package ru.nekit.android.qls.domain.answerChecker

import ru.nekit.android.qls.domain.answerChecker.common.QuestAnswerChecker
import ru.nekit.android.qls.domain.model.math.MathematicalOperation
import ru.nekit.android.qls.domain.model.math.MathematicalOperation.ADDITION
import ru.nekit.android.qls.domain.model.math.MathematicalOperation.SUBTRACTION
import ru.nekit.android.qls.domain.model.math.MathematicalSignComparison
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.shared.model.QuestionType.COMPARISON
import ru.nekit.android.qls.shared.model.QuestionType.UNKNOWN_OPERATION

class SimpleExampleAnswerChecker : QuestAnswerChecker<NumberSummandQuest>() {

    override fun checkAlternativeInput(quest: NumberSummandQuest, answer: Any): Boolean {
        return when (quest.questionType) {

            COMPARISON -> {
                val sign = answer as MathematicalSignComparison
                quest.sign == sign
            }

            UNKNOWN_OPERATION -> {
                var operation = ADDITION
                val answerOperation = answer as MathematicalOperation
                if (quest.leftNode[quest.unknownOperatorIndex + 1] < 0) {
                    operation = SUBTRACTION
                }
                operation == answerOperation
            }
            else -> false
        }
    }
}
