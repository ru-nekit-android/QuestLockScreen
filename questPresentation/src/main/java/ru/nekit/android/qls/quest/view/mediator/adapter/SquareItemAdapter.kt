package ru.nekit.android.qls.quest.view.mediator.adapter

import android.support.annotation.CallSuper
import android.support.v7.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.Subject
import ru.nekit.android.qls.utils.IAutoDispose

//ver 1.0
abstract class SquareItemAdapter<T : RecyclerView.ViewHolder>(protected val answerPublisher: Subject<Any>) :
        RecyclerView.Adapter<T>(), IAutoDispose {

    override var disposable: CompositeDisposable = CompositeDisposable()

    var size: Int = 0

    @CallSuper
    override fun onBindViewHolder(holder: T, position: Int) {
        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = size
        layoutParams.width = size
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        dispose()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun getItemCount(): Int {
        return 0
    }

}