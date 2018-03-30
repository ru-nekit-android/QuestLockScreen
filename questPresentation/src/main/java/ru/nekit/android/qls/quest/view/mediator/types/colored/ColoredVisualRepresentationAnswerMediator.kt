package ru.nekit.android.qls.quest.view.mediator.types.colored

import android.util.Pair
import android.view.ViewGroup
import ru.nekit.android.qls.data.representation.common.IColorfullVisualResourceProvider
import ru.nekit.android.qls.data.representation.common.PairColorStruct
import ru.nekit.android.qls.data.representation.getRepresentation
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.model.quest.VisualRepresentationalNumberSummandQuest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.view.mediator.answer.ListableAnswerMediator
import ru.nekit.android.qls.window.RightAnswerWindow
import ru.nekit.android.utils.Delay
import java.util.*

class ColoredVisualRepresentationAnswerMediator :
        ListableAnswerMediator<Pair<IColorfullVisualResourceProvider, PairColorStruct>,
                ColoredVisualRepresentationQuestAdapter>() {

    private lateinit var localQuest: VisualRepresentationalNumberSummandQuest
    private lateinit var localListData: MutableList<Pair<IColorfullVisualResourceProvider, PairColorStruct>>

    override val listData: List<Pair<IColorfullVisualResourceProvider, PairColorStruct>>
        get() {
            localListData = ArrayList()
            val length = localQuest.visualRepresentationList.size
            val questResourceLibrary = questContext.questResourceRepository
            for (i in 0 until length) {
                val coloredVisualResource = questResourceLibrary
                        .getVisualResourceItemById(localQuest.visualRepresentationList[i])
                        .getRepresentation() as IColorfullVisualResourceProvider
                val pairColorStruct = PairColorStruct(localQuest.leftNode[i], localQuest.rightNode[i])
                localListData.add(Pair(coloredVisualResource, pairColorStruct))
            }
            return localListData
        }

    override fun createListAdapter(listData: List<Pair<IColorfullVisualResourceProvider, PairColorStruct>>): ColoredVisualRepresentationQuestAdapter {
        return ColoredVisualRepresentationQuestAdapter(questContext, listData, answerPublisher)
    }

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        localQuest = quest as VisualRepresentationalNumberSummandQuest
        super.onCreate(questContext, quest)
    }

    override fun deactivate() {
        super.deactivate()
        localListData.clear()
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
            fadeOutAndIn(Delay.SMALL.get(questContext), {
                val item = localListData[localQuest.unknownMemberIndex]
                localListData.clear()
                localListData.add(item)
                updateListAdapter(false)
                listView.requestLayout()
            }, {
                RightAnswerWindow.openSimple(questContext)
            })
            return false
        } else if (answerType == AnswerType.WRONG) {
            fadeOutAndIn(Delay.SMALL.get(questContext), {
                shuffleListData()
                updateListAdapter(true)
            })
        }
        return super.onAnswer(answerType)
    }
}
