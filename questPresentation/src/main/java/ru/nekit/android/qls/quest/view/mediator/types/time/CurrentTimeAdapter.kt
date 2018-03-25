package ru.nekit.android.qls.quest.view.mediator.types.time

import io.reactivex.subjects.Subject
import ru.nekit.android.qls.domain.model.quest.CurrentTimeQuest
import ru.nekit.android.utils.TimeUtils

//ver 1.0
class CurrentTimeAdapter(private val quest: CurrentTimeQuest, listData: List<Int>,
                         answerPublisher: Subject<Any>) : TimeAdapter(listData, answerPublisher) {

    override fun getTime(position: Int): Int {
        val delta = ((TimeUtils.currentTime - quest.timeStamp) / 1000 / 60).toInt()
        return quest.leftNode[position] + delta
    }

    override fun onBindViewHolder(holder: TimeAdapter.TimeViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.view.tag = position
    }
}