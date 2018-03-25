package ru.nekit.android.qls.domain.model

import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.shared.model.QuestType
import ru.nekit.android.qls.shared.model.QuestionType

open class QuestAndQuestionType(val questType: QuestType, val questionType: QuestionType) {

    override fun equals(other: Any?): Boolean = (other as QuestAndQuestionType).let {
        questType == it.questType && questionType == it.questionType
    }

    override fun hashCode(): Int =
            31 * questType.hashCode() + questionType.hashCode()
}

fun Quest.questAndQuestionType(): QuestAndQuestionType = questType + questionType

infix operator fun QuestType.plus(that: QuestionType): QuestAndQuestionType = QuestAndQuestionType(this, that)
infix operator fun QuestionType.plus(that: QuestType): QuestAndQuestionType = QuestAndQuestionType(that, this)
