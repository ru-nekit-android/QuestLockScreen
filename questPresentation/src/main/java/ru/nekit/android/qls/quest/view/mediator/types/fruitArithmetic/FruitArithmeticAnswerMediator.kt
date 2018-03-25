package ru.nekit.android.qls.quest.view.mediator.types.fruitArithmetic

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.view.mediator.answer.ButtonListAnswerMediator
import ru.nekit.android.qls.shared.model.QuestionType

//ver 1.0
class FruitArithmeticAnswerMediator : ButtonListAnswerMediator() {

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        when (quest.questionType) {

            QuestionType.SOLUTION ->

                fillButtonListWithAvailableVariants(true)
            else -> {
            }
        }
    }

    override fun onAnswer(answerType: AnswerType): Boolean {
        if (answerType == AnswerType.WRONG) {
            refillButtonListWithAvailableVariants(true)
        }
        return super.onAnswer(answerType)
    }

    override fun createButton(label: String, tag: Any,
                              layoutParams: LinearLayout.LayoutParams, isFirst: Boolean, isLast: Boolean): View {
        val button = LayoutInflater.from(questContext).inflate(R.layout.button_fruit_arithmetic, null)
        button.setBackgroundResource(R.drawable.background_button_green)
        val margin = questContext.resources.getDimensionPixelSize(R.dimen.big_gap) / 4
        layoutParams.setMargins(if (isFirst) 0 else margin, margin, if (isLast) 0 else margin, margin)
        return button
    }
}