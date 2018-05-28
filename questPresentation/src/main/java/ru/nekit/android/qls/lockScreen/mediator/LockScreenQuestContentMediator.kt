package ru.nekit.android.qls.lockScreen.mediator

import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewSwitcher
import at.grabner.circleprogress.CircleProgressView
import io.reactivex.disposables.CompositeDisposable
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.qls.R
import ru.nekit.android.qls.R.id.*
import ru.nekit.android.qls.data.representation.getRepresentation
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.AnswerType.RIGHT
import ru.nekit.android.qls.domain.model.AnswerType.WRONG
import ru.nekit.android.qls.domain.model.QuestState.DELAYED_PLAY
import ru.nekit.android.qls.domain.model.QuestState.PLAYED
import ru.nekit.android.qls.domain.model.Reward
import ru.nekit.android.qls.domain.model.Transition.QUEST
import ru.nekit.android.qls.domain.useCases.ConsumeRewardUseCase
import ru.nekit.android.qls.domain.useCases.SetupWizardUseCases
import ru.nekit.android.qls.domain.useCases.TransitionChoreographEvent
import ru.nekit.android.qls.domain.useCases.TransitionChoreographUseCases
import ru.nekit.android.qls.lockScreen.mediator.common.AbstractLockScreenContentMediator
import ru.nekit.android.qls.lockScreen.mediator.common.ILockScreenContentViewHolder
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.QuestContextEvent.*
import ru.nekit.android.qls.quest.providers.IQuestContextSupport
import ru.nekit.android.qls.quest.view.IQuestMediatorFacade
import ru.nekit.android.qls.quest.view.IQuestViewHolder
import ru.nekit.android.qls.quest.view.QuestVisualBuilder
import ru.nekit.android.qls.setupWizard.VoiceCenter
import ru.nekit.android.qls.window.*
import ru.nekit.android.qls.window.UnlockKeyHelpWindowMediator.Step.HELP_ON_CONSUME
import ru.nekit.android.qls.window.UnlockKeyHelpWindowMediator.Step.HELP_ON_ZERO_COUNT
import ru.nekit.android.qls.window.common.QuestWindowEvent.*
import ru.nekit.android.utils.AnimationUtils.fadeAnimation
import ru.nekit.android.utils.Delay
import ru.nekit.android.utils.ViewHolder
import ru.nekit.android.utils.responsiveClicks
import ru.nekit.android.window.Window

