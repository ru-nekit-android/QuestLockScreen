package ru.nekit.android.qls.view.adapters

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import ru.nekit.android.qls.R
import ru.nekit.android.qls.data.representation.getDrawableRepresentation
import ru.nekit.android.qls.data.representation.getRepresentation
import ru.nekit.android.qls.data.representation.getRepresentationContraction
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
                val medalData: MutableList<Pair<MedalType, Int>> = ArrayList()
                MedalType.Values.get().forEach {
                    val count = statistics.rewardList[Reward.Medal(it)] ?: 0
                    medalData.add(it to count)
                }
                val medalCount = medalData.sumBy {
                    it.second
                }
                val information = "Попыток ответа: ${statistics.answerCount}\n" +
                        "Правильных ответов: ${statistics.rightAnswerCount}\n" +
                        "Процент правильных ответов: ${100 * (statistics.rightAnswerCount.toFloat() / statistics.answerCount)}%\n" +
                        "Среднее время ответа: ${SimpleDateFormat(context.getString(R.string.right_answer_timer_formatter),
                                Locale.getDefault()).format(statistics.averageAnswerTime)}\n" +
                        if (medalCount > 0)
                            "Количество медалей: $medalCount\n"
                        else
                            ""
                val statisticsMedalAdapter = StatisticsMedalAdapter(medalData.filter { it.second > 0 })
                val layoutManager = FlexboxLayoutManager(context)
                layoutManager.flexDirection = FlexDirection.ROW
                layoutManager.justifyContent = JustifyContent.SPACE_AROUND
                layoutManager.alignItems = AlignItems.CENTER
                medalListView.layoutManager = layoutManager
                medalListView.adapter = statisticsMedalAdapter
                informationView.text = information
                statisticsMedalAdapter.notifyDataSetChanged()
            } else {
                informationView.text = "Не играл"
            }
        }
    }

    override fun getItemCount(): Int = data.size

}

class StatisticsMedalAdapter(private val data: List<Pair<MedalType, Int>>) :
        RecyclerView.Adapter<StatisticsMedalItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsMedalItemViewHolder {
        return StatisticsMedalItemViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.ill_statistics_medal_item, parent, false))
    }

    override fun onBindViewHolder(holder: StatisticsMedalItemViewHolder, position: Int) {
        val statisticsMedal = data[position]
        val context = holder.itemView.context
        val medalType = statisticsMedal.first
        holder.medalCount.text = "x${statisticsMedal.second}"
        holder.medalImage.setImageResource(medalType.getDrawableRepresentation().drawableResourceId)
        holder.medalTitle.text = medalType.getRepresentationContraction().getString(context)
    }

    override fun getItemCount(): Int = data.size

}

class StatisticsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val titleView: TextView = view.findViewById(R.id.tv_title)
    val informationView: TextView = view.findViewById(R.id.tv_information)
    val medalListView: RecyclerView = view.findViewById(R.id.list_view_medal)

}

class StatisticsMedalItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val medalImage: AppCompatImageView = view.findViewById(R.id.image_medal)
    val medalTitle: TextView = view.findViewById(R.id.tv_title)
    val medalCount: TextView = view.findViewById(R.id.tv_count)

}