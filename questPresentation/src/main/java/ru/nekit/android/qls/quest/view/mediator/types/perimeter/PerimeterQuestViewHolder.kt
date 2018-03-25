package ru.nekit.android.qls.quest.view.mediator.types.perimeter

import android.content.Context
import android.view.View
import android.widget.TextView

import ru.nekit.android.qls.R
import ru.nekit.android.qls.utils.ViewHolder

//ver 1.0
internal class PerimeterQuestViewHolder(context: Context) : ViewHolder(context, R.layout.ql_perimeter) {

    var aFigureSideLabel: TextView = view.findViewById<View>(R.id.field_figure_side_a) as TextView
    var bFigureSideLabel: TextView = view.findViewById<View>(R.id.field_figure_side_b) as TextView
    var figureView: View = view.findViewById(R.id.view_figure)

}
