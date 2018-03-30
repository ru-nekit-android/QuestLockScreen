package ru.nekit.android.qls.data.representation

import ru.nekit.android.qls.domain.model.StatisticsPeriodType
import ru.nekit.android.questData.R.string.*

object PeriodTypeRepresentationProvider : StringIdRepresentationProvider<StatisticsPeriodType>() {

    init {
        createRepresentation(StatisticsPeriodType.HOURLY, period_hourly_title)
        createRepresentation(StatisticsPeriodType.DAILY, period_daily_title)
        createRepresentation(StatisticsPeriodType.WEEKLY, period_weekly_title)
        createRepresentation(StatisticsPeriodType.MONTHLY, period_monthly_title)
    }
}

fun StatisticsPeriodType.getRepresentation() = PeriodTypeRepresentationProvider.getRepresentation(this)