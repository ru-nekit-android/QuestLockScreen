package ru.nekit.android.qls.quest.view

import android.content.Context
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.BounceInterpolator
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.RelativeLayout.ABOVE
import android.widget.RelativeLayout.BELOW
import com.jakewharton.rxbinding2.widget.editorActionEvents
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.Subject
import ru.nekit.android.domain.event.KeyboardHideAction
import ru.nekit.android.qls.R
import ru.nekit.android.qls.data.representation.getAnswerInputType
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.QuestState.DELAYED_PLAY
import ru.nekit.android.qls.domain.model.QuestState.PLAYED
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.QuestContextEvent
import ru.nekit.android.qls.quest.QuestContextEvent.*
import ru.nekit.android.qls.quest.view.mediator.answer.ButtonListAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.answer.IAlternativeAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.answer.IAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.answer.IButtonListAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.content.EmptyQuestContentMediator
import ru.nekit.android.qls.quest.view.mediator.content.IContentMediator
import ru.nekit.android.qls.quest.view.mediator.title.ITitleMediator
import ru.nekit.android.qls.utils.KeyboardHost
import ru.nekit.android.qls.utils.KeyboardHost.hideKeyboard
import ru.nekit.android.qls.utils.throttleClicks
import ru.nekit.android.utils.AnimationUtils
import ru.nekit.android.utils.Delay
import ru.nekit.android.utils.ViewHolder
import java.util.concurrent.TimeUnit

