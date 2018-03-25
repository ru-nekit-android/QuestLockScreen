package ru.nekit.android.qls.domain.answerChecker

import ru.nekit.android.qls.domain.answerChecker.common.QuestAnswerChecker
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.domain.model.resources.CoinVisualResourceCollection
import ru.nekit.android.qls.shared.model.QuestionType

class CoinQuestAnswerChecker : QuestAnswerChecker<NumberSummandQuest>() {

    override fun checkAlternativeInput(quest: NumberSummandQuest,
                                       answer: Any): Boolean {
        return when (quest.questionType) {

            QuestionType.UNKNOWN_MEMBER ->
                CoinVisualResourceCollection.getById(quest.answer).nomination ==
                        (answer as CoinVisualResourceCollection).nomination

            else ->
                true
        }
    }

    override fun checkStringInput(quest: NumberSummandQuest, value: String): Boolean =
            Integer.valueOf(value) == quest.leftNode.sumBy {
                CoinVisualResourceCollection.getById(it).nomination
            }

}