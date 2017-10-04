package ru.nekit.android.qls.lockScreen.content;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import at.grabner.circleprogress.CircleProgressView;
import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.LockScreenMediator;
import ru.nekit.android.qls.lockScreen.TransitionChoreograph.Transition;
import ru.nekit.android.qls.lockScreen.window.Window;
import ru.nekit.android.qls.quest.IQuestViewHolder;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestVisualBuilder;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgram;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgramLevel;
import ru.nekit.android.qls.quest.window.MenuWindowMediator;
import ru.nekit.android.qls.quest.window.PupilStatisticsWindowMediator;
import ru.nekit.android.qls.quest.window.RightAnswerWindow;
import ru.nekit.android.qls.quest.window.RightAnswerWindowContentViewHolder;
import ru.nekit.android.qls.quest.window.WrongAnswerWindowContentViewHolder;
import ru.nekit.android.qls.utils.KeyboardHost;
import ru.nekit.android.qls.utils.ViewHolder;

import static android.view.View.INVISIBLE;
import static android.view.View.LAYER_TYPE_NONE;
import static android.view.View.VISIBLE;
import static ru.nekit.android.qls.lockScreen.TransitionChoreograph.EVENT_TRANSITION_CHANGED;
import static ru.nekit.android.qls.lockScreen.TransitionChoreograph.Transition.QUEST;
import static ru.nekit.android.qls.lockScreen.window.Window.EVENT_WINDOW_CLOSED;
import static ru.nekit.android.qls.lockScreen.window.Window.EVENT_WINDOW_CLOSED_INTERNAL;
import static ru.nekit.android.qls.lockScreen.window.Window.closeAllWindows;
import static ru.nekit.android.qls.lockScreen.window.Window.getWindowStack;
import static ru.nekit.android.qls.lockScreen.window.Window.open;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.DELAYED_START;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.STARTED;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_WRONG_ANSWER;

