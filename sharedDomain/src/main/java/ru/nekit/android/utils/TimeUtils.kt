package ru.nekit.android.utils

import java.util.*

object TimeUtils {

    val currentTime: Long
        get() = System.currentTimeMillis()

    private val calendar: Calendar
        get() = Calendar.getInstance()

    val timestampForStartOfWeek: Long
        get() {
            val cal = calendar
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.clear(Calendar.MINUTE)
            cal.clear(Calendar.SECOND)
            cal.clear(Calendar.MILLISECOND)
            cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
            return cal.timeInMillis
        }

    val timestampForStartOfMonth: Long
        get() {
            val cal = calendar
            cal.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
            cal.clear(Calendar.MINUTE)
            cal.clear(Calendar.SECOND)
            cal.clear(Calendar.MILLISECOND)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            return cal.timeInMillis
        }

    val timestampForStartOfDay: Long
        get() {
            val cal = calendar
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

}
