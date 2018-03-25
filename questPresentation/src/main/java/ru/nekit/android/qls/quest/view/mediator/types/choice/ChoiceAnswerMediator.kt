package ru.nekit.android.qls.quest.view.mediator.types.choice

import android.support.annotation.LayoutRes
import android.view.ViewGroup
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.view.mediator.answer.SimpleImageAdapterAnswerMediator

//ver 1.1
class ChoiceAnswerMediator : SimpleImageAdapterAnswerMediator() {

    private lateinit var mQuest: NumberSummandQuest
    override val adapterItemLayoutResId: Int
        @LayoutRes
        get() = R.layout.ill_choice

    override val listData: List<Int>
        get() = mQuest.leftNode.toList()

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        mQuest = quest as NumberSummandQuest
        super.onCreate(questContext, quest)
    }

    override fun onAnswer(answerType: AnswerType): Boolean {
        if (answerType == AnswerType.WRONG) {
            shuffleListData()
            updateListAdapter()
        }
        return super.onAnswer(answerType)
    }

    override fun onQuestAttach(rootContentContainer: ViewGroup) {
        super.onQuestAttach(rootContentContainer)
        updateListAdapter()
    }

    override fun onQuestStart(delayedPlay: Boolean) {
        super.onQuestStart(delayedPlay)
        if (!delayedPlay) {
            updateListAdapter()
        }
    }

    override fun onQuestPlay(delayedPlay: Boolean) {
        if (delayedPlay) {
            updateListAdapter()
        }
        super.onQuestPlay(delayedPlay)
    }

    private fun updateListAdapter() {
        val size = Math.min(rootContentContainer.width, rootContentContainer.height)
        val itemCount = listAdapter.itemCount
        val rowCount = Math.ceil((itemCount / columnCount.toFloat()).toDouble()).toInt()
        listAdapter.size = size / rowCount
        listAdapter.notifyDataSetChanged()
    }

}