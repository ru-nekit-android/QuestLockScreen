package ru.nekit.android.qls.quest.view.mediator.types.direction

import android.view.View
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.view.mediator.answer.SwipeAnswerMediator
import ru.nekit.android.qls.utils.AnimationUtils
import ru.nekit.android.qls.window.AnswerWindow
import ru.nekit.android.qls.window.AnswerWindow.Type

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
            AnswerType.RIGHT -> AnswerWindow.open(questContext, Type.RIGHT,
                    R.style.Window_RightAnswer_Simple,
                    R.layout.wc_right_answer_simple_content,
                    R.layout.wc_right_answer_tool_simple_content)
            AnswerType.WRONG -> {
                AnimationUtils.shake(viewHolder.targetView)
                AnswerWindow.open(questContext, Type.WRONG,
                        R.style.Window_WrongAnswer_Simple,
                        R.layout.wc_wrong_answer_simple_content,
                        R.layout.wc_wrong_answer_tool_simple_content)
            }
            else -> {
                AnimationUtils.shake(viewHolder.targetView)
            }
        }
        return false
    }
}