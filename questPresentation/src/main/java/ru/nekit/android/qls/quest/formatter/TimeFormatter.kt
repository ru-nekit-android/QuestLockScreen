package ru.nekit.android.qls.quest.formatter

import ru.nekit.android.qls.domain.model.quest.TimeQuest

object TimeFormatter {

    fun getTimeString(time: TimeQuest): String {
        val index = time.unknownMemberIndex
        val hours = time.getHoursByIndex(index)
        val minutes = time.getMinutesByIndex(index)
        return String.format("%s:%s", hours, if (minutes < 10) "0" + minutes else minutes)
    }

}