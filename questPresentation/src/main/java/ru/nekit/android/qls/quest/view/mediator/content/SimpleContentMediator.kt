package ru.nekit.android.qls.quest.view.mediator.content

import android.support.annotation.CallSuper
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import io.reactivex.disposables.CompositeDisposable
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext

//ver 1.0
abstract class SimpleContentMediator : IContentMediator {

    override lateinit var quest: Quest
    override var disposableMap: MutableMap<String, CompositeDisposable> = HashMap()
    override lateinit var questContext: QuestContext
    protected lateinit var rootContentContainer: ViewGroup

    override fun detachView() {

    }

    @CallSuper
    override fun onQuestPlay(delayedPlay: Boolean) {
        if (delayedPlay) {
            playDelayedStartAnimation()
        }
    }

    override fun onQuestAttach(rootContentContainer: ViewGroup) {
        this.rootContentContainer = rootContentContainer
    }

    override fun onQuestStart(delayedPlay: Boolean) {

    }

    protected open fun playDelayedStartAnimation() {
        val view = view
        if (view != null) {
            view.scaleX = 0f
            view.scaleY = 0f
            view.animate().withLayer().withEndAction { view.setLayerType(View.LAYER_TYPE_NONE, null) }.scaleX(1f).scaleY(1f).setInterpolator(BounceInterpolator()).duration = questContext.questDelayedPlayAnimationDuration.toLong()
        }
    }

    override fun onQuestPause() {

    }

    override fun onQuestResume() {

    }

    override fun onQuestStop() {

    }

    override fun onQuestReplay() {

    }

    @CallSuper
    override fun deactivate() {
        dispose()
    }

    override fun onAnswer(answerType: AnswerType): Boolean {
        return true
    }
}