package ru.nekit.android.qls.domain.model.quest

import ru.nekit.android.utils.MathUtils
import ru.nekit.android.utils.TimeUtils
import java.util.Calendar.*

class CurrentTimeQuest(quest: NumberSummandQuest) : TimeQuest(quest) {

    val timeStamp: Long

    override val answer: Int
        get() = unknownMemberIndex

    init {
        leftNode = quest.leftNode
        val length = leftNode.size
        unknownMemberIndex = MathUtils.randUnsignedInt(length - 1)
        timeStamp = TimeUtils.currentTime
        val calendar = getInstance()
        val currentMinutes = calendar.get(MINUTE)
        val currentHours = calendar.get(HOUR)
        for (i in 0 until length) {
            if (i == unknownMemberIndex) {
                leftNode[i] = currentHours * TIME_METRICS + currentMinutes
            } else {
                var currentHoursLocal = (leftNode[i] - leftNode[i] % TIME_METRICS) / TIME_METRICS
                while (currentHoursLocal == currentHours) {
                    currentHoursLocal = MathUtils.randUnsignedInt(12)
                }
                leftNode[i] = currentHoursLocal * TIME_METRICS + currentMinutes
            }
        }
    }

}