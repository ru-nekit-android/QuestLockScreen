package ru.nekit.android.qls.quest.view.adapter

import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.Subject
import ru.nekit.android.qls.R
import ru.nekit.android.qls.quest.view.mediator.IContentContainerViewHolder

//ver 1.1
class SquareImageAdapter(@param:LayoutRes @field:LayoutRes
                         private val imageLayoutResId: Int,
                         private val dataList: List<*>,
                         @field:DrawableRes
                         private val imageResourceIdList: List<Int>,
                         answerPublisher: Subject<Any>) :
        SquareItemAdapter<SquareImageAdapter.ImageViewHolder>(answerPublisher) {

    override var disposableMap: MutableMap<String, CompositeDisposable> = HashMap()

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.apply {
            itemView.apply {
                autoDispose {
                    clicks().map { itemView.tag }.subscribe({ answerPublisher.onNext(it) })
                }
                tag = dataList[position]
            }
            imageView.setImageResource(imageResourceIdList[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
            ImageViewHolder(LayoutInflater.from(parent.context)
                    .inflate(imageLayoutResId, parent, false))

    override fun getItemCount(): Int = imageResourceIdList.size

    class ImageViewHolder internal constructor(val view: View) : RecyclerView.ViewHolder(view),
            IContentContainerViewHolder {
        val imageView: ImageView = view.findViewById(R.id.view_image)
        override val contentContainer: View = view.findViewById(R.id.container_content)
    }
}