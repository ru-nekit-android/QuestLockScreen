package ru.nekit.android.qls.domain.model.resources.common

interface IGroupWeightComparisonQuest {

    var groupComparisonType: Int

    companion object {

        val MIN_GROUP_WEIGHT = 0
        val MAX_GROUP_WEIGHT = 1
    }
}
