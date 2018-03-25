package ru.nekit.android.qls.domain.model.quest

import ru.nekit.android.qls.domain.model.resources.common.IGroupWeightComparisonQuest
import ru.nekit.android.qls.domain.model.resources.common.IGroupWeightComparisonQuest.Companion.MAX_GROUP_WEIGHT
import ru.nekit.android.qls.shared.model.QuestionType.COMPARISON
import ru.nekit.android.qls.shared.model.QuestionType.UNKNOWN_MEMBER
import ru.nekit.android.utils.MathUtils.randInt
import ru.nekit.android.utils.MathUtils.randUnsignedInt

open class TimeQuest(quest: NumberSummandQuest) : NumberSummandQuest(), IGroupWeightComparisonQuest {

    final override var groupComparisonType: Int = -1

    val unknownTime: Int
        get() = unknownMember

    val unknownTimeHours: Int
        get() = getHoursByIndex(unknownMemberIndex)

    val unknownTimeMinutes: Int
        get() = getMinutesByIndex(unknownMemberIndex)

    override val answer: Int
        get() {
            if (questionType == COMPARISON) {
                return (if (groupComparisonType == MAX_GROUP_WEIGHT)
                    leftNode.max()
                else
                    leftNode.min())!!
            }
            return super.answer
        }

    init {
        leftNode = quest.leftNode
        val length = leftNode.size
        if (quest.questionType == UNKNOWN_MEMBER) {
            unknownMemberIndex = randUnsignedInt(length - 1)
        } else if (quest.questionType == COMPARISON) {
            groupComparisonType = randInt(IGroupWeightComparisonQuest.MIN_GROUP_WEIGHT,
                    MAX_GROUP_WEIGHT)
        }
    }

    fun getHoursByIndex(index: Int): Int {
        val time = leftNode[index]
        return (time - time % TIME_METRICS) / TIME_METRICS
    }

    fun getMinutesByIndex(index: Int): Int {
        val time = leftNode[index]
        return time - getHoursByIndex(index) * TIME_METRICS
    }

    companion object {
        fun getTimeHours(time: Int): Int {
            return (time - time % TIME_METRICS) / TIME_METRICS
        }

        fun getTimeMinutes(time: Int): Int {
            return time - getTimeHours(time) * TIME_METRICS
        }

        val TIME_METRICS = 60

    }
}