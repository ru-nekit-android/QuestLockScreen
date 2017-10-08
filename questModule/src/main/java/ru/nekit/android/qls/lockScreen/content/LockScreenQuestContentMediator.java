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

import org.jetbrains.annotations.Nullable;

import at.grabner.circleprogress.CircleProgressView;
import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.R;
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
import ru.nekit.android.qls.utils.KeyboardHost;
import ru.nekit.android.qls.utils.ViewHolder;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static ru.nekit.android.qls.lockScreen.TransitionChoreograph.EVENT_TRANSITION_CHANGED;
import static ru.nekit.android.qls.lockScreen.TransitionChoreograph.Transition.QUEST;
import static ru.nekit.android.qls.lockScreen.window.Window.EVENT_WINDOW_CLOSED;
import static ru.nekit.android.qls.lockScreen.window.Window.EVENT_WINDOW_OPEN;
import static ru.nekit.android.qls.lockScreen.window.Window.closeAllWindows;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.DELAYED_START;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.STARTED;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_RIGHT_ANSWER;

public class LockScreenQuestContentMediator extends AbstractLockScreenContentMediator
        implements View.OnClickListener, EventBus.IEventHandler {

    @NonNull
    private final QuestContext mQuestContext;
    private QuestVisualBuilder mQuestVisualBuilder, mPreviousQuestVisualBuilder;
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
                    mViewHolder.updateViewVisibility(mQuestContext);
                    //mQuestVisualBuilder.getView().setLayerType(LAYER_TYPE_NONE, null);
                    mQuestContext.showAndStartQuestIfAble();
                } else if (animation == mViewHolder.outAnimation) {
                    mPreviousQuestVisualBuilder.detachView();
                    //mPreviousQuestVisualBuilder.getView().setLayerType(LAYER_TYPE_NONE, null);
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
                EVENT_RIGHT_ANSWER,
                EVENT_WINDOW_OPEN,
                EVENT_WINDOW_CLOSED
        );

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
        mQuestVisualBuilder.deactivate();
    }

    @Override
    public void detachView() {
        closeAllWindows();
        if (mPreviousQuestVisualBuilder != null) {
            mPreviousQuestVisualBuilder.detachView();
        }
        mQuestVisualBuilder.detachView();
        mViewHolder.detachQuestView();
    }

    @Override
    public void attachView() {
        updateQuestTitle();
        mViewHolder.updateViewVisibility(mQuestContext);
        createAndAttachQuestView();
    }

    private void createAndAttachQuestView() {
        mQuestVisualBuilder = new QuestVisualBuilder(mQuestContext);
        mQuestVisualBuilder.create();
        View questView = mQuestVisualBuilder.getView();
        questView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                mQuestContext.attachQuest();
                Transition transition = mTransitionChoreograph.getPreviousTransition();
                if (transition != QUEST) {
                    mQuestContext.showAndStartQuestIfAble();
                }
                view.removeOnAttachStateChangeListener(this);
            }

            @Override
            public void onViewDetachedFromWindow(View view) {

            }
        });
        mViewHolder.attachQuestView(questView);
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
            if (mQuestContext.startQuest()) {
                mViewHolder.updateViewVisibility(mQuestContext);
            }
        }
    }

    @Override
    public void onEvent(@NonNull final Intent intent) {
        String action = intent.getAction();
        switch (action) {

            case EVENT_RIGHT_ANSWER:

                updatePupilStatisticsView();

                break;

            case EVENT_WINDOW_OPEN:

                mQuestContext.pauseQuest();

                break;

            case EVENT_WINDOW_CLOSED:

                @Nullable
                String windowName = intent.getStringExtra(Window.VALUE_WINDOW_NAME);
                if (windowName != null && windowName.equals(RightAnswerWindow.NAME)) {
                    mTransitionChoreograph.goCurrentTransition();
                } else {
                    if (mTransitionChoreograph.getCurrentTransition() == QUEST) {
                        mQuestContext.resumeQuest();
                    }
                }

                break;

            case EVENT_TRANSITION_CHANGED:

                Transition transition = mTransitionChoreograph.getCurrentTransition();
                if (transition == QUEST) {
                    //remember current as previous
                    mPreviousQuestVisualBuilder = mQuestVisualBuilder;
                    mPreviousQuestVisualBuilder.deactivate();
                    createAndAttachQuestView();
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
            boolean contentContainerHasNoChild = questContentContainer.getChildCount() == 0;
            questContentContainer.addView(questView);
            //questView.setLayerType(LAYER_TYPE_HARDWARE, null);
            if (!contentContainerHasNoChild) {
                View previousQuestView = questContentContainer.getChildAt(0);
                //previousQuestView.setLayerType(LAYER_TYPE_HARDWARE, null);
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
                delayedStartContainer.animate().withLayer().alpha(1).setDuration(mContext.getResources().getInteger(R.integer.short_animation_duration));
            }
        }
    }
}