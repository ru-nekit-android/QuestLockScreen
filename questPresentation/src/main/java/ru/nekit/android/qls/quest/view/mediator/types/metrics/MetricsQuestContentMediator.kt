package ru.nekit.android.qls.quest.view.mediator.types.metrics

import android.view.View
import android.widget.EditText
import ru.nekit.android.qls.domain.model.quest.Quest

import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.formatter.IQuestTextContentFormatter
import ru.nekit.android.qls.quest.formatter.MetricsQuestContentFormatter
import ru.nekit.android.qls.quest.view.mediator.types.simpleExample.SimpleExampleQuestContentMediator

//ver 1.0
class MetricsQuestContentMediator : SimpleExampleQuestContentMediator() {

    override val answerInput: EditText?
        get() = null

    override fun createFormatter(): IQuestTextContentFormatter {
        return MetricsQuestContentFormatter()
    }

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        viewHolder.alternativeAnswerInput.visibility = View.GONE
    }

}
