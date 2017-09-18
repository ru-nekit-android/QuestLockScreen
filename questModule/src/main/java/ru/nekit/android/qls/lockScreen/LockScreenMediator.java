package ru.nekit.android.qls.lockScreen;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.content.AbstractLockScreenContentMediator;
import ru.nekit.android.qls.lockScreen.content.ILockScreenContentContainerViewHolder;
import ru.nekit.android.qls.lockScreen.content.LockScreenQuestContentMediator;
import ru.nekit.android.qls.lockScreen.content.SupportContentMediator;
import ru.nekit.android.qls.lockScreen.service.LockScreenService;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.statistics.QuestStatistics;
import ru.nekit.android.qls.utils.KeyboardHost;
import ru.nekit.android.qls.utils.RevealAnimator;
import ru.nekit.android.qls.utils.RevealPoint;
import ru.nekit.android.qls.utils.ScreenHost;
import ru.nekit.android.qls.utils.TimeUtils;
import ru.nekit.android.qls.utils.ViewHolder;

import static android.content.Intent.ACTION_TIME_TICK;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static ru.nekit.android.qls.lockScreen.TransitionChoreograph.EVENT_TRANSITION_CHANGED;
import static ru.nekit.android.qls.lockScreen.TransitionChoreograph.Transition.QUEST;
import static ru.nekit.android.qls.lockScreen.window.Window.EVENT_WINDOW_CLOSED;
import static ru.nekit.android.qls.lockScreen.window.Window.EVENT_WINDOW_OPEN;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_CREATE;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_PAUSE;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_RESUME;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_TIC_TAC;

public class LockScreenMediator implements EventBus.IEventHandler, View.OnLayoutChangeListener {

    public static final String EVENT_SHOW = "event_show";

    public static final String ACTION_DETACH = "action_detach";
    public static final String ACTION_CLOSE = "action_close";

    @NonNull
    private final QuestContext mQuestContext;
    @NonNull
    private final LockScreenViewHolder mViewHolder;
    @NonNull
    private final StyleParameters mStyleParameters;
    @NonNull
    private final WindowManager mWindowManager;
    @NonNull
    private final TransitionChoreograph mTransitionChoreograph;
    private StatusBarViewHolder mStatusBarViewHolder;
    private AbstractLockScreenContentMediator mCurrentContentMediator, mPreviousContentMediator;
    private QuestStatistics mCurrentQuestStatistics;
    private LayoutParams mLockScreenLayoutParams;
    private Animator.AnimatorListener mAnimatorListenerOnClose = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            fadeAnimation(mStatusBarViewHolder.getView(), true);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animation.removeAllListeners();
            mQuestContext.getEventBus().sendEvent(LockScreenService.ACTION_HIDE_LOCK_SCREEN_VIEW);
            detachView();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            animation.removeAllListeners();
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    public LockScreenMediator(@NonNull final QuestContext questContext) {
        mQuestContext = questContext;
        mWindowManager = (WindowManager) questContext.getSystemService(Context.WINDOW_SERVICE);
        mTransitionChoreograph = new TransitionChoreograph(questContext);
        mViewHolder = new LockScreenViewHolder(questContext);
        mViewHolder.getView().addOnLayoutChangeListener(this);
        mViewHolder.outAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mViewHolder.setInAnimation(AnimationUtils.loadAnimation(mQuestContext,
                        R.anim.slide_in));
                mPreviousContentMediator.deactivate();
                mPreviousContentMediator.detachView();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mStyleParameters = new StyleParameters(questContext);
        questContext.getEventBus().handleEvents(this,
                ACTION_DETACH,
                ACTION_CLOSE,
                EVENT_WINDOW_OPEN,
                EVENT_WINDOW_CLOSED,
                EVENT_TRANSITION_CHANGED,
                EVENT_TIC_TAC,
                EVENT_QUEST_CREATE,
                EVENT_QUEST_PAUSE,
                EVENT_QUEST_RESUME,
                ACTION_TIME_TICK
        );
    }

    private void fadeAnimation(@Nullable View view, boolean fadeOut) {
        if (view != null) {
            if (!fadeOut) {
                view.setAlpha(0);
            }
            view.animate().alpha(fadeOut ? 0 : 1).setDuration(
                    mQuestContext.getResources().getInteger(R.integer.short_animation_duration));
        }
    }

