package ru.nekit.android.qls.quest.view.mediator.types.colored

import android.util.Pair
import android.view.ViewGroup
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.model.quest.VisualRepresentationalNumberSummandQuest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.resources.common.IColorfullVisualResourceProvider
import ru.nekit.android.qls.quest.resources.representation.getRepresentation
import ru.nekit.android.qls.quest.resources.struct.PairColorStruct
import ru.nekit.android.qls.quest.view.mediator.answer.ListableAnswerMediator
import ru.nekit.android.qls.window.AnswerWindow
import java.util.*

class ColoredVisualRepresentationAnswerMediator :
        ListableAnswerMediator<Pair<IColorfullVisualResourceProvider, PairColorStruct>,
                ColoredVisualRepresentationQuestAdapter>() {

    private lateinit var mQuest: VisualRepresentationalNumberSummandQuest
    private lateinit var mListData: MutableList<Pair<IColorfullVisualResourceProvider, PairColorStruct>>

    override val listData: List<Pair<IColorfullVisualResourceProvider, PairColorStruct>>
        get() {
            mListData = ArrayList()
            val length = mQuest.visualRepresentationList.size
            val questResourceLibrary = questContext.questResourceRepository
            for (i in 0 until length) {
                val coloredVisualResource = questResourceLibrary
                        .getVisualResourceItemById(mQuest.visualRepresentationList[i])
                        .getRepresentation() as IColorfullVisualResourceProvider
                val pairColorStruct = PairColorStruct(mQuest.leftNode[i], mQuest.rightNode[i])
                mListData.add(Pair(coloredVisualResource, pairColorStruct))
            }
            return mListData
        }

    override fun createListAdapter(listData: List<Pair<IColorfullVisualResourceProvider, PairColorStruct>>): ColoredVisualRepresentationQuestAdapter {
        return ColoredVisualRepresentationQuestAdapter(questContext, listData, answerPublisher)
    }

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        mQuest = quest as VisualRepresentationalNumberSummandQuest
        super.onCreate(questContext, quest)
    }

    override fun deactivate() {
        super.deactivate()
        mListData.clear()
    }

    override fun onQuestPlay(delayedPlay: Boolean) {
        updateListAdapter(true)
        super.onQuestPlay(delayedPlay)
    }

    override fun onQuestAttach(rootContentContainer: ViewGroup) {
        super.onQuestAttach(rootContentContainer)
        updateListAdapter(true)
    }

    override fun onQuestReplay() {
        super.onQuestReplay()
        updateListAdapter(true)
    }

    private fun updateListAdapter(useSizeDivider: Boolean) {
        val size = Math.min(rootContentContainer.width, rootContentContainer.height)
        val count = listAdapter.itemCount
        val rowCount = Math.ceil((count / columnCount.toFloat()).toDouble()).toInt()
        listAdapter.size = size / if (useSizeDivider) Math.max(columnCount, rowCount) else 1
        listAdapter.notifyDataSetChanged()
    }

    override fun onAnswer(answerType: AnswerType): Boolean {
        if (answerType == AnswerType.RIGHT) {
            fadeOutAndIn(200, {
                val item = mListData[mQuest.unknownMemberIndex]
                mListData.clear()
                mListData.add(item)
                updateListAdapter(false)
                listView.requestLayout()
            }, {
                AnswerWindow.open(questContext, AnswerWindow.Type.RIGHT,
                        R.style.Window_RightAnswer_Simple,
                        R.layout.wc_right_answer_simple_content,
                        R.layout.wc_right_answer_tool_simple_content)
            })
            return false
        } else if (answerType == AnswerType.WRONG) {
            fadeOutAndIn(200, {
                shuffleListData()
                updateListAdapter(true)
            })
        }
        return super.onAnswer(answerType)
    }
}
