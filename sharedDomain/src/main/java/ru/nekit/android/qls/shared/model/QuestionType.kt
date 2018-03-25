package ru.nekit.android.qls.shared.model

enum class QuestionType {

    SOLUTION,
    UNKNOWN_MEMBER,
    UNKNOWN_OPERATION,
    COMPARISON;


    companion object {

        val QUESTION_TYPE_BY_DEFAULT = SOLUTION
    }

}