//ver 1.3
class LockScreenQuestContentMediator(override var questContext: QuestContext) :
        AbstractLockScreenContentMediator() {

    override lateinit var viewHolder: LockScreenQuestViewHolder
    private val questMediatorFacadeStack: Array<IQuestMediatorFacade?> = arrayOfNulls(2)
    private val voiceCenter: VoiceCenter = VoiceCenter()

    init {
        viewHolder = LockScreenQuestViewHolder(questContext).apply {
            autoDisposeList(
                    menuButton.responsiveClicks {
                        hideKeyboard {
                            MenuWindowMediator.openWindow(questContext)
                        }
                    },
                    statisticsContainer.responsiveClicks {
                        hideKeyboard {
                            StatisticsWindowMediator.openWindow(questContext)
                        }
                    },
                    delayedPlayContainer.responsiveClicks {
                        questContext.playQuest()
                    },
                    unlockKeyContainer.responsiveClicks {
                        getUnlockKeyCount { unlockKeyCount ->
                            SetupWizardUseCases.showHelpOnUnlockKeyConsume {
                                if (it) {
                                    UnlockKeyHelpWindowMediator.openWindow(questContext,
                                            if (unlockKeyCount == 0) HELP_ON_ZERO_COUNT
                                            else HELP_ON_CONSUME)
                                } else {
                                    if (unlockKeyCount == 0)
                                        UnlockKeyHelpWindowMediator.openWindow(questContext,
                                                HELP_ON_ZERO_COUNT
                                        )
                                    else
                                        ConsumeRewardUseCase(questContext.repositoryHolder).use(Reward.UnlockKey()) {
                                            if (it) sendEvent(LockScreenContentMediatorAction.CLOSE)
                                        }
                                }
                            }
                        }
                    }
            )
            val animationListener = object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    if (animation == inAnimation) {
                        questMediatorFacadeStack[0]?.view?.setLayerType(LAYER_TYPE_NONE, null)
                        questContext.startAndPlayQuestIfAble()
                    } else if (animation == outAnimation) {
                        questMediatorFacadeStack[1]?.view?.setLayerType(LAYER_TYPE_NONE, null)
                        questMediatorFacadeStack[1]?.detachView()
                        questMediatorFacadeStack[1] = null
                    }
                }

                override fun onAnimationRepeat(animation: Animation) {}
            }
            inAnimation.setAnimationListener(animationListener)
            outAnimation.setAnimationListener(animationListener)
            listenForEvent(TransitionChoreographEvent::class.java) { event ->
                if (event == TransitionChoreographEvent.TRANSITION_CHANGED) {
                    if (event.currentTransition == QUEST) {
                        questMediatorFacadeStack[1] = questMediatorFacadeStack[0]
                        questMediatorFacadeStack[1]?.deactivate()
                        createAndAttachQuestView {}
                    }
                }
            }
            listenForEvent(LockScreenContentMediatorEvent::class.java) { event ->
                if (event == LockScreenContentMediatorEvent.ON_CONTENT_SWITCH)
                    TransitionChoreographUseCases.getPreviousTransition { previousTransition ->
                        if (previousTransition != QUEST)
                            questContext.startAndPlayQuestIfAble()
                    }
            }
            listenForQuestEvent { event ->
                when (event) {
                    QUEST_START, QUEST_PLAY, QUEST_ATTACH -> {
                        if (event == QUEST_ATTACH)
                            getUnlockKeyCount { unlockKeyCount ->
                                unlockKeyCountView.text = "$unlockKeyCount"
                            }
                        if (event == QUEST_START || event == QUEST_PLAY)
                            updateViewVisibility()
                        questIsDelayed {
                            if ((it && event == QUEST_PLAY) || (!it && event == QUEST_START)) {
                                quest { quest ->
                                    questContext.questSeriesLength { questSeriesLength ->
                                        questContext.questSeriesCounterValue { questSeriesCounterValue ->
                                            val questName = quest.questType.getRepresentation().getString(questContext)
                                            titleView.text = questName
                                            (questSeriesLength > 1).apply {
                                                if (this) {
                                                    val questSeriesString = if (questSeriesLength > 1)
                                                        String.format(questContext.getString(R.string.quest_series),
                                                                questSeriesLength - questSeriesCounterValue + 1,
                                                                questSeriesLength)
                                                    else
                                                        ""
                                                    val questSeriesSpan = SpannableString(questSeriesString)
                                                    val sizeSpan = AbsoluteSizeSpan(
                                                            questContext.resources.getDimensionPixelSize(R.dimen.quest_series_size), false)
                                                    questSeriesSpan.setSpan(sizeSpan, 0, questSeriesString.length,
                                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                                    titleViewSecondary.text = questSeriesString
                                                }
                                                titleViewSecondary.visibility = if (this) VISIBLE else GONE
                                            }
                                        }
                                    }
                                }
                            }
                            if (it && event == QUEST_ATTACH) {
                                titleViewSecondary.visibility = GONE
                                titleView.text = getString(R.string.wait_for_start_title)
                            }
                        }
                    }
                    RIGHT_ANSWER, WRONG_ANSWER -> {
                        val isRight = event == RIGHT_ANSWER
                        questMediatorFacadeStack[0]!!.onAnswer(if (isRight) RIGHT else WRONG).let {
                            if (it) hideKeyboard {
                                if (isRight)
                                    RightAnswerWindow.openDefault(questContext)
                                else
                                    WrongAnswerWindow.openDefault(questContext)
                            }
                        }
                    }
                }
            }
            listenForWindowEvent {
                when (it) {
                    OPEN -> questContext.pauseQuest()
                    OPENED -> when (it.windowName) {
                        AnswerWindowType.RIGHT.name ->
                            voiceCenter.playVoice(AnswerType.RIGHT)
                        AnswerWindowType.WRONG.name ->
                            voiceCenter.playVoice(AnswerType.WRONG)
                    }
                    CLOSE -> voiceCenter.stopVoice()
                    CLOSED -> TransitionChoreographUseCases.getCurrentTransition { currentTransition ->
                        when (it.windowName) {
                            AnswerWindowType.RIGHT.name ->
                                TransitionChoreographUseCases.doCurrentTransition()
                            else ->
                                if (currentTransition == QUEST)
                                    questContext.resumeQuest()

                        }
                    }
                }
            }
            listenPupilStatistics { pupilStatistics ->
                currentLevel { currentLevel ->
                    allPointsLevel { allPoints ->
                        val scoreOnLevel = pupilStatistics.score - allPoints
                        pupilProgress.setValue(scoreOnLevel * 100 / currentLevel.pointsWeight.toFloat())
                        pupilLevel.text = currentLevel.toString()
                    }
                }
            }
        }
    }

    override fun deactivate() {
        viewHolder.apply {
            inAnimation.setAnimationListener(null)
            outAnimation.setAnimationListener(null)
        }
        dispose()
        questMediatorFacadeStack[1]?.deactivate()
        questMediatorFacadeStack[0]?.deactivate()
    }

    override fun detachView() {
        Window.closeAllWindows()
        questMediatorFacadeStack[1]?.detachView()
        questMediatorFacadeStack[0]?.detachView()
        viewHolder.detachQuestView()
    }

    override fun attachView(callback: () -> Unit) = createAndAttachQuestView(callback)

    private fun createAndAttachQuestView(callback: () -> Unit) =
            autoDispose {
                QuestVisualBuilder.build(questContext) { it ->
                    questMediatorFacadeStack[0] = it
                    it.view.let { view ->
                        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                            override fun onViewAttachedToWindow(view: View) {
                                questContext.attachQuest()
                                view.removeOnAttachStateChangeListener(this)
                            }

                            override fun onViewDetachedFromWindow(view: View) {
                            }
                        })
                        viewHolder.attachQuestView(view)
                        callback()
                    }
                }
            }

    class LockScreenQuestViewHolder internal constructor(override var questContext: QuestContext) :
            ViewHolder(questContext, R.layout.layout_lock_screen_quest_view_container),
            ILockScreenContentViewHolder, IQuestViewHolder, IQuestContextSupport {

        override var disposableMap: MutableMap<String, CompositeDisposable> = HashMap()

        private var titleContainer: ViewGroup = view.findViewById(container_title)
        private val instructionContainer: ViewGroup = view.findViewById(container_instruction)
        private val questContentContainer: ViewSwitcher = view.findViewById(container_content_quest)

        internal val menuButton: ImageView = view.findViewById(btn_menu)
        internal val titleView: TextView = view.findViewById(tv_title)
        internal val titleViewSecondary: TextView = view.findViewById(tv_message_secondary)
        internal val pupilLevel: TextView = view.findViewById(tv_pupil_level)
        internal val statisticsContainer: ViewGroup = view.findViewById(container_statistics)
        internal val delayedPlayContainer: ViewGroup = view.findViewById(container_delayed_play)
        internal val unlockKeyContainer: ViewGroup = view.findViewById(container_unlock_key)
        internal val unlockKeyCountView: TextView = view.findViewById(tv_unlock_key_count)
        internal val pupilProgress: CircleProgressView = view.findViewById(pupil_progress)
        internal val outAnimation: Animation = AnimationUtils.loadAnimation(questContext, R.anim.slide_vertical_out)
        internal val inAnimation: Animation = AnimationUtils.loadAnimation(questContext, R.anim.slide_vertical_in)

        override val titleContentContainer: View
            get() = titleContainer

        init {
            questContentContainer.outAnimation = outAnimation
            questContentContainer.inAnimation = inAnimation
            (view as ViewGroup).removeAllViews()
        }

        override val contentContainer: View
            get() = questContentContainer.parent as View

        override fun attachQuestView(questView: View) {
            val contentContainerHasNoChild = questContentContainer.childCount == 0
            questContentContainer.addView(questView)
            questView.setLayerType(LAYER_TYPE_HARDWARE, null)
            if (!contentContainerHasNoChild) {
                val previousQuestView = questContentContainer.getChildAt(0)
                previousQuestView.setLayerType(LAYER_TYPE_HARDWARE, null)
                questContentContainer.showNext()
                questContentContainer.removeViewAt(0)
            }
            instructionContainer.visibility = INVISIBLE
        }

        override fun detachQuestView() {
            questContentContainer.removeAllViews()
            dispose()
        }

        internal fun updateViewVisibility() {
            autoDispose {
                questHasStates(DELAYED_PLAY, PLAYED) {
                    val delayed = it[0]
                    val played = it[1]
                    val showDelayedPlayContainer = delayed && !played
                    instructionContainer.visibility = if (showDelayedPlayContainer) {
                        fadeAnimation(instructionContainer, false, Delay.SHORT.get(questContext))
                        VISIBLE
                    } else INVISIBLE
                }
            }
        }
    }
}
