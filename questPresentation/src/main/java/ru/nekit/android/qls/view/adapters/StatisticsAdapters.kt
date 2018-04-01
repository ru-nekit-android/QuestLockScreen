package ru.nekit.android.qls.view.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.nekit.android.qls.R
import ru.nekit.android.qls.data.representation.getRepresentation
import ru.nekit.android.qls.domain.model.MedalType
import ru.nekit.android.qls.domain.model.Reward
import ru.nekit.android.qls.domain.model.Statistics
import java.text.SimpleDateFormat
import java.util.*

//ver 1.0
class StatisticsAdapter(private val data: List<Statistics>) :
        RecyclerView.Adapter<StatisticsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        return StatisticsViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.ill_statics, parent, false))
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val statistics = data[position]
        val context = holder.titleView.context
        with(holder) {
            val periodName = "${statistics.periodNumber + 1}-ая ${statistics.statisticsPeriodType.getRepresentation().getString(context).toLowerCase()}"
            val title = if (statistics.isCurrentPeriod) "Текущий период ($periodName)" else periodName
            titleView.text = title
            if (statistics.history.isNotEmpty()) {
                val medals: List<Int?> = listOf(statistics.rewardList[Reward.Medal(MedalType.Gold)],
                        statistics.rewardList[Reward.Medal(MedalType.Silver)],
                        statistics.rewardList[Reward.Medal(MedalType.Bronze)]
                )
                var information = "Попыток ответа: ${statistics.answerCount}\n" +
                        "Правильных ответов: ${statistics.rightAnswerCount}\n" +
                        "Процент правильных ответов: ${100 * (statistics.rightAnswerCount.toFloat() / statistics.answerCount)}%\n" +
                        "Количество медалей: ${medals.sumBy {
                            it ?: 0
                        }}\n" +
                        (if ((medals[0] ?: 0) > 0) "Золотых: ${medals[0]}\n" else "") +
                        (if ((medals[1] ?: 0) > 0) "Серебряных: ${medals[1]}\n" else "") +
                        (if ((medals[2] ?: 0) > 0) "Бронзовых: ${medals[2]}\n" else "")
                information += "Среднее время ответа: ${SimpleDateFormat(context.getString(R.string.right_answer_timer_formatter),
                        Locale.getDefault()).format(statistics.averageAnswerTime)}"
                informationView.text = information
            } else {
                informationView.text = "Не играл"
            }
        }
    }

    override fun getItemCount(): Int = data.size

}

class StatisticsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var titleView: TextView = view.findViewById<View>(R.id.tv_title) as TextView
    var informationView: TextView = view.findViewById<View>(R.id.tv_information) as TextView

}