    private void close() {
        close(RevealPoint.POSITION_MIDDLE_CENTER);
    }

    private void close(String position) {
        startWindowAnimation(position, false, mAnimatorListenerOnClose);
    }

    private boolean isLandscape() {
        return is10InchTabletDevice();
    }

    private boolean is10InchTabletDevice() {
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float widthDpi = metrics.xdpi;
        float heightDpi = metrics.ydpi;
        float widthInches = widthPixels / widthDpi;
        float heightInches = heightPixels / heightDpi;
        double diagonalInches = Math.sqrt(
                (widthInches * widthInches)
                        + (heightInches * heightInches));
        return diagonalInches >= 9;
    }

    private int getStatusBarHeight() {
        return mQuestContext.getResources().getDimensionPixelSize(R.dimen.status_bar_height);
    }

    public void attachView() {
        if (!isViewAttached()) {
            boolean isLandscape = isLandscape();
            Point screenSize = ScreenHost.getScreenSize(mQuestContext);
            int statusBaHeight = isLandscape ? 0 : getStatusBarHeight();
            int x = Math.max(screenSize.x, screenSize.y);
            int y = Math.min(screenSize.x, screenSize.y);
            int height = y - (isLandscape ? statusBaHeight : 0);
            int width = isLandscape ? Math.min(x,
                    (int) (height * ((float) y) / x * 1.3)) :
                    MATCH_PARENT;
            mLockScreenLayoutParams = new LayoutParams(
                    width,
                    MATCH_PARENT,
                    LayoutParams.TYPE_SYSTEM_ERROR,
                    LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | LayoutParams.FLAG_FULLSCREEN
                            | LayoutParams.FLAG_DISMISS_KEYGUARD
                            | LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                            | LayoutParams.FLAG_DIM_BEHIND
                            | LayoutParams.FLAG_HARDWARE_ACCELERATED
                    ,
                    PixelFormat.TRANSLUCENT);
            mLockScreenLayoutParams.screenBrightness = 1;
            //mLockScreenLayoutParams.height = 0;
            mLockScreenLayoutParams.gravity = (isLandscape ? Gravity.CENTER_HORIZONTAL : Gravity.LEFT);
            mLockScreenLayoutParams.screenOrientation = isLandscape ?
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            mLockScreenLayoutParams.dimAmount = mStyleParameters.dimAmount;
            mLockScreenLayoutParams.y = 0;
            mWindowManager.addView(mViewHolder.getView(), mLockScreenLayoutParams);
            attachStatusBar();
            TransitionChoreograph.Transition startTransition;
            while ((startTransition = mTransitionChoreograph.generateNextTransition()) == null) {
                mTransitionChoreograph.reset();
            }
            mTransitionChoreograph.saveCurrentTransition(startTransition);
            mTransitionChoreograph.goCurrentTransition();
            mQuestContext.getEventBus().sendEvent(EVENT_SHOW);
        }
    }

    private void attachStatusBar() {
        if (!isLandscape()) {
            mStatusBarViewHolder = new StatusBarViewHolder(mQuestContext);
            LayoutParams statusBarLayoutParams = new LayoutParams();
            statusBarLayoutParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
            statusBarLayoutParams.gravity = Gravity.TOP;
            statusBarLayoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE |
                    LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            statusBarLayoutParams.format = PixelFormat.TRANSLUCENT;
            statusBarLayoutParams.width = MATCH_PARENT;
            statusBarLayoutParams.height = getStatusBarHeight();
            mStatusBarViewHolder.timerContainer.setVisibility(INVISIBLE);
            mWindowManager.addView(mStatusBarViewHolder.getView(), statusBarLayoutParams);
            updateTimeClockText();
        }
    }

    private boolean isViewAttached() {
        return mViewHolder.getView().getParent() != null;
    }

    private void startWindowAnimation(String position, boolean openAnimation,
                                      @Nullable Animator.AnimatorListener listener) {
        final Animator animator = RevealAnimator.getRevealAnimator(mQuestContext,
                mViewHolder.container, position, new AnticipateOvershootInterpolator(),
                mStyleParameters.animationDuration, !openAnimation);
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.start();
    }

