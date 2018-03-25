package ru.nekit.android.qls.domain.answerChecker.common

import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.model.resources.common.IGroupWeightComparisonQuest
import java.util.*

abstract class AbstractGroupWeightComparisonQuestAnswerChecker<in R : Quest> : IAnswerChecker<R> {

    protected abstract fun getGroupList(quest: R): List<Any>

    override fun checkAlternativeInput(quest: R, answer: Any): Boolean {
        val inQuest = quest as IGroupWeightComparisonQuest
        val map = HashMap<Any, Int>()
        val groupList = getGroupList(quest)
        val length = groupList.size
        var i = 0
        while (i < length) {
            val key = groupList[i]
            val value: Int = map[key] ?: 0
            map[key] = value + 1
            i++
        }
        val isMax = inQuest.groupComparisonType == IGroupWeightComparisonQuest.MAX_GROUP_WEIGHT
        var value = if (isMax) 0 else Integer.MAX_VALUE
        for ((_, item) in map) {
            value = if (isMax) Math.max(value, item) else Math.min(value, item)
        }
        return map[answer] == value
    }

    override fun checkStringInputFormat(quest: R, value: String): Boolean {
        return false
    }

    override fun checkStringInput(quest: R, value: String): Boolean {
        return false
    }

}