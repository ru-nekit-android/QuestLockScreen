package ru.nekit.android.qls.domain.providers

import ru.nekit.android.qls.domain.model.PeriodTime
import ru.nekit.android.qls.domain.model.StatisticsPeriodType

interface ITimeProvider {

    fun getCurrentTime(): Long

    fun getTimestampBy(statisticsPeriodType: StatisticsPeriodType): Long

    fun getPeriodIntervalForPeriod(statisticsPeriodTypePair: Pair<StatisticsPeriodType, StatisticsPeriodType>): List<Pair<Long, Long>>
    fun getPeriodTime(periodTime: PeriodTime): Long
}