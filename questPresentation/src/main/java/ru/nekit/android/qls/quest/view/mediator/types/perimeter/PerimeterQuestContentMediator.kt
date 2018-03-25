package ru.nekit.android.qls.quest.view.mediator.types.perimeter

import android.view.View
import android.widget.EditText
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.types.PerimeterQuest
import ru.nekit.android.qls.quest.view.mediator.content.SimpleContentMediator
import ru.nekit.android.qls.shared.model.QuestionType

//ver 1.0
class PerimeterQuestContentMediator : SimpleContentMediator() {

    private lateinit var _quest: PerimeterQuest
    private lateinit var viewHolder: PerimeterQuestViewHolder

    override val view: View?
        get() = viewHolder.view

    override val answerInput: EditText?
        get() = null

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        _quest = quest as PerimeterQuest
        with(_quest) {
            viewHolder = PerimeterQuestViewHolder(questContext)
            val figureViewLayoutParams = viewHolder.figureView.layoutParams
            if (isSquare) {
                figureViewLayoutParams.width = 100
                figureViewLayoutParams.height = 100
                if (questionType == QuestionType.SOLUTION) {
                    viewHolder.bFigureSideLabel.visibility = View.GONE
                }
            } else {
                if (aSideSize > bSideSize) {
                    figureViewLayoutParams.width = 200
                    figureViewLayoutParams.height = 100
                } else {
                    figureViewLayoutParams.width = 100
                    figureViewLayoutParams.height = 200
                }
            }
            if (questionType == QuestionType.SOLUTION) {
                viewHolder.aFigureSideLabel.text = aSideSize.toString()
                viewHolder.bFigureSideLabel.text = bSideSize.toString()
            } else if (questionType == QuestionType.UNKNOWN_MEMBER) {
                val unknownSideString = questContext.getString(R.string.unknown_side)
                if (isSquare) {
                    viewHolder.aFigureSideLabel.text = unknownSideString
                } else {
                    if (unknownMemberIndex == 0) {
                        viewHolder.aFigureSideLabel.text = unknownSideString
                        viewHolder.bFigureSideLabel.text = bSideSize.toString()
                    } else if (unknownMemberIndex == 1) {
                        viewHolder.bFigureSideLabel.text = unknownSideString
                        viewHolder.aFigureSideLabel.text = aSideSize.toString()
                    }
                }
            }
        }
    }

    override fun includeInLayout(): Boolean {
        return true
    }

    override fun updateSize() {}
}