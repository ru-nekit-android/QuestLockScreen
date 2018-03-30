package ru.nekit.android.qls.domain.quest.generator

import ru.nekit.android.qls.domain.model.math.MathematicalOperation
import ru.nekit.android.qls.domain.model.math.MathematicalSignComparison
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.shared.model.QuestionType
import ru.nekit.android.qls.shared.model.QuestionType.*
import ru.nekit.android.utils.MathUtils.*
import java.util.*

class NumberSummandQuestGenerator(questionType: QuestionType) :
        IQuestGenerator<NumberSummandQuest> {

    private val quest: NumberSummandQuest = NumberSummandQuest()
    private var memberMaxValue: List<Int>? = null
    private var memberMinValue: List<Int>? = null
    private var zeroChance: List<Int>? = null
    private var availableValueArray: MutableList<Int>? = null
    private var eachMemberMinMaxValue1: Array<IntArray>? = null
    private var flags: Int = 0
    private var leftNodeEachItemTransformFunction: ((Int) -> Int)? = null

    init {
        quest.questionType = questionType
    }

    fun setMemberCounts(leftNodeMemberCount: Int, rightNodeMemberCount: Int) {
        quest.leftNode = IntArray(leftNodeMemberCount)
        quest.rightNode = IntArray(rightNodeMemberCount)
    }

    fun setMemberCounts(memberCounts: IntArray) {
        setMemberCounts(memberCounts[0], memberCounts[1])
    }

    fun setLeftNodeMembersZeroValueChance(zeroChance: List<Int>) {
        this.zeroChance = zeroChance
    }

    fun setFlags(flags: Int) {
        this.flags = flags
    }

    fun setFlags(vararg flags: Flag) {
        this.flags = 0
        for (flag in flags) {
            this.flags = this.flags or flag.value()
        }
    }

    private fun setMembersMinAndMaxValues(memberMinValue: List<Int>, memberMaxValue: List<Int>) {
        this.memberMinValue = memberMinValue
        this.memberMaxValue = memberMaxValue
    }

    fun setMembersMinAndMaxValues(memberMinAndMaxValue: List<List<Int>>) {
        setMembersMinAndMaxValues(memberMinAndMaxValue[0], memberMinAndMaxValue[1])
    }

    fun setEachMemberMinAndMaxValues(eachMemberMinMaxValue: Array<IntArray>) {
        eachMemberMinMaxValue1 = eachMemberMinMaxValue
    }

    private fun <T> setAvailableMemberValues(availableValues: Array<T>, mapper: (T) -> Int) {
        availableValueArray = ArrayList()
        for (availableValue in availableValues) {
            availableValueArray!! += mapper(availableValue)
        }
    }

    fun setAvailableMemberValues(availableValues: List<Int>) {
        availableValueArray = availableValues.toMutableList()
    }

    fun <T : Enum<*>> setAvailableMemberValues(availableValues: Array<T>) {
        setAvailableMemberValues(availableValues) { value -> value.ordinal }
    }

    private fun generateArrayRandomAvailableValues(quest: NumberSummandQuest) {
        for (i in 0 until quest.leftNode.size) {
            quest.leftNode[i] = availableValueArray!![randUnsignedInt(availableValueArray!!.size - 1)]
        }
    }

    private fun getMemberMinValue(nodeIndex: Int, position: Int): Int {
        var memberMinValue = 0
        if (this.memberMinValue != null) {
            memberMinValue = this.memberMinValue!![nodeIndex]
        } else if (eachMemberMinMaxValue1 != null) {
            memberMinValue = eachMemberMinMaxValue1!![position][0]
        }
        return memberMinValue
    }

    private fun getMemberMaxValue(nodeIndex: Int, position: Int): Int {
        var memberMaxValue = 0
        if (this.memberMaxValue != null) {
            memberMaxValue = this.memberMaxValue!![nodeIndex]
        } else if (eachMemberMinMaxValue1 != null) {
            memberMaxValue = eachMemberMinMaxValue1!![position][1]
        }
        return memberMaxValue
    }

    private fun generateArrayRandomValuesInRange(quest: NumberSummandQuest, nodeIndex: Int) {
        val memberArray = if (nodeIndex == LEFT_NODE_INDEX) quest.leftNode else quest.rightNode
        val memberCount = memberArray.size
        val memberRandomValues: IntArray
        if (nodeIndex == LEFT_NODE_INDEX) {
            quest.leftNode = IntArray(memberCount)
            memberRandomValues = quest.leftNode
        } else {
            quest.rightNode = IntArray(memberCount)
            memberRandomValues = quest.rightNode
        }
        var sumValue: Int
        for (i in 0 until memberCount) {
            val randomValue = randInt(getMemberMinValue(nodeIndex, i),
                    getMemberMaxValue(nodeIndex, i))
            if (flags and Flag.ONLY_POSITIVE_SUMMANDS.value() != 0) {
                memberRandomValues[i] = Math.abs(randomValue)
            } else {
                memberRandomValues[i] = (if (randBoolean()) -1 else 1) * randomValue
            }
            if (zeroChance != null) {
                memberRandomValues[i] = (if (randUnsignedInt(100) < zeroChance!![i])
                    0
                else
                    1) * memberRandomValues[i]
            }
            sumValue = sum(memberRandomValues)
            if ((flags and Flag.AVOID_NEGATIVE_ANSWER_WHILE_CALCULATION.value()) != 0 && sumValue < 0
                    || (flags and Flag.AVOID_ZERO_ANSWER_WHILE_CALCULATION.value()) != 0 && sumValue == 0) {
                memberRandomValues[i] = -memberRandomValues[i]
            }
        }
        sumValue = sum(memberRandomValues)
        if (flags and Flag.AVOID_NEGATIVE_ANSWER.value() != 0 && sumValue < 0 || flags and Flag.AVOID_ZERO_ANSWER.value() != 0 && sumValue == 0) {
            for (i in 0 until memberCount) {
                memberRandomValues[i] = -memberRandomValues[i]
            }
            for (i in 0 until memberCount) {
                if (memberRandomValues[i] > 0) {
                    if (getMemberMaxValue(nodeIndex, i) > memberRandomValues[i]) {
                        memberRandomValues[i]++
                    }
                    break
                }
            }
        }
        if (flags and Flag.POSITIVE_FIRST_SUMMAND.value() != 0) {
            memberRandomValues[0] = Math.abs(memberRandomValues[0])
        }
    }

    private fun generateRightNodeEqualsLeftNode(quest: NumberSummandQuest) {
        if (quest.rightNode.isNotEmpty()) {
            generateArrayRandomValuesInRange(quest, RIGHT_NODE_INDEX)
            val rightMembersCount = quest.rightNode.size
            val differenceValue = quest.leftNodeSum - quest.rightNodeSum
            var residueValue = Math.abs(differenceValue)
            if (differenceValue != 0) {
                for (i in 0 until rightMembersCount) {
                    if (residueValue != 0) {
                        var randomValue = Math.max(1, Math.min(residueValue, randInt(0,
                                if (differenceValue > 0)
                                    getMemberMaxValue(RIGHT_NODE_INDEX, i) - Math.abs(quest.rightNode[i])
                                else
                                    Math.abs(quest.rightNode[i]))))
                        if (differenceValue > 0) {
                            quest.rightNode[i] += randomValue
                        } else {
                            quest.rightNode[i] -= randomValue
                            if (quest.rightNode[i] == 0) {
                                quest.rightNode[i]++
                                randomValue--
                            }
                        }
                        residueValue -= randomValue
                    } else {
                        return
                    }
                }
                if (residueValue != 0) {
                    if (differenceValue > 0) {
                        quest.rightNode[rightMembersCount - 1] += residueValue
                    } else {
                        quest.rightNode[rightMembersCount - 1] -= residueValue
                    }
                }
                if (flags and Flag.ONLY_POSITIVE_SUMMANDS.value() != 0) {
                    for (i in 0 until rightMembersCount) {
                        quest.rightNode[i] = Math.abs(quest.rightNode[i])
                    }
                }
            }
        }
    }

    override fun generate(): NumberSummandQuest {
        if (availableValueArray == null) {
            generateArrayRandomValuesInRange(quest, LEFT_NODE_INDEX)
            when (quest.questionType) {

                COMPARISON -> {
                    if (randPositiveInt(100) >= CHANCE_EQUALITY_FOR_COMPARISON) {
                        generateArrayRandomValuesInRange(quest, RIGHT_NODE_INDEX)
                    } else {
                        generateRightNodeEqualsLeftNode(quest)
                    }
                    val availableAnswerVariants = ArrayList<MathematicalSignComparison>()
                    Collections.addAll(availableAnswerVariants, *MathematicalSignComparison.values())
                    quest.availableAnswerVariants = availableAnswerVariants
                }

                UNKNOWN_MEMBER -> {

                    generateRightNodeEqualsLeftNode(quest)
                    quest.unknownMemberIndex = randUnsignedInt(quest.leftNode.size - 1)
                }

                UNKNOWN_OPERATION -> {
                    generateRightNodeEqualsLeftNode(quest)
                    quest.unknownOperatorIndex = randUnsignedInt(quest.leftNode.size - 2)
                    val availableAnswerVariants = ArrayList<MathematicalOperation>()
                    availableAnswerVariants.add(MathematicalOperation.ADDITION)
                    availableAnswerVariants.add(MathematicalOperation.SUBTRACTION)
                    quest.availableAnswerVariants = availableAnswerVariants
                }
            }
        } else {
            quest.availableAnswerVariants = availableValueArray
            generateArrayRandomAvailableValues(quest)
            when (quest.questionType) {

                UNKNOWN_MEMBER ->

                    quest.unknownMemberIndex = randUnsignedInt(quest.leftNode.size - 1)

                UNKNOWN_OPERATION, COMPARISON -> {
                }
            }
        }
        if (leftNodeEachItemTransformFunction != null) {
            quest.leftNode.map { value ->
                leftNodeEachItemTransformFunction!!(value)
            }
        }
        return quest
    }

    fun leftNodeEachItemTransformationFunction(value: (Int) -> Int) {
        leftNodeEachItemTransformFunction = value
    }

    enum class Flag {

        POSITIVE_FIRST_SUMMAND,
        AVOID_NEGATIVE_ANSWER,
        AVOID_NEGATIVE_ANSWER_WHILE_CALCULATION,
        AVOID_ZERO_ANSWER,
        AVOID_ZERO_ANSWER_WHILE_CALCULATION,
        ONLY_POSITIVE_SUMMANDS;

        fun value(): Int {
            return Math.pow(2.0, ordinal.toDouble()).toInt()
        }

    }

    companion object {

        private val CHANCE_EQUALITY_FOR_COMPARISON = 25
        private val LEFT_NODE_INDEX = 0
        private val RIGHT_NODE_INDEX = 1
    }
}