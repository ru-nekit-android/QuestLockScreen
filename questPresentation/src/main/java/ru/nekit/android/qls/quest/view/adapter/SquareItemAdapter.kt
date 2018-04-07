package ru.nekit.android.qls.quest.view.adapter

import android.support.annotation.CallSuper
import android.support.v7.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.Subject
import ru.nekit.android.utils.IAutoDispose

//ver 1.0
abstract class SquareItemAdapter<T : RecyclerView.ViewHolder>(protected val answerPublisher: Subject<Any>) :
        RecyclerView.Adapter<T>(), IAutoDispose {

    override var disposableMap: MutableMap<String, CompositeDisposable> = HashMap()

    var size: Int = 0

    @CallSuper
    override fun onBindViewHolder(holder: T, position: Int) {
        holder.itemView.layoutParams.apply {
            height = size
            width = size
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        dispose()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun getItemCount(): Int = 0

}