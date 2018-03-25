package ru.nekit.android.qls.quest.view.mediator.types.metrics

import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.answer.MetricsQuestAnswerVariantAdapter
import ru.nekit.android.qls.quest.view.mediator.answer.ButtonListAnswerMediator
import ru.nekit.android.qls.shared.model.QuestionType

class MetricsAnswerMediator : ButtonListAnswerMediator(MetricsQuestAnswerVariantAdapter()) {
    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        when (quest.questionType) {
            QuestionType.COMPARISON ->
                fillButtonListWithAvailableVariants()
            else -> {
            }
        }
    }
}
