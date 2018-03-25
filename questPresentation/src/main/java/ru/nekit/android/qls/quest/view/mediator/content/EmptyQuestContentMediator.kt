package ru.nekit.android.qls.quest.view.mediator.content

import android.view.View
import android.widget.EditText

//ver 1.0
class EmptyQuestContentMediator : SimpleContentMediator() {

    override val view: View? = null

    override val answerInput: EditText? = null

    override fun includeInLayout(): Boolean {
        return true
    }

    override fun updateSize() {

    }
}