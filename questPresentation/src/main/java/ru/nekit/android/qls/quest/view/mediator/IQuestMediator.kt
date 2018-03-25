package ru.nekit.android.qls.quest.view.mediator

import android.view.View
import android.view.ViewGroup
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.providers.IQuestContextProvider

//ver 1.0
interface IQuestMediator : IQuestContextProvider {

    val view: View?
    var quest: Quest

    fun onCreate(questContext: QuestContext, quest: Quest) {
        this.questContext = questContext
        this.quest = quest
    }

    fun onQuestAttach(rootContentContainer: ViewGroup)

    fun onQuestStart(delayedPlay: Boolean)

    fun onQuestPlay(delayedPlay: Boolean)

    fun onAnswer(answerType: AnswerType): Boolean

    fun onQuestReplay()

    fun onQuestPause()

    fun onQuestResume()

    //call when screen is getting off
    fun onQuestStop()

    fun deactivate()

    fun detachView()

    fun updateSize()

}