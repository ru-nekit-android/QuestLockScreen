package ru.nekit.android.qls.quest.view.mediator.types.simpleExample

import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.view.mediator.answer.ButtonListAnswerMediator
import ru.nekit.android.qls.shared.model.QuestionType

//ver 1.0
class SimpleExampleAnswerMediator : ButtonListAnswerMediator() {
    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        when (quest.questionType) {

            QuestionType.UNKNOWN_OPERATION, QuestionType.COMPARISON ->

                fillButtonListWithAvailableVariants()
            else -> {
            }
        }
    }
}
