package ru.nekit.android.qls.domain.model.quest

import ru.nekit.android.qls.domain.model.math.MathematicalOperation
import ru.nekit.android.qls.domain.model.math.MathematicalSignComparison
import ru.nekit.android.qls.shared.model.QuestionType.*
import ru.nekit.android.utils.MathUtils

open class NumberSummandQuest : Quest() {

    /*
                     /(<,=,>)\
                    /         \
                  []           []
            leftNode           rightNode

    Example: 1 + 2 = 3 - 4 -> [1, 2]=[3, -4]
    */

    lateinit var leftNode: IntArray
    lateinit var rightNode: IntArray
    var unknownMemberIndex: Int = 0
    var unknownOperatorIndex: Int = 0

    val leftNodeSum: Int
        get() = leftNode.sum()

    val rightNodeSum: Int
        get() = rightNode.sum()

    val sign: MathematicalSignComparison
        get() {
            val leftNodeSum = MathUtils.sum(leftNode)
            val rightNodeSum = MathUtils.sum(rightNode)
            if (leftNodeSum > rightNodeSum) {
                return MathematicalSignComparison.GREATER
            } else if (leftNodeSum < rightNodeSum) {
                return MathematicalSignComparison.LESS
            }
            return MathematicalSignComparison.EQUAL
        }

    val unknownMember: Int
        get() = leftNode[unknownMemberIndex]

    override val answer: Int
        get() = when (questionType) {
            SOLUTION -> leftNodeSum
            UNKNOWN_MEMBER -> Math.abs(unknownMember)
            else -> 0
        }

    override val answerClass: Class<*>
        get() {
            return when (questionType) {
                UNKNOWN_MEMBER, SOLUTION -> Int::class.java
                COMPARISON -> MathematicalSignComparison::class.java
                UNKNOWN_OPERATION -> MathematicalOperation::class.java
            }
        }
}