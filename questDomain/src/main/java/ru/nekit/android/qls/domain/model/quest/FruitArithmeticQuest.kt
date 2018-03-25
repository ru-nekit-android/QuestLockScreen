package ru.nekit.android.qls.domain.model.quest

import ru.nekit.android.qls.domain.model.resources.common.IGroupWeightComparisonQuest

class FruitArithmeticQuest : VisualRepresentationalNumberSummandQuest(), IGroupWeightComparisonQuest {

    override var groupComparisonType: Int = -1

}