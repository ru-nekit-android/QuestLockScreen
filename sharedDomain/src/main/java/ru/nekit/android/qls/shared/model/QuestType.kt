package ru.nekit.android.qls.shared.model

import ru.nekit.android.qls.shared.model.QuestionType.*
import ru.nekit.android.qls.shared.model.QuestionType.Companion.QUESTION_TYPE_BY_DEFAULT

enum class QuestType constructor(val supportQuestionTypes: Array<QuestionType> = arrayOf(QUESTION_TYPE_BY_DEFAULT),
                                 val defaultQuestionType: QuestionType = QUESTION_TYPE_BY_DEFAULT) {
    //16 variants
    SIMPLE_EXAMPLE(QuestionType.values()),
    TRAFFIC_LIGHT(SOLUTION),
    COINS(arrayOf(SOLUTION, UNKNOWN_MEMBER), SOLUTION),
    METRICS(arrayOf(SOLUTION, COMPARISON)),
    PERIMETER(arrayOf(SOLUTION, UNKNOWN_MEMBER)),
    FRUIT_ARITHMETIC(arrayOf(SOLUTION, COMPARISON), SOLUTION),
    TEXT_CAMOUFLAGE,
    TIME(arrayOf(UNKNOWN_MEMBER, COMPARISON), UNKNOWN_MEMBER),
    CURRENT_TIME(UNKNOWN_MEMBER),
    CURRENT_SEASON(UNKNOWN_MEMBER),
    CHOICE(UNKNOWN_MEMBER),
    MISMATCH(UNKNOWN_MEMBER),
    COLORS(UNKNOWN_MEMBER),
    DIRECTION(UNKNOWN_MEMBER);

    constructor(defaultQuestionType: QuestionType) : this(arrayOf(defaultQuestionType), defaultQuestionType)

}