    public void deactivate() {
        mTransitionChoreograph.destroy();
        if (mCurrentContentMediator != null) {
            mCurrentContentMediator.deactivate();
        }
        mViewHolder.outAnimation.setAnimationListener(null);
        mQuestContext.getEventBus().stopHandleEvents(this);
    }

    public void detachView() {
        if (isViewAttached()) {
            mViewHolder.titleContainer.removeAllViews();
            mViewHolder.contentContainer.removeAllViews();
            mWindowManager.removeView(mViewHolder.getView());
            if (mCurrentContentMediator != null) {
                mCurrentContentMediator.detachView();
            }
            if (mStatusBarViewHolder != null) {
                mWindowManager.removeView(mStatusBarViewHolder.getView());
            }
        }
    }

    @Override
    public void onEvent(@NonNull Intent intent) {
        String action = intent.getAction();
        switch (action) {

            case ACTION_DETACH:

                detachView();

                break;

            case ACTION_CLOSE:

                close();

                break;

            case EVENT_WINDOW_OPEN:

                mQuestContext.pauseQuest();

                break;

            case EVENT_QUEST_RESUME:

                fadeAnimation(mStatusBarViewHolder.timerContainer, false);

                break;

            case EVENT_WINDOW_CLOSED:

                if (mTransitionChoreograph.getCurrentTransition() == QUEST) {
                    mQuestContext.resumeQuest();
                }

                break;

            case EVENT_QUEST_PAUSE:

                fadeAnimation(mStatusBarViewHolder.timerContainer, true);

                break;

            case EVENT_QUEST_CREATE:

                mCurrentQuestStatistics = mQuestContext.getQuestStatistics();
                updateTimerProgress(0);

                break;

            case EVENT_TIC_TAC:

                long sessionTime = intent.getLongExtra(QuestContext.NAME_SESSION_TIME, 0);
                updateTimerProgress(sessionTime);

                break;

            case EVENT_TRANSITION_CHANGED:

                TransitionChoreograph.Transition currentTransition =
                        mTransitionChoreograph.getCurrentTransition(),
                        previousTransition = mTransitionChoreograph.getPreviousTransition();
                if (currentTransition == null) {
                    close();
                } else {
                    if (previousTransition == null
                            || currentTransition != QUEST
                            || currentTransition != previousTransition) {
                        boolean useAnimation = previousTransition != null ||
                                currentTransition == QUEST;
                        mPreviousContentMediator = mCurrentContentMediator;
                        if (currentTransition == QUEST) {
                            mCurrentContentMediator = new LockScreenQuestContentMediator(mQuestContext);
                        } else {
                            mCurrentContentMediator = new SupportContentMediator(mQuestContext);
                        }
                        mCurrentContentMediator.setTransitionChoreograph(mTransitionChoreograph);
                        mViewHolder.switchToContent(mCurrentContentMediator, useAnimation,
                                previousTransition == null && currentTransition == QUEST
                        );
                        mCurrentContentMediator.attachView();
                        KeyboardHost.hideKeyboard(mQuestContext, mViewHolder.getView());
                    }
                    if (currentTransition == QUEST) {
                        mStatusBarViewHolder.timerContainer.setVisibility(VISIBLE);
                        fadeAnimation(mStatusBarViewHolder.timerContainer, false);
                    } else {
                        fadeAnimation(mStatusBarViewHolder.timerContainer, true);
                    }
                }

                break;

            case ACTION_TIME_TICK:

                updateTimeClockText();

                break;

        }
    }

    private void updateTimeClockText() {
        mStatusBarViewHolder.clockView.setText(new SimpleDateFormat("HH:mm",
                Locale.getDefault()).format(TimeUtils.getCurrentTime()));
    }

    private void updateTimerProgress(long sessionTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
        mStatusBarViewHolder.sessionTimeTextView.setText(sdf.format(sessionTime));
        long bestTime = mCurrentQuestStatistics.bestAnswerTime;
        long worstTime = mCurrentQuestStatistics.worseAnswerTime;
        long maxTime = Math.max(1000 * 60, worstTime * 2);
        if (bestTime == Long.MAX_VALUE) {
            bestTime = 0;
        }
        mStatusBarViewHolder.bestTime.setScaleX((float) bestTime / maxTime);
        mStatusBarViewHolder.worstTime.setScaleX((float) worstTime / maxTime);
        mStatusBarViewHolder.sessionTime.setScaleX(Math.min(1, (float) sessionTime / maxTime));
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return getClass().getName();
    }

