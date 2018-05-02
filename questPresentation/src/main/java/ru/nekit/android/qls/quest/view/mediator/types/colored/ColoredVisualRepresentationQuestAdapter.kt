package ru.nekit.android.qls.quest.view.mediator.types.colored

import android.support.annotation.ColorInt
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.subjects.Subject
import ru.nekit.android.qls.R
import ru.nekit.android.qls.data.representation.common.ColorfullVisualResourceStruct.ColorType.*
import ru.nekit.android.qls.data.representation.common.IColorfullVisualResourceProvider
import ru.nekit.android.qls.data.representation.common.PairColorStruct
import ru.nekit.android.qls.data.representation.getRepresentation
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.view.adapter.SquareItemAdapter

class ColoredVisualRepresentationQuestAdapter internal constructor(private val questContext: QuestContext,
                                                                   private val listData: List<Pair<IColorfullVisualResourceProvider, PairColorStruct>>,
                                                                   answerPublisher: Subject<Any>) :
        SquareItemAdapter<ColoredVisualRepresentationQuestAdapter.ViewHolder>(answerPublisher) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.ill_colored_visual_representation, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val resources = questContext.resources
        val item = listData[position]
        for (colorfullQuestVisualResourceStruct in item.first.coloredVisualResourceList) {
            val drawableResourceId = colorfullQuestVisualResourceStruct.drawableResourceId
            if (drawableResourceId != 0) {
                val imageView = AppCompatImageView(questContext)
                var drawable = ResourcesCompat.getDrawable(resources,
                        colorfullQuestVisualResourceStruct.drawableResourceId, null)
                if (colorfullQuestVisualResourceStruct.colorType != NONE) {
                    drawable = DrawableCompat.wrap(drawable!!.mutate())
                    val coloredItem = item.second
                    @ColorInt
                    var color = 0
                    when (colorfullQuestVisualResourceStruct.colorType) {

                        PRIMARY ->

                            color = coloredItem.primaryColorModel
                                    .getRepresentation().getColor(questContext)

                        SECONDARY ->

                            color = coloredItem.secondaryColorModel
                                    .getRepresentation().getColor(questContext)

                        PRIMARY_INVERSE ->

                            color = 0xFFFFFF - coloredItem.primaryColorModel
                                    .getRepresentation().getColor(questContext) or -0x1000000

                        SECONDARY_INVERSE ->

                            color = 0xFFFFFF - coloredItem.secondaryColorModel
                                    .getRepresentation().getColor(questContext) or -0x1000000
                    }
                    DrawableCompat.setTint(drawable, color)
                }
                imageView.setImageDrawable(drawable)
                val layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                imageView.layoutParams = layoutParams
                imageView.requestFocus()
                holder.container.addView(imageView)
            }
        }
        holder.apply {
            view.setBackgroundColor(item.second.secondaryColorModel.getRepresentation().getColor(questContext))
            view.tag = item.second.primaryColorModel.id
            autoDispose {
                view.clicks().map { view.tag }.subscribe({ answerPublisher.onNext(it) })
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val container: ViewGroup
            get() = view as ViewGroup
    }
}