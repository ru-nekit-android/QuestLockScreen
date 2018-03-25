package ru.nekit.android.qls.quest.view.mediator.types.coins

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.RelativeLayout.LayoutParams
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.model.resources.CoinVisualResourceCollection
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.view.mediator.content.SimpleContentMediator
import ru.nekit.android.qls.shared.model.QuestionType
import java.util.*

//ver 1.0
class CoinQuestContentMediator : SimpleContentMediator() {

    private lateinit var contentContainer: RelativeLayout
    private lateinit var coinViewHolderList: MutableList<CoinViewHolder>

    override val view: View?
        get() = contentContainer

    override val answerInput: EditText? = null

    private fun getXPositionShiftByWidth(width: Int, coinCount: Int): Int {
        val sizeMultiplier = if (coinCount > 4) 0.6f else 0.7f
        return (width * sizeMultiplier).toInt()
    }

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        contentContainer = RelativeLayout(questContext)
        coinViewHolderList = ArrayList()
        listenQuest(NumberSummandQuest::class.java) {
            with(it) {
                val values = leftNode.toList()
                when (questionType) {
                    QuestionType.SOLUTION, QuestionType.UNKNOWN_MEMBER ->
                        for (i in values.size - 1 downTo 0) {
                            if (questionType == QuestionType.UNKNOWN_MEMBER
                                    && i == unknownMemberIndex) {
                                continue
                            }
                            val coinVisualResourceItem = CoinVisualResourceCollection.getById(values[i])
                            val coinViewHolder = CoinViewBuilder.createView(questContext,
                                    coinVisualResourceItem)
                            contentContainer.addView(coinViewHolder.view)
                            val layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                            coinViewHolder.view.layoutParams = layoutParams
                            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
                            coinViewHolder.view.requestLayout()
                            coinViewHolderList.add(coinViewHolder)

                        }
                }
            }
        }
    }

    override fun onQuestAttach(rootContentContainer: ViewGroup) {
        super.onQuestAttach(rootContentContainer)
        updateSizeInternal()
    }

    override fun deactivate() {
        coinViewHolderList.clear()
        super.deactivate()
    }

    override fun detachView() {
        contentContainer.removeAllViews()
    }

    override fun includeInLayout(): Boolean {
        return true
    }

    override fun updateSize() {
        //updateSizeInternal();
    }

    private fun updateSizeInternal() {
        val width = rootContentContainer.width
        val coinCount = coinViewHolderList.size
        var coinContainerWidth = 0
        var coinXPosition = 0
        var coinSize = 0
        var maxCoinHeight = 0
        coinViewHolderList.forEachIndexed { index, coinViewHolder ->
            coinSize = coinViewHolder.getAdaptiveWidth(width)
            maxCoinHeight = Math.max(maxCoinHeight, coinSize)
            coinXPosition += getXPositionShiftByWidth(coinSize, coinCount)
            if (index < coinCount - 1) {
                coinContainerWidth = coinXPosition
            }
        }
        coinContainerWidth += coinSize
        //scale
        val scale = 1 / (if (coinCount > 2) 1f else 1.3f) * width / coinContainerWidth
        coinXPosition = 0
        coinViewHolderList.forEach {
            val coinView = it.view
            val coinLayoutParams = coinView.layoutParams
            coinSize = (it.getAdaptiveWidth(width) * scale).toInt()
            coinLayoutParams.height = coinSize
            coinLayoutParams.width = coinLayoutParams.height
            coinView.x = coinXPosition.toFloat()
            coinXPosition += getXPositionShiftByWidth(coinSize, coinCount)
            coinView.requestLayout()
        }
    }

    override fun onQuestPause() {
        super.onQuestPause()
        updateSize()
    }
}