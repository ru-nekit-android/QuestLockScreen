package ru.nekit.android.domain.shared.model

import ru.nekit.android.domain.shared.model.ktQuestionType.*
import ru.nekit.android.domain.shared.model.ktQuestionType.Companion.QUESTION_TYPE_BY_DEFAULT

enum class ktQuestType constructor(val supportQuestionTypes: Array<ktQuestionType> = arrayOf(QUESTION_TYPE_BY_DEFAULT),
                                   val defaultQuestionType: ktQuestionType = QUESTION_TYPE_BY_DEFAULT) {

    //16 variants
    SIMPLE_EXAMPLE(ktQuestionType.values()),

    TRAFFIC_LIGHT(arrayOf(SOLUTION)),

    COINS(arrayOf(SOLUTION, UNKNOWN_MEMBER),
            SOLUTION),

    METRICS(arrayOf(SOLUTION, COMPARISON)),

    PERIMETER(arrayOf(SOLUTION, UNKNOWN_MEMBER)),

    FRUIT_ARITHMETIC(arrayOf(SOLUTION, ktQuestionType.COMPARISON),
            SOLUTION),

    TEXT_CAMOUFLAGE,

    TIME(arrayOf(UNKNOWN_MEMBER, COMPARISON),
            UNKNOWN_MEMBER),

    CURRENT_TIME(arrayOf(UNKNOWN_MEMBER),
            UNKNOWN_MEMBER),

    CURRENT_SEASON(arrayOf(UNKNOWN_MEMBER),
            UNKNOWN_MEMBER),

    CHOICE(arrayOf(UNKNOWN_MEMBER),
            UNKNOWN_MEMBER),

    MISMATCH(arrayOf(UNKNOWN_MEMBER),
            UNKNOWN_MEMBER),

    COLORS(arrayOf(UNKNOWN_MEMBER),
            UNKNOWN_MEMBER),

    DIRECTION(arrayOf(UNKNOWN_MEMBER),
            UNKNOWN_MEMBER);

}