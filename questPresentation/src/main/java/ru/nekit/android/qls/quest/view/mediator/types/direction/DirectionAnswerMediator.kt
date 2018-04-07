package ru.nekit.android.qls.quest.view.mediator.types.direction

import android.view.View
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.view.mediator.answer.SwipeAnswerMediator
import ru.nekit.android.utils.AnimationUtils

//ver 1.0
class DirectionAnswerMediator : SwipeAnswerMediator() {

    private lateinit var viewHolder: DirectionQuestViewHolder

    override val targetView: View
        get() = viewHolder.targetView

    override val view: View?
        get() = viewHolder.view

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        viewHolder = DirectionQuestViewHolder(questContext)
    }

    override fun onAnswer(answerType: AnswerType): Boolean {
        when (answerType) {
            AnswerType.RIGHT -> {
            }
            AnswerType.WRONG -> AnimationUtils.shake(viewHolder.targetView)
            else -> AnimationUtils.shake(viewHolder.targetView)
        }
        return super.onAnswer(answerType)
    }
}