public class LockScreenQuestContentMediator extends AbstractLockScreenContentMediator
        implements View.OnClickListener, EventBus.IEventHandler {

    public static final String ACTION_SHOW_RIGHT_ANSWER_WINDOW = "action_show_right_answer_window";

    @NonNull
    private final QuestContext mQuestContext;
    private QuestVisualBuilder mCurrentQuestVisualBuilder, mPreviousQuestVisualBuilder;
    private QuestContentViewHolder mViewHolder;

    public LockScreenQuestContentMediator(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mViewHolder = new QuestContentViewHolder(questContext);
        mViewHolder.menuButton.setOnClickListener(this);
        mViewHolder.statisticsContainer.setOnClickListener(this);
        mViewHolder.delayedStartContainer.setOnClickListener(this);
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (animation == mViewHolder.inAnimation) {
                    updateQuestTitle();
                    mCurrentQuestVisualBuilder.getView().setLayerType(LAYER_TYPE_NONE, null);
                    mViewHolder.updateViewVisibility(mQuestContext);
                } else if (animation == mViewHolder.outAnimation) {
                    mPreviousQuestVisualBuilder.getView().setLayerType(LAYER_TYPE_NONE, null);
                    mPreviousQuestVisualBuilder.detachView();
                    mPreviousQuestVisualBuilder = null;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };
        mViewHolder.inAnimation.setAnimationListener(animationListener);
        mViewHolder.outAnimation.setAnimationListener(animationListener);
        questContext.getEventBus().handleEvents(this,
                EVENT_TRANSITION_CHANGED,
                ACTION_SHOW_RIGHT_ANSWER_WINDOW,
                EVENT_WRONG_ANSWER,
                EVENT_WINDOW_CLOSED,
                EVENT_WINDOW_CLOSED_INTERNAL
        );
        mCurrentQuestVisualBuilder = new QuestVisualBuilder(questContext);
        mCurrentQuestVisualBuilder.create(mViewHolder.questContentContainer);
        updatePupilStatisticsView();
    }

    private void updateQuestTitle() {
        int questSeriesLength = mQuestContext.getQuestSeriesLength();
        String questSeriesString = questSeriesLength > 1 ? String.format(mQuestContext.getString(R.string.quest_series),
                questSeriesLength - mTransitionChoreograph.getQuestSeriesCounterValue() + 1,
                questSeriesLength) : "";
        Spannable questSeriesSpan = new SpannableString(questSeriesString);
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(
                mQuestContext.getResources().getDimensionPixelSize(R.dimen.quest_series_size), false);
        questSeriesSpan.setSpan(sizeSpan, 0, questSeriesString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        String questName = mQuestContext.getQuest().getQuestType().getTitle(mQuestContext);
        mViewHolder.titleView.setText(questName);
        mViewHolder.titleViewRight.setText(questSeriesString);
    }

    private void updatePupilStatisticsView() {
        int score = mQuestContext.getPupilStatistics().score;
        QuestTrainingProgram qtp = mQuestContext.getQuestTrainingProgram();
        QuestTrainingProgramLevel currentQTPLevel = mQuestContext.getQTPLevel();
        int scoreOnLevel;
        if (currentQTPLevel.getIndex() == 0) {
            scoreOnLevel = score;
        } else {
            QuestTrainingProgramLevel previousLevel =
                    qtp.getLevelByIndex(currentQTPLevel.getIndex() - 1);
            scoreOnLevel = score - qtp.getLevelAllPoints(previousLevel);
        }
        mViewHolder.pupilProgress.setValue(scoreOnLevel * 100 /
                (float) currentQTPLevel.getPointsWeight());
        mViewHolder.pupilLevel.setText(currentQTPLevel.toString());
    }

    @NonNull
    @Override
    public ILockScreenContentContainerViewHolder getContentContainerViewHolder() {
        return mViewHolder;
    }

    private boolean allowToClose() {
        return getWindowStack().isEmpty();
    }

    @Override
    public void deactivate() {
        mQuestContext.getEventBus().stopHandleEvents(this);
        mViewHolder.menuButton.setOnClickListener(null);
        mViewHolder.statisticsContainer.setOnClickListener(null);
        mViewHolder.inAnimation.setAnimationListener(null);
        mViewHolder.outAnimation.setAnimationListener(null);
        if (mPreviousQuestVisualBuilder != null) {
            mPreviousQuestVisualBuilder.deactivate();
        }
        mCurrentQuestVisualBuilder.deactivate();
    }

    @Override
    public void detachView() {
        closeAllWindows();
        if (mPreviousQuestVisualBuilder != null) {
            mPreviousQuestVisualBuilder.detachView();
        }
        mCurrentQuestVisualBuilder.detachView();
        mViewHolder.detachQuestView();
    }

    @Override
    public void attachView() {
        updateQuestTitle();
        mViewHolder.updateViewVisibility(mQuestContext);
        attachQuestView();
    }

    private void attachQuestView() {
        mViewHolder.attachQuestView(mCurrentQuestVisualBuilder.getView());
        mQuestContext.createAndStartQuestIfAble();
    }

    @Override
    public void onClick(View view) {
        if (view == mViewHolder.statisticsContainer) {
            KeyboardHost.hideKeyboard(mQuestContext, view, new Runnable() {
                @Override
                public void run() {
                    PupilStatisticsWindowMediator.openWindow(mQuestContext);
                }
            });
        } else if (view == mViewHolder.menuButton) {
            KeyboardHost.hideKeyboard(mQuestContext, view, new Runnable() {
                @Override
                public void run() {
                    MenuWindowMediator.openWindow(mQuestContext);
                }
            });
        } else if (view == mViewHolder.delayedStartContainer) {
            if (mQuestContext.startQuestIfAble()) {
                mViewHolder.updateViewVisibility(mQuestContext);
            }
        }
    }

    @Override
    public void onEvent(@NonNull final Intent intent) {
        String action = intent.getAction();
        switch (action) {

            case ACTION_SHOW_RIGHT_ANSWER_WINDOW:

                KeyboardHost.hideKeyboard(mQuestContext, mViewHolder.getView(), new Runnable() {
                    @Override
                    public void run() {
                        updatePupilStatisticsView();
                        RightAnswerWindowContentViewHolder content =
                                new RightAnswerWindowContentViewHolder(mQuestContext,
                                        R.layout.wc_right_answer);
                        new RightAnswerWindow.Builder(mQuestContext).
                                setContent(content).
                                setStyle(R.style.Window_RightAnswer).
                                open();
                    }
                });

                break;

            case EVENT_WRONG_ANSWER:

                open(mQuestContext,
                        new WrongAnswerWindowContentViewHolder(mQuestContext),
                        R.style.Window_WrongAnswer
                );

                break;

            case EVENT_WINDOW_CLOSED:

                if (allowToClose()) {
                    mQuestContext.getEventBus().sendEvent(LockScreenMediator.ACTION_CLOSE);
                }

                break;

            case EVENT_WINDOW_CLOSED_INTERNAL:

                if (intent.getSerializableExtra(Window.VALUE_WINDOW_CLASS).equals(RightAnswerWindow.class)) {
                    mTransitionChoreograph.goCurrentTransition();
                }

                break;

            case EVENT_TRANSITION_CHANGED:

                Transition transition = mTransitionChoreograph.getCurrentTransition();
                if (transition == QUEST) {
                    //remember current as previous
                    mPreviousQuestVisualBuilder = mCurrentQuestVisualBuilder;
                    mPreviousQuestVisualBuilder.deactivate();
                    mCurrentQuestVisualBuilder = new QuestVisualBuilder(mQuestContext);
                    mCurrentQuestVisualBuilder.create(mViewHolder.questContentContainer);
                    attachQuestView();
                }

                break;

        }
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return getClass().getName();
    }

    private static class QuestContentViewHolder extends ViewHolder implements
            ILockScreenContentContainerViewHolder, IQuestViewHolder {

        final QuestContext questContext;
        final ImageView menuButton;
        final TextView titleView, titleViewRight, pupilLevel;
        final ViewGroup titleContainer, contentContainer, statisticsContainer, delayedStartContainer;
        final ViewSwitcher questContentContainer;
        final CircleProgressView pupilProgress;
        final Animation outAnimation, inAnimation;

        QuestContentViewHolder(@NonNull final QuestContext questContext) {
            super(questContext, R.layout.layout_lock_screen_quest_content);
            this.questContext = questContext;
            titleContainer = (ViewGroup) getView().findViewById(R.id.container_title);
            contentContainer = (ViewGroup) getView().findViewById(R.id.container_content);
            questContentContainer = (ViewSwitcher) getView().findViewById(R.id.container_content_quest);
            titleView = (TextView) mView.findViewById(R.id.tv_title);
            titleViewRight = (TextView) mView.findViewById(R.id.tv_title_right);
            menuButton = (ImageView) mView.findViewById(R.id.btn_menu);
            pupilLevel = (TextView) getView().findViewById(R.id.tv_pupil_level);
            pupilProgress = (CircleProgressView) getView().findViewById(R.id.pupil_progress);
            statisticsContainer = (ViewGroup) getView().findViewById(R.id.container_statistics);
            delayedStartContainer = (ViewGroup) getView().findViewById(R.id.container_delayed_start);
            outAnimation = AnimationUtils.loadAnimation(questContext, R.anim.slide_out);
            inAnimation = AnimationUtils.loadAnimation(questContext, R.anim.slide_in);
            questContentContainer.setOutAnimation(outAnimation);
            questContentContainer.setInAnimation(inAnimation);
            ((ViewGroup) getView()).removeAllViews();
        }

        @NonNull
        @Override
        public View getTitleContentContainer() {
            return titleContainer;
        }

        @NonNull
        @Override
        public View getContentContainer() {
            return (View) questContentContainer.getParent();
        }

        @Override
        public void attachQuestView(@NonNull View questView) {
            questView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            boolean contentContainerHasNoChild = questContentContainer.getChildCount() == 0;
            questContentContainer.addView(questView);
            if (!contentContainerHasNoChild) {
                View previousQuestView = questContentContainer.getChildAt(0);
                previousQuestView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                questContentContainer.showNext();
                questContentContainer.removeViewAt(0);
            }
        }

        @Override
        public void detachQuestView() {
            questContentContainer.removeAllViews();
        }

        void updateViewVisibility(@NonNull QuestContext questContext) {
            boolean showDelayStartView = questContext.questHasState(DELAYED_START) && !questContext.questHasState(STARTED);
            delayedStartContainer.setVisibility(showDelayStartView ? VISIBLE : INVISIBLE);
            if (showDelayStartView) {
                delayedStartContainer.setAlpha(0);
                delayedStartContainer.animate().alpha(1).withLayer().setDuration(mContext.getResources().getInteger(R.integer.short_animation_duration));
            }
        }
    }
}