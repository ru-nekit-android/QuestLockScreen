package ru.nekit.android.qls.quest.view.mediator.types.simpleExample

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView

import ru.nekit.android.qls.R
import ru.nekit.android.utils.ViewHolder

//ver 1.0
class SimpleExampleQuestViewHolder internal constructor(context: Context) : ViewHolder(context, R.layout.ql_simple_example) {

    var alternativeAnswerInput: EditText = view.findViewById<View>(R.id.alternative_answer_input) as EditText
    var rightSideView: TextView = view.findViewById<View>(R.id.right_side_view) as TextView
    var leftSideView: TextView = view.findViewById<View>(R.id.tv_left_side) as TextView

}
