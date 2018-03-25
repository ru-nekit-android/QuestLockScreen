package ru.nekit.android.qls.domain.model.quest

import ru.nekit.android.qls.shared.model.QuestType
import ru.nekit.android.qls.shared.model.QuestionType

abstract class Quest {

    lateinit var questType: QuestType
    lateinit var questionType: QuestionType
    @Transient
    var availableAnswerVariants: List<Any>? = null
    abstract val answer: Any
    abstract val answerClass: Class<*>

}