package ru.nekit.android.qls.quest.types

import android.content.Context

import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.shared.model.QuestType
import ru.nekit.android.qls.shared.model.QuestionType

//ver 1.0
class PerimeterQuest(quest: Quest) : NumberSummandQuest() {

    val isSquare: Boolean
        get() = aSideSize == bSideSize

    val aSideSize: Int
        get() = leftNode[0]

    val bSideSize: Int
        get() = leftNode[1]

    val perimeter: Int
        get() = (aSideSize + bSideSize) * 2

    override val answer: Int
        get() {
            when (questionType) {

                QuestionType.SOLUTION ->

                    return perimeter
            }
            return super.answer
        }

    init {
        questType = QuestType.PERIMETER
        questionType = quest.questionType
        val inQuest = quest as NumberSummandQuest
        val values = IntArray(2)
        values[0] = inQuest.leftNode[0]
        values[1] = inQuest.leftNode[1]
        if (values[1] == 0) {
            values[1] = values[0]
        }
        leftNode = values
        unknownMemberIndex = inQuest.unknownMemberIndex
        unknownOperatorIndex = inQuest.unknownOperatorIndex
        if (quest.availableAnswerVariants != null) {
            availableAnswerVariants = quest.availableAnswerVariants
        }
    }

    fun getFigureName(context: Context): String {
        return context.getString(if (isSquare) R.string.square_figure_name else R.string.rectangle_figure_name)
    }
}