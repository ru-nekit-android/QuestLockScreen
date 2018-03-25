package ru.nekit.android.qls.lockScreen.content

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
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.QuestState
import ru.nekit.android.qls.domain.model.RecordType
import ru.nekit.android.qls.domain.model.Transition.QUEST
import ru.nekit.android.qls.domain.useCases.CommitCurrentTransitionUseCase
import ru.nekit.android.qls.domain.useCases.GetCurrentTransitionUseCase
import ru.nekit.android.qls.domain.useCases.GetPreviousTransitionUseCase
import ru.nekit.android.qls.domain.useCases.TransitionChoreographEvent
import ru.nekit.android.qls.lockScreen.LockScreenContentMediatorEvent
import ru.nekit.android.qls.lockScreen.content.common.AbstractLockScreenContentMediator
import ru.nekit.android.qls.lockScreen.content.common.ILockScreenContentViewHolder
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.QuestContextEvent
import ru.nekit.android.qls.quest.QuestContextEvent.*
import ru.nekit.android.qls.quest.providers.IQuestContextProvider
import ru.nekit.android.qls.quest.resources.representation.getRepresentation
import ru.nekit.android.qls.quest.view.IQuestMediatorFacade
import ru.nekit.android.qls.quest.view.IQuestViewHolder
import ru.nekit.android.qls.quest.view.QuestVisualBuilder
import ru.nekit.android.qls.utils.AnimationUtils.fadeAnimation
import ru.nekit.android.qls.utils.Delay.SHORT
import ru.nekit.android.qls.utils.ViewHolder
import ru.nekit.android.qls.utils.throttleClicks
import ru.nekit.android.qls.window.AnswerWindow
import ru.nekit.android.qls.window.MenuWindowMediator
import ru.nekit.android.qls.window.Window
import ru.nekit.android.qls.window.common.QuestWindowEvent
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//ver 1.2
class LockScreenQuestContainerMediator(override var questContext: QuestContext) :
        AbstractLockScreenContentMediator() {

    override lateinit var viewHolder: LockScreenQuestViewHolder

    private val questMediatorFacadeStack: Array<IQuestMediatorFacade?> = arrayOfNulls(2)

    //TODO:IMPLEMENT!!
    //private val mVoiceCenter: VoiceCenter

    init {

        viewHolder = LockScreenQuestViewHolder(questContext).apply {
            autoDisposeList {
                listOf(
                        menuButton.throttleClicks {
                            hideKeyboard {
                                MenuWindowMediator.openWindow(questContext, MenuWindowMediator.Step.PHONE)
                            }
                        },
                        statisticsContainer.throttleClicks {
                            /*KeyboardHost.hideKeyboard(questContext, view) {
                          MenuWindowMediator.openWindow(questContext,
                                  if (view == viewHolder.menuButton)
                                      null
                                  else
                                      MenuWindowMediator.Step.PUPIL_STATISTICS_TITLE)
                      }*/
                        },
                        delayedPlayContainer.throttleClicks {
                            questContext.playQuest {
                                if (it) viewHolder.updateViewVisibility()
                            }
                        }
                )
            }
        }
        val animationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                if (animation == viewHolder.outAnimation) {
                    fadeAnimation(viewHolder.titleView, true, SHORT.get(questContext))
                    fadeAnimation(viewHolder.titleViewSecondary, true, SHORT.get(questContext))
                }
            }

            override fun onAnimationEnd(animation: Animation) {
                if (animation == viewHolder.inAnimation) {
                    questMediatorFacadeStack[0]?.view?.setLayerType(LAYER_TYPE_NONE, null)
                    questContext.startAndPlayQuestIfAble()
                } else if (animation == viewHolder.outAnimation) {
                    questMediatorFacadeStack[1]?.view?.setLayerType(LAYER_TYPE_NONE, null)
                    questMediatorFacadeStack[1]?.detachView()
                    questMediatorFacadeStack[1] = null
                    fadeAnimation(viewHolder.titleView, false, SHORT.get(questContext))
                    fadeAnimation(viewHolder.titleViewSecondary, false, SHORT.get(questContext))
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        }
        viewHolder.inAnimation.setAnimationListener(animationListener)
        viewHolder.outAnimation.setAnimationListener(animationListener)
        listenForEvent(TransitionChoreographEvent::class.java) { event ->
            if (event.currentTransition == QUEST) {
                questMediatorFacadeStack[1] = questMediatorFacadeStack[0]
                questMediatorFacadeStack[1]?.deactivate()
                createAndAttachQuestView()
            }
        }
        listenForEvent(LockScreenContentMediatorEvent::class.java) { event ->
            if (event == LockScreenContentMediatorEvent.ON_CONTENT_SWITCH)
                GetPreviousTransitionUseCase(questContext.repository,
                        questContext.schedulerProvider).use { previousTransitionOpt ->
                    if (previousTransitionOpt.data != QUEST)
                        questContext.startAndPlayQuestIfAble()
                }
        }
        listenForEvent(QuestContextEvent::class.java) {

            when (it) {

                QUEST_START -> viewHolder.updateViewVisibility()

                RIGHT_ANSWER -> questMediatorFacadeStack[0]?.onAnswer(AnswerType.RIGHT)?.let {

                    if (it) pupil { pupil ->
                        questHistory { questHistory ->
                            questPreviousHistoryWithBestSessionTime(questHistory!!) { previousHistory ->
                                questStatisticsReport { report ->
                                    hideKeyboard {
                                        //TODO: make right answer content holder with logic
                                        val contentViewHolder = ViewHolder(questContext, R.layout.wc_right_answer_content)
                                        val dateFormat = SimpleDateFormat(questContext.getString(R.string.right_answer_timer_formatter),
                                                Locale.getDefault())
                                        val textList = ArrayList<String>()
                                        textList.add("${pupil.name}, ${questContext.getString(
                                                R.string.congratulation_for_right_answer).toLowerCase()}")
                                        textList.add(String.format(questContext.getString(R.string.right_answer_time_formatter),
                                                dateFormat.format(questHistory.sessionTime)))
                                        if (previousHistory != null && (questHistory.recordTypes and RecordType.RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD.value) != 0) {
                                            textList.add(String.format(questContext.getString(R.string.right_answer_best_time_update_formatter),
                                                    dateFormat.format(previousHistory.sessionTime)))
                                        }
                                        if ((questHistory.recordTypes and RecordType.RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD.value) != 0) {
                                            textList.add(String.format(questContext.getString(R.string.right_answer_series_length_update_formatter),
                                                    report.rightAnswerSeriesCount))
                                        }
                                        val titleView = contentViewHolder.view.findViewById(R.id.tv_title) as TextView
                                        titleView.text = textList.joinToString("\n")
                                        AnswerWindow.open(
                                                questContext,
                                                AnswerWindow.Type.RIGHT,
                                                R.style.Window_RightAnswer,
                                                contentViewHolder,
                                                R.layout.wc_right_answer_tool_simple_content
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                WRONG_ANSWER -> questMediatorFacadeStack[0]?.onAnswer(AnswerType.WRONG)?.let {
                    if (it) hideKeyboard {
                        val contentViewHolder = ViewHolder(questContext, R.layout.wc_wrong_answer_content)
                        val titleView = contentViewHolder.view.findViewById(R.id.tv_title) as TextView
                        titleView.text = questContext.getString(R.string.title_wrong_answer)
                        AnswerWindow.open(
                                questContext,
                                AnswerWindow.Type.WRONG,
                                R.style.Window_WrongAnswer,
                                contentViewHolder,
                                R.layout.wc_wrong_answer_tool_simple_content
                        )
                    }
                }
                else -> {
                }
            }
        }
        listenForEvent(QuestWindowEvent::class.java) {
            when (it) {
                QuestWindowEvent.OPEN ->
                    questContext.pauseQuest()

                QuestWindowEvent.OPENED -> {

                    //TODO:IMPLEMENT!!
                    when (it.windowName) {
                        AnswerWindow.Type.RIGHT.name -> {
                            //mVoiceCenter.playVoice(VoiceCenter.Type.RightSeries)
                        }
                        AnswerWindow.Type.WRONG.name -> {
                            // mVoiceCenter.playVoice(VoiceCenter.Type.WRONG)
                        }
                    }
                }

                QuestWindowEvent.CLOSE -> {
                    //TODO:IMPLEMENT!!
                    //mVoiceCenter.stopVoice()
                }

                QuestWindowEvent.CLOSED -> {
                    when (it.windowName) {
                        AnswerWindow.Type.RIGHT.name -> {
                            CommitCurrentTransitionUseCase(questContext.repository,
                                    eventSender, questContext.schedulerProvider).use()
                        }
                        else -> GetCurrentTransitionUseCase(questContext.repository,
                                questContext.schedulerProvider).use { currentTransition ->
                            if (currentTransition.data == QUEST) {
                                questContext.resumeQuest()
                            }
                        }
                    }
                }
            }
        }
        listenQuest { quest ->
            questContext.questSeriesLength { questSeriesLength ->
                questContext.questSeriesCounterValue { questSeriesCounterValue ->
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
                    val questName = quest.questType.getRepresentation().getString(questContext)
                    viewHolder.titleView.text = questName
                    viewHolder.titleViewSecondary.text = questSeriesString
                }
            }
        }
        listenPupilStatistics { pupilStatistics ->
            currentLevel { currentLevel ->
                allPointsLevel { allPoints ->
                    val scoreOnLevel = pupilStatistics.score - allPoints
                    viewHolder.pupilProgress.setValue(scoreOnLevel * 100 / currentLevel.pointsWeight.toFloat())
                    viewHolder.pupilLevel.text = currentLevel.toString()
                }
            }
        }
    }

    override fun deactivate() {
        with(viewHolder) {
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

    override fun attachView() {
        createAndAttachQuestView()
    }

    private fun createAndAttachQuestView() =
            QuestVisualBuilder.build(questContext).use {
                questMediatorFacadeStack[0] = it
                it.view?.also {
                    it.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                        override fun onViewAttachedToWindow(view: View) {
                            questContext.attachQuest()
                            view.removeOnAttachStateChangeListener(this)
                        }

                        override fun onViewDetachedFromWindow(view: View) {
                        }
                    })
                    viewHolder.attachQuestView(it)
                }
            }

    class LockScreenQuestViewHolder internal constructor(override var questContext: QuestContext) :
            ViewHolder(questContext, R.layout.layout_lock_screen_quest_view_container),
            ILockScreenContentViewHolder, IQuestViewHolder, IQuestContextProvider {

        override var disposable: CompositeDisposable = CompositeDisposable()

        internal val menuButton: ImageView = view.findViewById(R.id.btn_menu) as ImageView
        internal val titleView: TextView = view.findViewById(R.id.tv_title) as TextView
        internal val titleViewSecondary: TextView = view.findViewById(R.id.tv_message_secondary) as TextView
        internal val pupilLevel: TextView = view.findViewById(R.id.tv_pupil_level) as TextView
        private var titleContainer: ViewGroup = view.findViewById(R.id.container_title) as ViewGroup
        internal val statisticsContainer: ViewGroup = view.findViewById(R.id.container_statistics) as ViewGroup
        internal val delayedPlayContainer: ViewGroup = view.findViewById(R.id.container_delayed_play) as ViewGroup
        private val questContentContainer: ViewSwitcher = view.findViewById(R.id.container_content_quest) as ViewSwitcher
        internal val pupilProgress: CircleProgressView = view.findViewById(R.id.pupil_progress) as CircleProgressView
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
            delayedPlayContainer.visibility = INVISIBLE
        }

        override fun detachQuestView() {
            questContentContainer.removeAllViews()
            dispose()
        }

        internal fun updateViewVisibility() {
            questHasState(QuestState.DELAYED_PLAY) { delayed ->
                questHasState(QuestState.PLAYED) { played ->
                    val showDelayedPlayContainer = delayed && !played
                    delayedPlayContainer.visibility = if (showDelayedPlayContainer) {
                        fadeAnimation(delayedPlayContainer, false, SHORT.get(questContext))
                        VISIBLE
                    } else INVISIBLE
                }
            }
        }
    }
}
