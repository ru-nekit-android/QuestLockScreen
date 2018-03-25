package ru.nekit.android.qls.lockScreen

import android.animation.Animator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.View.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.WindowManager.LayoutParams.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.ViewFlipper
import io.reactivex.disposables.CompositeDisposable
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.Transition.*
import ru.nekit.android.qls.domain.useCases.DestroyTransitionUseCase
import ru.nekit.android.qls.domain.useCases.GoStartTransitionUseCase
import ru.nekit.android.qls.domain.useCases.SessionLimiter
import ru.nekit.android.qls.domain.useCases.TransitionChoreographEvent
import ru.nekit.android.qls.lockScreen.LockScreenContentMediatorEvent.ON_CONTENT_SWITCH
import ru.nekit.android.qls.lockScreen.LockScreenMediatorAction.CLOSE
import ru.nekit.android.qls.lockScreen.LockScreenMediatorAction.CLOSE_IMMEDIATELY
import ru.nekit.android.qls.lockScreen.StatusBarViewState.*
import ru.nekit.android.qls.lockScreen.content.IntroductionContentMediator
import ru.nekit.android.qls.lockScreen.content.LockScreenQuestContainerMediator
import ru.nekit.android.qls.lockScreen.content.SupportContentMediator
import ru.nekit.android.qls.lockScreen.content.common.AbstractLockScreenContentMediator
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.QuestContextEvent
import ru.nekit.android.qls.quest.QuestContextEvent.QUEST_PAUSE
import ru.nekit.android.qls.quest.QuestContextEvent.QUEST_RESUME
import ru.nekit.android.qls.quest.providers.IQuestContextProvider
import ru.nekit.android.qls.utils.AnimationUtils.fadeAnimation
import ru.nekit.android.qls.utils.Delay
import ru.nekit.android.qls.utils.ScreenHost
import ru.nekit.android.qls.utils.ViewHolder
import java.text.SimpleDateFormat
import java.util.*

