package ru.nekit.android.qls.domain.providers

import ru.nekit.android.qls.domain.model.StatisticPeriodType

interface ITimeProvider {

    fun getCurrentTime(): Long

    fun getTimestampBy(statisticPeriodType: StatisticPeriodType): Long

}