package ru.nekit.android.qls.domain.providers

import ru.nekit.android.qls.domain.model.StatisticsPeriodType
import ru.nekit.android.qls.domain.useCases.PeriodTime

interface ITimeProvider {

    fun getCurrentTime(): Long

    fun getTimestampBy(statisticsPeriodType: StatisticsPeriodType): Long

    fun getPeriodIntervalForPeriod(statisticsPeriodTypePair: Pair<StatisticsPeriodType, StatisticsPeriodType>): List<Pair<Long, Long>>
    fun getPeriodTime(periodTime: PeriodTime): Long
}