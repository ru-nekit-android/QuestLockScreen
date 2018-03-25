package ru.nekit.android.qls.domain.answerChecker.common

import ru.nekit.android.qls.domain.model.quest.Quest

open class QuestAnswerChecker<in R : Quest> : IAnswerChecker<R> {

    override fun checkAlternativeInput(quest: R, answer: Any): Boolean {
        return quest.answer == answer
    }

    override fun checkStringInputFormat(quest: R, value: String): Boolean {
        var result = false
        val answerClass = quest.answerClass
        try {
            if (answerClass == Int::class.java) {
                Integer.parseInt(value)
                result = true
            } else if (answerClass == Double::class.java) {
                java.lang.Double.parseDouble(value)
                result = true
            }
        } catch (ignored: NumberFormatException) {
        }
        if (answerClass == String::class.java) {
            result = true
        }
        return result
    }

    override fun checkStringInput(quest: R, value: String): Boolean {
        val answerClass = quest.answerClass
        return when (answerClass) {
            Int::class.java -> quest.answer == Integer.valueOf(value)
            Double::class.java -> quest.answer == java.lang.Double.valueOf(value)
            String::class.java -> (quest.answer as String).toLowerCase() == value.toLowerCase()
            else -> false
        }
    }
}