class LockScreenContentMediator(override var questContext: QuestContext) : View.OnLayoutChangeListener,
        IQuestContextProvider {

    override var disposable: CompositeDisposable = CompositeDisposable()

    private lateinit var contentViewHolder: LockScreenContentViewHolder
    private var statusBarViewHolder: StatusBarViewHolder

    private val windowManager: WindowManager = questContext.getSystemService(WINDOW_SERVICE) as WindowManager
    private val contentMediatorStack: Array<AbstractLockScreenContentMediator?> = arrayOf(null, null)

    private val animatorListenerOnClose = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            render(FadeOutViewState)
        }

        override fun onAnimationEnd(animation: Animator) {
            animation.removeAllListeners()
            LockScreen.hide(questContext)
        }

        override fun onAnimationCancel(animation: Animator) {
            animation.removeAllListeners()
        }

        override fun onAnimationRepeat(animation: Animator) {}
    }

    private val timeTickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            render(TimeViewState(questContext.timeProvider.getCurrentTime()))
        }
    }

    init {
        contentViewHolder = LockScreenContentViewHolder(questContext, windowManager,
                object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        if (animation == contentViewHolder.outAnimation) {
                            contentMediatorStack[1]!!.viewHolder
                                    .contentContainer!!.setLayerType(LAYER_TYPE_NONE, null)
                            contentMediatorStack[1]!!.detachView()
                        } else if (animation == contentViewHolder.inAnimation) {
                            contentMediatorStack[0]!!.viewHolder.contentContainer!!
                                    .setLayerType(LAYER_TYPE_NONE, null)
                            sendEvent(ON_CONTENT_SWITCH)
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
        contentViewHolder.view.addOnLayoutChangeListener(this)
        statusBarViewHolder = StatusBarViewHolder(questContext, windowManager)
        questContext.registerReceiver(timeTickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
        listenForEvent(LockScreenMediatorAction::class.java) { action ->
            when (action) {
                CLOSE_IMMEDIATELY ->
                    detachView()
                CLOSE ->
                    closeView()
            }
        }
        listenForEvent(QuestContextEvent::class.java) { event ->
            when (event) {
                QUEST_PAUSE ->
                    render(FadeOutViewState)
                QUEST_RESUME ->
                    render(FadeInViewState)
                else -> {
                }
            }
        }
        listenForEvent(TransitionChoreographEvent::class.java) { event ->
            if (event.currentTransition == null) {
                closeView()
            } else {
                val currentTransition = event.currentTransition
                val previousTransition = event.previousTransition
                val questTransition = currentTransition == QUEST
                if (previousTransition == null ||
                        !questTransition ||
                        currentTransition != previousTransition) {
                    val useAnimation = previousTransition != null || questTransition
                    contentMediatorStack[1] = contentMediatorStack[0]
                    contentMediatorStack[1]?.apply {
                        deactivate()
                        viewHolder.contentContainer?.setLayerType(LAYER_TYPE_HARDWARE, null)
                    }
                    contentMediatorStack[0] = when (currentTransition) {
                        QUEST -> LockScreenQuestContainerMediator(questContext)
                        INTRODUCTION -> IntroductionContentMediator(questContext)
                        LEVEL_UP, ADVERT -> SupportContentMediator(questContext)
                        else -> TODO()
                    }
                    render(LockScreenViewViewState.SwitchContentViewState(contentMediatorStack[0]!!,
                            useAnimation, previousTransition == null && questTransition))
                }
                if (questTransition) {
                    render(VisibleViewState(true))
                }
                render(if (!questTransition) FadeOutViewState else FadeInViewState)
            }
        }
        listenSessionTime { sessionTime ->
            questStatisticsReport { report ->
                var bestTime = report.bestAnswerTime
                val worstTime = report.worseAnswerTime
                val maxTimeDefault = Math.max(1000 * 60, worstTime * 2)
                val maxTime = if (sessionTime < maxTimeDefault) maxTimeDefault else
                    SessionLimiter.MAX_SESSION_TIME
                if (bestTime == Long.MAX_VALUE) {
                    bestTime = 0
                }
                render(SessionTimeViewState(sessionTime, bestTime, worstTime, maxTime))
            }
        }
        questContext.apply {
            GoStartTransitionUseCase(repository, eventSender,
                    schedulerProvider).use {
                sendEvent(LockScreenContentMediatorEvent.ON_INIT)
            }
        }
    }

    fun render(viewState: IViewState) {
        when (viewState) {
            IViewState.DetachViewState -> {
                contentViewHolder.render(viewState)
                statusBarViewHolder.render(viewState)
            }
            is IViewState.AttachViewState -> {
                contentViewHolder.render(viewState)
                statusBarViewHolder.render(viewState)
                render(TimeViewState(questContext.timeProvider.getCurrentTime()))
            }
            is StatusBarViewState -> statusBarViewHolder.render(viewState)
            is LockScreenViewViewState -> contentViewHolder.render(viewState)
        }
    }

    private val statusBarHeight: Int
        get() = questContext.resources.getDimensionPixelSize(R.dimen.status_bar_height)

    private val isLandscape: Boolean
        get() = is10InchTabletDevice

    private val is10InchTabletDevice: Boolean
        get() {
            val diagonalInches = DisplayMetrics().let {
                with(it) {
                    windowManager.defaultDisplay.getMetrics(it)
                    val widthPixels = widthPixels
                    val heightPixels = heightPixels
                    val widthDpi = xdpi
                    val heightDpi = ydpi
                    val widthInches = widthPixels / widthDpi
                    val heightInches = heightPixels / heightDpi
                    Math.sqrt((widthInches * widthInches + heightInches * heightInches).toDouble())
                }
            }
            return diagonalInches >= 9
        }


    fun attachView() {
        render(IViewState.AttachViewState(isLandscape, statusBarHeight))
    }

    fun deactivate() {
        DestroyTransitionUseCase(questContext.repository, questContext.schedulerProvider)
        render(IViewState.DeactiveViewState)
        contentMediatorStack[0]?.deactivate()
        contentViewHolder.view.removeOnLayoutChangeListener(this)
        questContext.unregisterReceiver(timeTickReceiver)
        dispose()
    }

    fun detachView() {
        render(IViewState.DetachViewState)
        contentMediatorStack[0]?.detachView()
    }

    private fun startWindowAnimation(listener: Animator.AnimatorListener?) =
            render(LockScreenViewViewState.FadeOutViewState(listener))

    private fun closeView() = startWindowAnimation(animatorListenerOnClose)

    override fun onLayoutChange(view: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int,
                                oldTop: Int, oldRight: Int, oldBottom: Int) {
        render(LockScreenViewViewState.UpdateSizeViewState(statusBarHeight))
    }

    private class LockScreenContentViewHolder internal constructor(private val questContext: QuestContext,
                                                                   private val windowManager: WindowManager,
                                                                   private val animationListener: Animation.AnimationListener) :
            ViewHolder(questContext, R.layout.layout_lock_screen) {

        private val container: View = view.findViewById(R.id.container)
        private val contentContainer: ViewFlipper = view.findViewById(R.id.container_content) as ViewFlipper
        private val titleContainer: ViewFlipper = view.findViewById(R.id.container_title) as ViewFlipper
        var outAnimation: Animation = AnimationUtils.loadAnimation(questContext, R.anim.slide_horizontal_out)
        var inAnimation: Animation? = null

        init {
            titleContainer.outAnimation = outAnimation
            contentContainer.outAnimation = outAnimation
            outAnimation.setAnimationListener(animationListener)
        }

        internal fun switchToContent(lockScreenContentMediator: AbstractLockScreenContentMediator,
                                     useAnimation: Boolean, useFadeAnimation: Boolean) {
            val contentContainerHasChild = contentContainer.childCount != 0
            val titleContainerHasChild = titleContainer.childCount != 0
            if (inAnimation != null) {
                inAnimation?.cancel()
                inAnimation?.setAnimationListener(null)
            }
            inAnimation = AnimationUtils.loadAnimation(questContext,
                    if (useFadeAnimation) R.anim.fade_in else R.anim.slide_horizontal_in).apply {
                setAnimationListener(animationListener)
                titleContainer.inAnimation = this
                contentContainer.inAnimation = this
            }
            val contentViewHolder = lockScreenContentMediator.viewHolder
            val titleContent = contentViewHolder.titleContentContainer
            if (titleContent != null) {
                titleContainer.visibility = VISIBLE
                titleContainer.addView(contentViewHolder.titleContentContainer)
            } else {
                titleContainer.visibility = GONE
            }
            val content = contentViewHolder.contentContainer
            if (content != null) {
                contentContainer.visibility = VISIBLE
                contentContainer.addView(contentViewHolder.contentContainer)
            } else {
                contentContainer.visibility = GONE
            }
            if (useAnimation) {
                view.setLayerType(LAYER_TYPE_HARDWARE, null)
                contentContainer.showNext()
                if (contentContainerHasChild) {
                    contentContainer.removeViewAt(0)
                }
                titleContainer.showNext()
                if (titleContainerHasChild) {
                    titleContainer.removeViewAt(0)
                }
            }
        }

        private lateinit var lockScreenLayoutParams: LayoutParams

        val isViewAttached: Boolean
            get() = view.parent != null

        inner class StyleParameters internal constructor(context: Context) {

            internal var animationDuration: Int = 0
            internal var dimAmount: Float = 0F

            init {
                val ta = context.obtainStyledAttributes(R.style.LockScreen_Window,
                        R.styleable.LockScreenWindowStyle)
                dimAmount = ta.getFloat(R.styleable.LockScreenWindowStyle_dimAmount, 1f)
                animationDuration = ta.getInteger(R.styleable.LockScreenWindowStyle_animationDuration,
                        0)
                ta.recycle()
            }
        }

        private val styleParameters: StyleParameters = StyleParameters(questContext)

        fun render(viewState: IViewState) {
            when (viewState) {
                is IViewState.AttachViewState -> {
                    if (!isViewAttached) {
                        val statusBarHeight = viewState.statusBarHeight
                        val isLandscape = viewState.isLandscape
                        val screenSize = ScreenHost.getScreenSize(questContext)
                        val statusBaHeight = if (isLandscape) 0 else statusBarHeight
                        val x = Math.max(screenSize.x, screenSize.y)
                        val y = Math.min(screenSize.x, screenSize.y)
                        val height = y - if (isLandscape) statusBaHeight else 0
                        val width = if (isLandscape)
                            Math.min(x, (height * y.toFloat() / x * 1.3).toInt())
                        else
                            MATCH_PARENT
                        LayoutParams(
                                width,
                                MATCH_PARENT,
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                    TYPE_APPLICATION_OVERLAY
                                else {
                                    @Suppress("DEPRECATION")
                                    TYPE_SYSTEM_ERROR
                                },
                                FLAG_SHOW_WHEN_LOCKED
                                        or FLAG_FULLSCREEN
                                        or FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                                        or FLAG_DIM_BEHIND
                                        or FLAG_HARDWARE_ACCELERATED,
                                PixelFormat.TRANSLUCENT).apply {
                            lockScreenLayoutParams = this
                            screenBrightness = 1f
                            gravity = if (isLandscape) Gravity.CENTER_HORIZONTAL else Gravity.LEFT
                            screenOrientation = if (isLandscape)
                                SCREEN_ORIENTATION_LANDSCAPE
                            else
                                SCREEN_ORIENTATION_PORTRAIT

                            dimAmount = styleParameters.dimAmount
                            this.y = 0
                            windowManager.addView(view, this)
                        }
                    }
                }
                is LockScreenViewViewState.UpdateSizeViewState -> {
                    val statusBarHeight = viewState.statusBarHeight
                    val position = IntArray(2)
                    view.getLocationOnScreen(position)
                    if (position[1] < statusBarHeight) {
                        val screenSize = ScreenHost.getScreenSize(questContext)
                        lockScreenLayoutParams.height = screenSize.y - statusBarHeight
                        lockScreenLayoutParams.y = statusBarHeight
                        windowManager.updateViewLayout(view, lockScreenLayoutParams)
                    }
                }
                is LockScreenViewViewState.FadeOutViewState ->
                    container.animate().setListener(viewState.listener).withLayer().alpha(0f).start()
                is LockScreenViewViewState.SwitchContentViewState -> {
                    viewState.lockScreenContentMediator.apply {
                        switchToContent(this,
                                viewState.useAnimation,
                                viewState.useFadeAnimation)
                        attachView()
                    }
                }
                IViewState.DeactiveViewState -> {
                    outAnimation.setAnimationListener(null)
                    inAnimation!!.setAnimationListener(null)
                }
                IViewState.DetachViewState -> {
                    if (isViewAttached) {
                        titleContainer.removeAllViews()
                        contentContainer.removeAllViews()
                        windowManager.removeView(view)
                    }
                }
            }
        }
    }

    private class StatusBarViewHolder internal constructor(private val questContext: QuestContext,
                                                           private val windowManager: WindowManager) :
            ViewHolder(questContext, R.layout.layout_status_bar) {

        private var sessionTimeView: TextView = view.findViewById(R.id.tv_session_time) as TextView
        private var timeView: TextView = view.findViewById(R.id.tv_clock) as TextView
        private var progressSessionTimeView: View = view.findViewById(R.id.progress_session_time)
        private var bestTimeView: View = view.findViewById(R.id.progress_best_time)
        private var worstTimeView: View = view.findViewById(R.id.progress_worst_time)
        private var timerContainer: View = view.findViewById(R.id.container_timer)

        fun render(viewState: IViewState) {
            when (viewState) {
                is IViewState.AttachViewState -> {
                    if (!viewState.isLandscape) {
                        LayoutParams().apply {

                            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                TYPE_APPLICATION_OVERLAY
                            else
                                @Suppress("DEPRECATION")
                                TYPE_SYSTEM_ERROR
                            gravity = Gravity.TOP
                            flags = FLAG_NOT_FOCUSABLE or
                                    FLAG_NOT_TOUCH_MODAL or
                                    FLAG_LAYOUT_IN_SCREEN
                            format = PixelFormat.TRANSLUCENT
                            width = MATCH_PARENT
                            height = viewState.statusBarHeight
                            timerContainer.visibility = INVISIBLE
                            windowManager.addView(view, this)
                        }
                    }
                }
                is StatusBarViewState.VisibleViewState -> {
                    timerContainer.visibility = if (viewState.value) VISIBLE else INVISIBLE
                    bestTimeView.scaleX = 0F
                    worstTimeView.scaleX = 0F
                    progressSessionTimeView.scaleX = 0F
                    updateSessionTimeView(0)
                }
                is StatusBarViewState.TimeViewState ->
                    timeView.text = SimpleDateFormat(context.getString(R.string.clock_formatter),
                            Locale.getDefault()).format(viewState.time)
                is StatusBarViewState.SessionTimeViewState -> {
                    bestTimeView.scaleX = viewState.bestTime.toFloat() / viewState.maxTime
                    worstTimeView.scaleX = viewState.worstTime.toFloat() / viewState.maxTime
                    progressSessionTimeView.scaleX = Math.min(1f, viewState.sessionTime.toFloat() / viewState.maxTime)
                    updateSessionTimeView(viewState.sessionTime)
                }
                IViewState.DetachViewState ->
                    if (view.parent != null)
                        windowManager.removeView(view)
                FadeInViewState ->
                    fadeAnimation(timerContainer, false, Delay.SHORT.get(questContext))
                FadeOutViewState ->
                    fadeAnimation(timerContainer, true, Delay.SHORT.get(questContext))
            }
        }

        private fun updateSessionTimeView(sessionTime: Long) {
            SimpleDateFormat(questContext.getString(R.string.quest_timer_formatter),
                    Locale.getDefault()).apply {
                sessionTimeView.text = format(sessionTime)
            }
        }
    }
}

sealed class StatusBarViewState : IViewState {
    data class SessionTimeViewState(val sessionTime: Long,
                                    val bestTime: Long,
                                    val worstTime: Long,
                                    val maxTime: Long) : StatusBarViewState()

    data class TimeViewState(val time: Long) : StatusBarViewState()
    data class VisibleViewState(val value: Boolean) : StatusBarViewState()
    object FadeOutViewState : StatusBarViewState()
    object FadeInViewState : StatusBarViewState()
}

sealed class LockScreenViewViewState : IViewState {
    data class SwitchContentViewState(val lockScreenContentMediator: AbstractLockScreenContentMediator,
                                      val useAnimation: Boolean,
                                      val useFadeAnimation: Boolean) : LockScreenViewViewState()

    data class FadeOutViewState(val listener: Animator.AnimatorListener?) : LockScreenViewViewState()
    data class UpdateSizeViewState(val statusBarHeight: Int) : LockScreenViewViewState()
}

interface IViewState {
    object DetachViewState : IViewState
    object DeactiveViewState : IViewState
    data class AttachViewState(val isLandscape: Boolean, val statusBarHeight: Int) : IViewState
}

enum class LockScreenContentMediatorEvent : IEvent {

    ON_INIT,
    ON_CONTENT_SWITCH;

    override val eventName = "${javaClass.name}::$name"

}

enum class LockScreenMediatorAction : IEvent {

    CLOSE_IMMEDIATELY,
    CLOSE;

    override val eventName = "${javaClass.name}::$name"

}
