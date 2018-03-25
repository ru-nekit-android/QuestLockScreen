package ru.nekit.android.qls.domain.answerChecker.common

import ru.nekit.android.qls.domain.model.quest.Quest

interface IAnswerChecker<in R : Quest> {

    fun checkAlternativeInput(quest: R, answer: Any): Boolean

    fun checkStringInputFormat(quest: R, value: String): Boolean

    fun checkStringInput(quest: R, value: String): Boolean

}
