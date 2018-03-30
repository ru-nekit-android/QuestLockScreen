package ru.nekit.android.qls.quest.view.mediator.types.time

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.subjects.Subject

import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.quest.TimeQuest
import ru.nekit.android.qls.quest.view.adapter.SquareItemAdapter

//ver 1.0
open class TimeAdapter(private val timeListData: List<Int>, answerPublisher: Subject<Any>) :
        SquareItemAdapter<TimeAdapter.TimeViewHolder>(answerPublisher) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        return TimeViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.ill_time, parent, false))
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val time = getTime(position)
        val hour = TimeQuest.getTimeHours(time)
        val minute = TimeQuest.getTimeMinutes(time)
        holder.apply {
            hourHand.rotation = (30 * hour + minute / 2).toFloat()
            minuteHand.rotation = (6 * minute).toFloat()
            autoDispose {
                view.clicks().map { view.tag }.subscribe({ answerPublisher.onNext(it) })
            }
            view.tag = time
        }
    }

    protected open fun getTime(position: Int): Int = timeListData[position]

    override fun getItemCount(): Int = timeListData.size

    class TimeViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val hourHand: View = view.findViewById(R.id.hour_hand)
        val minuteHand: View = view.findViewById(R.id.minute_hand)
    }
}