    @Override
    public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft,
                               int oldTop, int oldRight, int oldBottom) {
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        if (position[1] < getStatusBarHeight()) {
            Point screenSize = ScreenHost.getScreenSize(mQuestContext);
            mLockScreenLayoutParams.height = screenSize.y - getStatusBarHeight();
            mLockScreenLayoutParams.y = getStatusBarHeight();
            mWindowManager.updateViewLayout(mViewHolder.getView(), mLockScreenLayoutParams);
        }
    }

    private static class StyleParameters {

        int animationDuration;
        float dimAmount;

        StyleParameters(@NonNull Context context) {
            TypedArray ta = context.obtainStyledAttributes(R.style.LockScreen_Window,
                    R.styleable.LockScreenWindowStyle);
            dimAmount = ta.getFloat(R.styleable.LockScreenWindowStyle_dimAmount, 1f);
            animationDuration = ta.getInteger(R.styleable.LockScreenWindowStyle_animationDuration,
                    0);
            ta.recycle();
        }
    }

    private static class LockScreenViewHolder extends ViewHolder {

        @NonNull
        final QuestContext questContext;
        @NonNull
        final View container;
        @NonNull
        final ViewFlipper contentContainer, titleContainer;
        @NonNull
        private Animation outAnimation;

        LockScreenViewHolder(@NonNull final QuestContext questContext) {
            super(questContext, R.layout.layout_lock_screen);
            this.questContext = questContext;
            container = mView.findViewById(R.id.container);
            contentContainer = (ViewFlipper) mView.findViewById(R.id.container_content);
            titleContainer = (ViewFlipper) mView.findViewById(R.id.container_title);
            outAnimation = AnimationUtils.loadAnimation(questContext, R.anim.slide_out);
            titleContainer.setOutAnimation(outAnimation);
            contentContainer.setOutAnimation(outAnimation);
        }

        void switchToContent(@NonNull AbstractLockScreenContentMediator contentMediator,
                             boolean useAnimation, boolean useFadeAnimation) {
            boolean contentContainerHasChild = contentContainer.getChildCount() != 0;
            boolean titleContainerHasChild = titleContainer.getChildCount() != 0;
            setInAnimation(AnimationUtils.loadAnimation(questContext,
                    useFadeAnimation ? R.anim.fade_in : R.anim.slide_in));
            ILockScreenContentContainerViewHolder contentViewHolder = contentMediator.getViewHolder();
            View titleContent = contentViewHolder.getTitleContentContainer();
            if (titleContent != null) {
                titleContainer.setVisibility(VISIBLE);
                titleContainer.addView(contentViewHolder.getTitleContentContainer());
            } else {
                titleContainer.setVisibility(GONE);
            }
            View content = contentViewHolder.getContentContainer();
            if (content != null) {
                contentContainer.setVisibility(VISIBLE);
                contentContainer.addView(contentViewHolder.getContentContainer());
            } else {
                contentContainer.setVisibility(GONE);
            }
            if (useAnimation) {
                contentContainer.showNext();
                if (contentContainerHasChild) {
                    contentContainer.removeViewAt(0);
                }
                titleContainer.showNext();
                if (titleContainerHasChild) {
                    titleContainer.removeViewAt(0);
                }
            }
        }

        private void setInAnimation(@NonNull Animation inAnimation) {
            titleContainer.setInAnimation(inAnimation);
            contentContainer.setInAnimation(inAnimation);
        }
    }

    private static class StatusBarViewHolder extends ViewHolder {

        TextView sessionTimeTextView, clockView;
        View sessionTime, bestTime, worstTime, timerContainer;

        StatusBarViewHolder(@NonNull Context context) {
            super(context, R.layout.layout_status_bar);
            sessionTimeTextView = (TextView) getView().findViewById(R.id.tv_session_time);
            clockView = (TextView) getView().findViewById(R.id.tv_clock);
            sessionTime = getView().findViewById(R.id.progress_session_time);
            bestTime = getView().findViewById(R.id.progress_best_time);
            worstTime = getView().findViewById(R.id.progress_worst_time);
            timerContainer = getView().findViewById(R.id.container_timer);
        }
    }
}