package ru.nekit.android.qls.domain.answerChecker

import ru.nekit.android.qls.domain.answerChecker.common.AbstractGroupWeightComparisonQuestAnswerChecker
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest

class GroupWeightComparisonQuestAnswerChecker : AbstractGroupWeightComparisonQuestAnswerChecker<NumberSummandQuest>() {

    override fun getGroupList(quest: NumberSummandQuest): List<Int> = quest.leftNode.toList()

}