package ru.nekit.android.qls.domain.answerChecker

import ru.nekit.android.qls.domain.answerChecker.common.QuestAnswerChecker
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.domain.model.resources.TrafficLightResourceCollection.Companion.getById
import ru.nekit.android.qls.shared.model.QuestionType

class TrafficLightQuestAnswerChecker : QuestAnswerChecker<NumberSummandQuest>() {

    override fun checkAlternativeInput(quest: NumberSummandQuest, answer: Any): Boolean {
        return when (quest.questionType) {
            QuestionType.SOLUTION -> getById(quest.answer) == getById(answer as Int)
            else -> false
        }
    }
}
