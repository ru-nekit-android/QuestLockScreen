package ru.nekit.android.qls.quest.view.mediator.content

import android.widget.EditText

import ru.nekit.android.qls.quest.view.mediator.IQuestMediator

//ver 1.0
interface IContentMediator : IQuestMediator {

    //TODO: used in only in a unused listenQuest - remove?
    val answerInput: EditText?

    fun includeInLayout(): Boolean

}