package ru.nekit.android.qls.quest.view.mediator.types.simpleExample

import android.view.View
import android.view.View.GONE
import android.widget.EditText
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.formatter.IQuestTextContentFormatter
import ru.nekit.android.qls.quest.formatter.TextQuestContentFormatter
import ru.nekit.android.qls.quest.types.TextQuest
import ru.nekit.android.qls.quest.view.mediator.content.SimpleContentMediator
import ru.nekit.android.qls.shared.model.QuestionType

//ver 1.0
open class SimpleExampleQuestContentMediator : SimpleContentMediator() {

    private lateinit var _quest: TextQuest
    protected lateinit var viewHolder: SimpleExampleQuestViewHolder

    override val view: View?
        get() = viewHolder.view

    override val answerInput: EditText?
        get() = viewHolder.alternativeAnswerInput

    protected open fun createFormatter(): IQuestTextContentFormatter {
        return TextQuestContentFormatter()
    }

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        viewHolder = SimpleExampleQuestViewHolder(questContext)
        val formatter = createFormatter()
        _quest = quest as TextQuest
        with(_quest) {
            val questStringList = formatter.format(questContext, this)
            when (questionType) {

                QuestionType.SOLUTION -> {

                    viewHolder.rightSideView.visibility = GONE
                    viewHolder.leftSideView.text = questStringList[0]
                    viewHolder.alternativeAnswerInput.hint = formatter.missedCharacter
                }

                QuestionType.UNKNOWN_MEMBER -> {

                    viewHolder.rightSideView.text = questStringList[0]
                    viewHolder.leftSideView.text = questStringList[1]
                    viewHolder.alternativeAnswerInput.hint = formatter.missedCharacter
                }

                QuestionType.COMPARISON -> {

                    viewHolder.alternativeAnswerInput.visibility = GONE
                    viewHolder.rightSideView.visibility = GONE
                    viewHolder.leftSideView.text = questStringList[0]
                }

                QuestionType.UNKNOWN_OPERATION -> {

                    viewHolder.alternativeAnswerInput.visibility = GONE
                    viewHolder.rightSideView.visibility = GONE
                    viewHolder.leftSideView.text = questStringList[0]
                }
            }
        }
    }


    override fun includeInLayout(): Boolean {
        return true
    }

    override fun updateSize() {

    }

}
