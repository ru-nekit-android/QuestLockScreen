package ru.nekit.android.domain.shared.model

enum class ktQuestionType {

    SOLUTION,
    UNKNOWN_MEMBER,
    UNKNOWN_OPERATION,
    COMPARISON;


    companion object {

        val QUESTION_TYPE_BY_DEFAULT = SOLUTION
    }

}