package ru.nekit.android.qls.quest.view.mediator.types.fruitArithmetic

import android.support.annotation.LayoutRes
import android.view.ViewGroup
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.quest.FruitArithmeticQuest
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.view.mediator.answer.SimpleImageAdapterAnswerMediator

//ver 1.0
class FruitComparisonAnswerMediator : SimpleImageAdapterAnswerMediator() {

    private lateinit var mQuest: FruitArithmeticQuest

    override val columnCount: Int
        get() {
            var i = 1
            while (i * i < mQuest.leftNode.size) {
                i++
            }
            return i
        }

    override val listData: List<Int>
        get() = mQuest.leftNode.toList()

    override val adapterItemLayoutResId: Int
        @LayoutRes
        get() = R.layout.ill_fruit

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        mQuest = quest as FruitArithmeticQuest
        super.onCreate(questContext, quest)
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
        val dataListLength = listAdapter.itemCount
        val rowCount = Math.ceil((dataListLength / columnCount.toFloat()).toDouble()).toInt()
        listAdapter.size = size / Math.max(rowCount, columnCount)
        listAdapter.notifyDataSetChanged()
    }
}