//ver 1.2
class QuestMediatorFacade internal constructor(override var questContext: QuestContext,
                                               override val titleMediator: ITitleMediator,
                                               contentMediator: IContentMediator?,
                                               questAnswerMediator: IAnswerMediator?) :
        IQuestMediatorFacade,
        View.OnLayoutChangeListener {

    override var disposable: CompositeDisposable = CompositeDisposable()

    override val view: View
        get() = viewHolder.view

    override val contentMediator: IContentMediator = contentMediator
            ?: EmptyQuestContentMediator()
    override val answerMediator: IAnswerMediator = questAnswerMediator
            ?: ButtonListAnswerMediator()

    private lateinit var viewHolder: QuestViewHolder

    override lateinit var quest: Quest

    private val answerCallback: Subject<Any>
        get() = questContext.answerCallback

    init {
        listenForEvent(KeyboardHideAction::class.java) { action ->
            hideKeyboard(questContext, viewHolder.defaultAnswerInput, Delay.KEYBOARD.get(questContext), action.action)
        }
        listenForEvent(QuestContextEvent::class.java) {
            when (it) {
                QUEST_ATTACH -> onQuestAttach(viewHolder.rootContainer)
                QUEST_START -> questHasState(DELAYED_PLAY) {
                    onQuestStart(it)
                }
                QUEST_PLAY -> questHasState(DELAYED_PLAY) {
                    onQuestPlay(it)
                }
                QUEST_REPLAY -> onQuestReplay()
                QUEST_PAUSE -> onQuestPause()
                QUEST_RESUME -> onQuestResume()
                QUEST_STOP -> onQuestStop()
                EMPTY_ANSWER -> {
                    onAnswer(AnswerType.EMPTY)
                    setErrorColorAndReturnToNormal()
                }
                WRONG_INPUT_FORMAT_ANSWER -> {
                    onAnswer(AnswerType.WRONG_INPUT_FORMAT)
                    setErrorColorAndReturnToNormal()
                }
                else -> {
                }
            }
        }
    }

    override fun updateSize() {
        /*boolean contentViewIsPresent = false, alternativeAnswerIsPresent = false;
        if (questContext.questHasState(PLAYED) && !questContext.questHasState(PAUSED)) {
            contentViewIsPresent = contentMediator.getView() != null;
            alternativeAnswerIsPresent =
                    viewHolder.alternativeAnswerContainer.getVisibility() == VISIBLE;
            if (contentViewIsPresent) {
                contentMediator.updateSize();
            }
            if (alternativeAnswerIsPresent) {
                answerMediator.updateSize();
            }
        }
        if (contentViewIsPresent) {
            viewHolder.contentContainer.requestLayout();
        }
        if (alternativeAnswerIsPresent) {
            viewHolder.alternativeAnswerContainer.requestLayout();
        }*/
    }

    private fun answerButtonVisible(): Boolean {
        val ta = questContext.obtainStyledAttributes(R.style.Quest, R.styleable.QuestStyle)
        val showAnswerButton = ta.getBoolean(R.styleable.QuestStyle_answerButtonVisible, false)
        ta.recycle()
        return showAnswerButton
    }

    override fun onCreate(questContext: QuestContext, quest: Quest) {
        this.questContext = questContext
        this.quest = quest
        viewHolder = QuestViewHolder(questContext)
        answerMediator.answerPublisher = answerCallback
        titleMediator.onCreate(questContext, quest)
        contentMediator.onCreate(questContext, quest)
        answerMediator.onCreate(questContext, quest)
        viewHolder.titleContainer.addView(titleMediator.view)
        val contentView = contentMediator.view
        val answerContainerLayoutParams = viewHolder.answerContainer.layoutParams as RelativeLayout.LayoutParams
        val answerButtonContainerLayoutParams = viewHolder.alternativeAnswerContainer.layoutParams
        if (contentView == null) {
            answerContainerLayoutParams.addRule(BELOW, R.id.container_title)
            answerButtonContainerLayoutParams.height = MATCH_PARENT
        } else {
            answerContainerLayoutParams.addRule(BELOW, 0)
            answerButtonContainerLayoutParams.height = WRAP_CONTENT
        }
        (viewHolder.contentContainer.layoutParams as RelativeLayout.LayoutParams).apply {
            if (contentMediator.includeInLayout()) {
                addRule(ABOVE, R.id.container_answer)
                addRule(BELOW, R.id.container_title)
            } else {
                setMargins(0, 0, 0, 0)
                addRule(ABOVE, 0)
                addRule(BELOW, 0)
            }
        }
        if (contentView != null) {
            viewHolder.contentContainer.addView(contentMediator.view)
        }
        viewHolder.alternativeAnswerContainer.apply {
            answerMediator.view.let {
                if (it == null) {
                    if (answerMediator is IButtonListAnswerMediator) {
                        autoDispose {
                            answerMediator.answerButtonPublisher.subscribe { buttonList ->
                                if (buttonList.isEmpty())
                                    AnimationUtils.fadeAnimation(this,
                                            true,
                                            Delay.SHORT.get(questContext)) {
                                        removeAllViews()
                                    }
                                else addView(buttonList.data)
                            }
                        }
                    }
                } else {
                    addView(it)
                }
            }
        }
        val defaultAnswerInputVisible = !alternativeAnswerIsPresent()
        viewHolder.defaultAnswerInputContainer.visibility = if (defaultAnswerInputVisible) VISIBLE else GONE
        val showAnswerButton = !alternativeAnswerWithButtonListIsPresent() && answerButtonVisible()
        if (defaultAnswerInputVisible) {
            viewHolder.defaultAnswerInput.visibility = if (defaultAnswerInputVisible) VISIBLE else GONE
            viewHolder.defaultAnswerButton.visibility = if (showAnswerButton) VISIBLE else GONE
            viewHolder.alternativeAnswerContainer.visibility = if (alternativeAnswerWithButtonListIsPresent()) VISIBLE else GONE
            val answerInput: EditText? = if (alternativeAnswerInputIsPresent()) {
                val contentAnswerInput = contentMediator.answerInput
                if (contentAnswerInput != null && contentAnswerInput.visibility == VISIBLE) {
                    contentAnswerInput.inputType = quest.getAnswerInputType()
                    contentAnswerInput
                } else null
            } else
                viewHolder.defaultAnswerInput
            answerInput?.apply {
                inputType = quest.getAnswerInputType()
                imeOptions = IME_ACTION_DONE
                autoDispose {
                    editorActionEvents().map { it ->
                        it.actionId() == IME_ACTION_DONE
                    }.subscribe { if (it) answerFunction() }
                }
            }
        } else if (showAnswerButton) {
            (viewHolder.defaultAnswerButton.layoutParams as LinearLayout.LayoutParams).apply {
                weight = 1f
                viewHolder.defaultAnswerButton.layoutParams = this
            }
        }
        updateVisibilityOfViews(false)
        if (viewHolder.defaultAnswerButton.visibility == VISIBLE)
            autoDispose {
                viewHolder.defaultAnswerButton.throttleClicks {
                    answerFunction()
                }
            }
    }

    private fun answerFunction() {
        if (!alternativeAnswerWithButtonListIsPresent()) {
            answerCallback.onNext((if (alternativeAnswerInputIsPresent())
                contentMediator.answerInput
            else
                viewHolder.defaultAnswerInput)?.text.toString())
        }
    }

    override fun onQuestAttach(rootContentContainer: ViewGroup) {
        titleMediator.onQuestAttach(viewHolder.titleContainer)
        contentMediator.onQuestAttach(viewHolder.contentContainer)
        answerMediator.onQuestAttach(viewHolder.alternativeAnswerContainer)
        viewHolder.view.addOnLayoutChangeListener(this)
    }

    override fun onQuestStart(delayedPlay: Boolean) {
        viewHolder.titleContainer.requestLayout()
        viewHolder.contentContainer.requestLayout()
        viewHolder.alternativeAnswerContainer.requestLayout()
        titleMediator.onQuestStart(delayedPlay)
        contentMediator.onQuestStart(delayedPlay)
        answerMediator.onQuestStart(delayedPlay)
        requestFocus()
        questHasState(DELAYED_PLAY) {
            updateVisibilityOfViews(!it)
        }
    }

    override fun onQuestPlay(delayedPlay: Boolean) {
        updateVisibilityOfViews(true)
        titleMediator.onQuestPlay(delayedPlay)
        contentMediator.onQuestPlay(delayedPlay)
        if (delayedPlay) {
            if (alternativeAnswerWithButtonListIsPresent()) {
                answerMediator.onQuestPlay(true)
            } else {
                val duration = questContext.questDelayedPlayAnimationDuration
                viewHolder.answerContainer.let {
                    it.alpha = 0f
                    it.animate().withLayer().alpha(1f).duration = duration
                }

            }
        } else {
            answerMediator.onQuestPlay(false)
        }
        requestFocus()
    }

    override fun onAnswer(answerType: AnswerType): Boolean {
        var showRightAnswerWindow = true
        showRightAnswerWindow = showRightAnswerWindow && titleMediator.onAnswer(answerType)
        showRightAnswerWindow = showRightAnswerWindow && contentMediator.onAnswer(answerType)
        showRightAnswerWindow = showRightAnswerWindow && answerMediator.onAnswer(answerType)
        return showRightAnswerWindow
    }

    override fun onQuestReplay() {
        titleMediator.onQuestReplay()
        contentMediator.onQuestReplay()
        answerMediator.onQuestReplay()
        requestFocus()
    }

    override fun onQuestPause() {
        titleMediator.onQuestPause()
        contentMediator.onQuestPause()
        answerMediator.onQuestPause()
    }

    override fun onQuestResume() {
        titleMediator.onQuestResume()
        contentMediator.onQuestResume()
        answerMediator.onQuestResume()
        requestFocus()
    }

    override fun onQuestStop() {
        titleMediator.onQuestStop()
        contentMediator.onQuestStop()
        answerMediator.onQuestStop()
    }

    private fun alternativeAnswerInputIsPresent(): Boolean {
        val answerInput = contentMediator.answerInput
        return answerInput != null && answerInput.visibility == VISIBLE
    }

    private fun alternativeAnswerWithButtonListIsPresent(): Boolean {
        if (answerMediator is IButtonListAnswerMediator) {
            val buttonList = answerMediator.answerButtonPublisher.values
            val answerView = answerMediator.view
            return buttonList.isNotEmpty() ||
                    answerView != null && answerView.visibility == VISIBLE
        }
        return false
    }

    private fun alternativeAnswerIsPresent(): Boolean = alternativeAnswerWithButtonListIsPresent()
            || alternativeAnswerInputIsPresent()
            || answerMediator is IAlternativeAnswerMediator

    private fun setErrorColorAndReturnToNormal() {
        val duration = Delay.SHORT.get(questContext).toInt()
        autoDispose {
            Completable.fromRunnable {
                AnimationUtils.getColorAnimator(questContext, R.color.green,
                        R.color.red,
                        duration,
                        viewHolder.defaultAnswerInputContainer, BounceInterpolator()
                ).start()
            }.subscribeOn(questContext.schedulerProvider.ui())
                    .delay(Delay.LONG.get(questContext),
                            TimeUnit.MILLISECONDS, questContext.schedulerProvider.ui()).concatWith(
                    Completable.fromRunnable {
                        AnimationUtils.getColorAnimator(questContext, R.color.red,
                                R.color.green,
                                duration,
                                viewHolder.defaultAnswerInputContainer
                        ).start()
                    }).subscribe()
        }
    }

    override fun deactivate() {
        view.removeOnLayoutChangeListener(this)
        viewHolder.defaultAnswerInput.setOnEditorActionListener(null)
        onQuestStop()
        titleMediator.deactivate()
        contentMediator.deactivate()
        answerMediator.deactivate()
        dispose()
    }

    override fun detachView() {
        titleMediator.detachView()
        contentMediator.detachView()
        answerMediator.detachView()
    }

    private fun requestFocus() = questHasState(DELAYED_PLAY) { delayed ->
        questHasState(PLAYED) { played ->
            if (delayed && !played)
                hideKeyboard(viewHolder.defaultAnswerInput)
            else {
                if (alternativeAnswerWithButtonListIsPresent())
                    hideKeyboard(viewHolder.defaultAnswerInput)
                if (contentMediator.answerInput == null) {
                    if (viewHolder.defaultAnswerInput.visibility == VISIBLE)
                        showKeyboard(viewHolder.defaultAnswerInput)
                    else
                        hideKeyboard(viewHolder.defaultAnswerInput)
                } else if (contentMediator.answerInput?.visibility == VISIBLE) {
                    showKeyboard(contentMediator.answerInput!!)
                }
            }
        }
    }

    private fun showKeyboard(view: View) {
        KeyboardHost.showKeyboard(questContext, view, Delay.KEYBOARD.get(questContext))
    }

    private fun hideKeyboard(view: View) {
        KeyboardHost.hideKeyboard(questContext, view, Delay.KEYBOARD.get(questContext))
    }

    private fun updateVisibilityOfViews(value: Boolean) {
        val contentView = contentMediator.view
        if (contentView == null) {
            viewHolder.contentContainer.visibility = GONE
        } else {
            viewHolder.contentContainer.visibility = if (value) VISIBLE else INVISIBLE
        }
        if (alternativeAnswerWithButtonListIsPresent()) {
            viewHolder.alternativeAnswerContainer.visibility = if (value) VISIBLE else INVISIBLE
        } else {
            viewHolder.answerContainer.visibility = if (value) VISIBLE else INVISIBLE
        }
    }

    override fun onLayoutChange(view: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int,
                                oldTop: Int, oldRight: Int, oldBottom: Int) {
        updateSize()
    }

    internal class QuestViewHolder(context: Context) : ViewHolder(context, R.layout.layout_quest_view) {

        val rootContainer: ViewGroup = view.findViewById(R.id.container_root) as ViewGroup
        val titleContainer: ViewGroup = view.findViewById(R.id.container_title) as ViewGroup
        val answerContainer: ViewGroup = view.findViewById(R.id.container_answer) as ViewGroup
        val contentContainer: ViewGroup = view.findViewById(R.id.container_content) as ViewGroup
        val alternativeAnswerContainer: ViewGroup = view.findViewById(R.id.container_answer_alternative) as ViewGroup
        val defaultAnswerInputContainer: ViewGroup = view.findViewById(R.id.container_answer_default) as ViewGroup
        val defaultAnswerButton: View = view.findViewById(R.id.btn_answer_default)
        val defaultAnswerInput: EditText = view.findViewById(R.id.input_answer_default) as EditText

    }
}