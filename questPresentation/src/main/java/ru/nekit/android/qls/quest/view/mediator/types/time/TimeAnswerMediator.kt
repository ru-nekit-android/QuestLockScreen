package ru.nekit.android.qls.quest.view.mediator.types.time

import android.view.ViewGroup
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.model.quest.TimeQuest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.view.mediator.answer.ListableAnswerMediator

//ver 1.1
open class TimeAnswerMediator : ListableAnswerMediator<Int, TimeAdapter>() {

    private lateinit var mQuest: TimeQuest

    override val listData: List<Int>
        get() = mQuest.leftNode.toList()

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        mQuest = quest as TimeQuest
        super.onCreate(questContext, quest)
    }

    override fun onAnswer(answerType: AnswerType): Boolean {
        if (answerType == AnswerType.WRONG) {
            shuffleListData()
            updateListAdapter()
        }
        return super.onAnswer(answerType)
    }

    override fun onQuestPlay(delayedPlay: Boolean) {
        super.onQuestPlay(delayedPlay)
        updateListAdapter()
    }

    override fun onQuestAttach(rootContentContainer: ViewGroup) {
        super.onQuestAttach(rootContentContainer)
        updateListAdapter()
        shuffleListData()
        updateListAdapter()
    }

    private fun updateListAdapter() {
        val rowCount = dataListSize / columnCount
        listAdapter.apply {
            size = rootContentContainer.let {
                if (rowCount > columnCount)
                    Math.max(it.width, it.height)
                else
                    Math.min(it.width, it.height)
            } / rowCount
            notifyDataSetChanged()
        }
    }

    override fun createListAdapter(listData: List<Int>): TimeAdapter {
        return TimeAdapter(listData, answerPublisher)
    }
}