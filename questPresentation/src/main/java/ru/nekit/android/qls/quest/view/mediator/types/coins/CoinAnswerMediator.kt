package ru.nekit.android.qls.quest.view.mediator.types.coins

import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.model.resources.CoinVisualResourceCollection
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.view.mediator.answer.ButtonListAnswerMediator
import ru.nekit.android.qls.shared.model.QuestionType
import java.util.*

//ver 1.0
class CoinAnswerMediator : ButtonListAnswerMediator() {

    private lateinit var coinButtonList: MutableList<CoinButtonViewHolder>

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        super.onCreate(questContext, quest)
        coinButtonList = ArrayList()
        with(quest) {
            when (questionType) {
                QuestionType.UNKNOWN_MEMBER ->
                    availableAnswerVariants?.shuffled()?.forEach { item ->
                        val coinVisualResourceItem = CoinVisualResourceCollection.getById(item as Int)
                        val coinButtonHost = CoinViewBuilder.createButton(questContext,
                                coinVisualResourceItem)
                        autoDispose {
                            coinButtonHost.container.clicks().map {
                                answerPublisher.onNext(coinButtonHost.container.tag)
                            }.subscribe()
                        }
                        coinButtonList.add(coinButtonHost)
                        answerButtonPublisher.onNext(Optional(coinButtonHost.view))
                    }
                else -> {
                }
            }
        }
    }

    override fun onQuestAttach(rootContentContainer: ViewGroup) {
        super.onQuestAttach(rootContentContainer)
        updateSizeInternal()
    }

    override fun detachView() {
        coinButtonList.clear()
        super.detachView()
    }

    override fun updateSize() {
        updateSizeInternal()
    }

    private fun updateSizeInternal() {
        val width = rootContentContainer.width
        if (width > 0) {
            when (quest.questionType) {

                QuestionType.UNKNOWN_MEMBER -> {
                    val coinCount = coinButtonList.size
                    var coinsWidth = 0
                    coinButtonList.forEach {
                        val coinLayoutParams = it.coinViewHolder.view.layoutParams
                        val coinSize = it.coinViewHolder.getAdaptiveWidth(width)
                        coinLayoutParams.height = coinSize
                        coinLayoutParams.width = coinLayoutParams.height
                        coinsWidth += coinSize
                    }
                    val spaceBetweenCoins = (width - coinsWidth) / coinCount / 2
                    coinButtonList.forEach {
                        val coinButtonLayoutParams = it.view.layoutParams
                        coinButtonLayoutParams.width = it.coinViewHolder.getAdaptiveWidth(width) + spaceBetweenCoins * 2
                        coinButtonLayoutParams.height = it.coinViewHolder.getAdaptiveHeight(width)
                        it.view.requestLayout()
                    }
                    rootContentContainer.requestLayout()
                }
            }
        }
    }
}