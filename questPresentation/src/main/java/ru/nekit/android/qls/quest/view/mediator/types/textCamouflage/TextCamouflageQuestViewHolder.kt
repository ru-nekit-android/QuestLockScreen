package ru.nekit.android.qls.quest.view.mediator.types.textCamouflage

import android.content.Context
import android.support.v7.widget.RecyclerView

import ru.nekit.android.qls.R
import ru.nekit.android.utils.ViewHolder

internal class TextCamouflageQuestViewHolder(context: Context) : ViewHolder(context, R.layout.ql_text_camouflage) {

    var textViewGrid: RecyclerView = view.findViewById(R.id.grid_text_view)

}
