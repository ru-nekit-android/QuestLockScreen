package ru.nekit.android.utils

import java.util.*
import kotlin.collections.ArrayList

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

    val weekNumberOfMonth: Int
        get() = calendar.get(Calendar.WEEK_OF_MONTH)

    val firstDayOfWeek: Int
        get() = calendar.firstDayOfWeek

    val dayNumberOfMonth: Int
        get() = calendar.get(Calendar.DAY_OF_MONTH)

    val dayNumberOfWeek: Int
        get() = calendar.get(Calendar.DAY_OF_WEEK)

    val dayCountForMonth: Int
        get() = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

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

    fun firstDayOfWeekInMonth(monthAdd: Int = 0): Int {
        val cal = calendar
        cal.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE)
        cal.clear(Calendar.SECOND)
        cal.clear(Calendar.MILLISECOND)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.add(Calendar.MONTH, monthAdd)
        return cal.get(Calendar.DAY_OF_WEEK)
    }

    fun dayCountInFirstWeekOfMonth(monthAdd: Int = 0): Int {
        val cal = calendar
        val delta = firstDayOfWeekInMonth(monthAdd) - cal.firstDayOfWeek
        return if (delta < 0)
            -delta
        else
            7 - delta
    }

    val weekPeriodsForMonth: List<Pair<Long, Long>>
        get() {
            val cal = calendar
            cal.timeInMillis = timestampForStartOfMonth
            val result = ArrayList<Pair<Long, Long>>()
            val allWeeksOfMonthCount = weekNumberOfMonth
            var allDaysOfMonthCount = dayCountForMonth
            var dayCountOfWeek: Int = dayCountInFirstWeekOfMonth()
            var start: Long
            var end: Long
            for (i in 0 until allWeeksOfMonthCount) {
                start = cal.timeInMillis
                cal.add(Calendar.DAY_OF_MONTH, dayCountOfWeek)
                end = cal.timeInMillis
                result.add(start to end)
                allDaysOfMonthCount -= dayCountOfWeek
                dayCountOfWeek = 7
                if (allDaysOfMonthCount < dayCountOfWeek) {
                    dayCountOfWeek = allDaysOfMonthCount
                }
            }
            return result
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
