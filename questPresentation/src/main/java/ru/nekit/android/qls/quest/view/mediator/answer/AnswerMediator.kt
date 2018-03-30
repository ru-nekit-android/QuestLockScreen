package ru.nekit.android.qls.quest.view.mediator.answer

import android.support.annotation.CallSuper
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.Subject
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.utils.AnimationUtils

//ver 1.0
open class AnswerMediator : IAnswerMediator {

    override lateinit var quest: Quest
    override lateinit var questContext: QuestContext
    override lateinit var answerPublisher: Subject<Any>
    override var disposable: CompositeDisposable = CompositeDisposable()

    protected lateinit var rootContentContainer: ViewGroup


    override val view: View?
        get() = null

    override fun onQuestStart(delayedPlay: Boolean) {

    }

    @CallSuper
    override fun onQuestPlay(delayedPlay: Boolean) {
        if (delayedPlay) {
            startDelayedPlayAnimation()
        }
    }

    private fun startDelayedPlayAnimation() {
        rootContentContainer.apply {
            scaleX = 0F
            scaleY = 0F
        }
        AnimationUtils.animate(rootContentContainer, questContext.questDelayedPlayAnimationDuration,
                BounceInterpolator(),
                { it.scaleX(1f).scaleY(1F) },
                {},
                { it },
                {})
    }

    protected fun fadeOutAndIn(duration: Long, actionStart: () -> Unit, actionEnd: () -> Unit) =
            AnimationUtils.fadeOutAndIn(view
                    ?: rootContentContainer, duration, actionStart, actionEnd)

    protected fun fadeOutAndIn(duration: Long, actionStart: () -> Unit) =
            fadeOutAndIn(duration, actionStart, {})


    override fun onQuestPause() {

    }

    override fun onQuestResume() {

    }

    @CallSuper
    override fun onQuestReplay() {

    }

    @CallSuper
    override fun onQuestStop() {

    }

    @CallSuper
    override fun deactivate() {
        dispose()
    }

    override fun detachView() {

    }

    override fun updateSize() {}

    override fun onAnswer(answerType: AnswerType): Boolean = true

    @CallSuper
    override fun onQuestAttach(rootContentContainer: ViewGroup) {
        this.rootContentContainer = rootContentContainer
    }
}