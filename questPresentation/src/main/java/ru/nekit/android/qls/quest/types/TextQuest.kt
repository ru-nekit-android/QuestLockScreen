package ru.nekit.android.qls.quest.types

import ru.nekit.android.qls.domain.model.quest.Quest

class TextQuest : Quest() {

    var questionStringArray: Array<String>? = null
    private var answerStringArray: Array<String>? = null

    override val answer: String
        get() = answerString

    val questionString: String
        get() {
            val questionString = StringBuilder()
            var answerItemPosition = 0
            for (questionItem in questionStringArray!!) {
                if ("" == questionItem) {
                    questionString.append(answerStringArray!![answerItemPosition])
                    answerItemPosition++
                } else {
                    questionString.append(questionItem)
                }
            }
            return questionString.toString()
        }

    private val answerString: String
        get() {
            val answerString = StringBuilder()
            for (answerItem in answerStringArray!!) {
                answerString.append(answerItem)
            }
            return answerString.toString()
        }

    override val answerClass: Class<*>
        get() = String::class.java

    fun setAnswer(answer: String) {
        answerStringArray = arrayOf(answer)
    }

}