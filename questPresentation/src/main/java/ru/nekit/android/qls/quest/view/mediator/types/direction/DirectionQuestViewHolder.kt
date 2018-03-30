package ru.nekit.android.qls.quest.view.mediator.types.direction

import android.content.Context
import android.view.View

import ru.nekit.android.qls.R
import ru.nekit.android.utils.ViewHolder

//ver 1.0
internal class DirectionQuestViewHolder(context: Context) : ViewHolder(context, R.layout.ql_direction) {

    var targetView: View = view.findViewById(R.id.view_target)

}