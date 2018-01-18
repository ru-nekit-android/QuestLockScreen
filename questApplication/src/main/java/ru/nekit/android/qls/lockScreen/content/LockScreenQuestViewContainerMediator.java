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

import java.text.SimpleDateFormat;
import java.util.Locale;

import at.grabner.circleprogress.CircleProgressView;
import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.TransitionChoreograph.Transition;
import ru.nekit.android.qls.lockScreen.window.Window;
import ru.nekit.android.qls.quest.IQuestViewHolder;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestVisualBuilder;
import ru.nekit.android.qls.quest.history.QuestHistoryItem;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgram;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgramLevel;
import ru.nekit.android.qls.quest.window.AnswerWindow;
import ru.nekit.android.qls.quest.window.MenuWindowMediator;
import ru.nekit.android.qls.quest.window.PupilStatisticsWindowMediator;
import ru.nekit.android.qls.utils.KeyboardHost;
import ru.nekit.android.qls.utils.ViewHolder;

import static ru.nekit.android.qls.lockScreen.TransitionChoreograph.EVENT_TRANSITION_CHANGED;
import static ru.nekit.android.qls.lockScreen.TransitionChoreograph.Transition.QUEST;
import static ru.nekit.android.qls.lockScreen.window.Window.EVENT_WINDOW_CLOSED;
import static ru.nekit.android.qls.lockScreen.window.Window.EVENT_WINDOW_OPEN;
import static ru.nekit.android.qls.lockScreen.window.Window.closeAllWindows;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.DELAYED_PLAY;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.PLAYED;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_RIGHT_ANSWER;

public class LockScreenQuestViewContainerMediator extends AbstractLockScreenContentMediator
        implements View.OnClickListener, EventBus.IEventHandler {

    public static final String ACTION_SHOW_WRONG_ANSWER_WINDOW = "action_show_wrong_answer_window";
    public static final String ACTION_SHOW_RIGHT_ANSWER_WINDOW = "action_show_right_answer_window";
    @NonNull
    private final QuestContext mQuestContext;
    private QuestVisualBuilder mQuestVisualBuilder, mPreviousQuestVisualBuilder;
    private LockScreenQuestViewContainerHolder mViewHolder;

    public LockScreenQuestViewContainerMediator(@NonNull QuestContext questContext) {
        mQuestContext = questContext;

        mViewHolder = new LockScreenQuestViewContainerHolder(questContext);
        mViewHolder.menuButton.setOnClickListener(this);
        mViewHolder.statisticsContainer.setOnClickListener(this);
        mViewHolder.delayedPlayContainer.setOnClickListener(this);

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
                EVENT_WINDOW_CLOSED,
                ACTION_SHOW_RIGHT_ANSWER_WINDOW,
                ACTION_SHOW_WRONG_ANSWER_WINDOW
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
        String questName = mQuestContext.getQuest().getQuestType().getName(mQuestContext);
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
        createAndAttachQuestView();
        updateQuestTitle();
        mViewHolder.updateViewVisibility(mQuestContext);
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
        } else if (view == mViewHolder.delayedPlayContainer) {
            if (mQuestContext.playQuest()) {
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
                if (windowName != null && windowName.equals(AnswerWindow.Type.RIGHT.getName())) {
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

            case ACTION_SHOW_RIGHT_ANSWER_WINDOW:

                KeyboardHost.hideKeyboard(mQuestContext, mViewHolder.rootContainer, new Runnable() {
                    @Override
                    public void run() {

                        //TODO: make right answer content holder with logic
                        ViewHolder contentViewHolder = new ViewHolder(mQuestContext, R.layout.wc_right_answer_content);
                        QuestHistoryItem.Pair questHistoryPair = mQuestContext.getQuestHistoryPair();
                        SimpleDateFormat dateFormat = new SimpleDateFormat(mQuestContext.getString(R.string.right_answer_timer_formatter),
                                Locale.getDefault());
                        StringBuilder textBuilder = new StringBuilder(mQuestContext.getString(R.string.congratulation_for_right_answer));
                        textBuilder.append("\n").append(String.format(mQuestContext.getString(R.string.right_answer_time_formatter),
                                dateFormat.format(questHistoryPair.questHistory.time)));
                        if ((questHistoryPair.questHistory.recordType & QuestHistoryItem.RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD) != 0) {
                            textBuilder.append("\n");
                            textBuilder.append(String.format(mQuestContext.getString(R.string.right_answer_best_time_update_formatter),
                                    dateFormat.format(questHistoryPair.questHistory.time)));
                        }
                        if ((questHistoryPair.globalQuestHistory.recordType & QuestHistoryItem.RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD) != 0) {
                            textBuilder.append("\n");
                            textBuilder.append(String.format(mQuestContext.getString(R.string.right_answer_series_length_update_formatter),
                                    questHistoryPair.globalQuestHistory.rightAnswerSeries));
                        }
                        ((TextView) contentViewHolder.view.findViewById(R.id.tv_title)).setText(textBuilder.toString());
                        new AnswerWindow.Builder(mQuestContext, AnswerWindow.Type.RIGHT).
                                setContent(contentViewHolder).
                                setToolContent(R.layout.wc_right_answer_tool_simple_content).
                                create().open();
                    }
                });

                break;

            case ACTION_SHOW_WRONG_ANSWER_WINDOW:

                KeyboardHost.hideKeyboard(mQuestContext, mViewHolder.rootContainer, new Runnable() {
                    @Override
                    public void run() {

                        ViewHolder contentViewHolder = new ViewHolder(mQuestContext,
                                R.layout.wc_wrong_answer_content);
                        ((TextView) contentViewHolder.view.findViewById(R.id.tv_title)).setText(mQuestContext.getString(R.string.title_wrong_answer));
                        new AnswerWindow.Builder(mQuestContext, AnswerWindow.Type.WRONG).
                                setContent(contentViewHolder).
                                setToolContent(R.layout.wc_wrong_answer_tool_simple_content).
                                create().open();
                    }
                });

                break;

        }
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return getClass().getName();
    }

    private static class LockScreenQuestViewContainerHolder extends ViewHolder implements
            ILockScreenContentContainerViewHolder, IQuestViewHolder {

        final QuestContext questContext;
        final ImageView menuButton;
        final TextView titleView, titleViewRight, pupilLevel;
        final ViewGroup rootContainer, titleContainer, contentContainer, statisticsContainer, delayedPlayContainer;
        final ViewSwitcher questContentContainer;
        final CircleProgressView pupilProgress;
        final Animation outAnimation, inAnimation;

        LockScreenQuestViewContainerHolder(@NonNull final QuestContext questContext) {
            super(questContext, R.layout.layout_lock_screen_quest_view_container);
            this.questContext = questContext;
            rootContainer = (ViewGroup) view.findViewById(R.id.container_root);
            titleContainer = (ViewGroup) view.findViewById(R.id.container_title);
            contentContainer = (ViewGroup) view.findViewById(R.id.container_content);
            questContentContainer = (ViewSwitcher) view.findViewById(R.id.container_content_quest);
            titleView = (TextView) view.findViewById(R.id.tv_title);
            titleViewRight = (TextView) view.findViewById(R.id.tv_message_right);
            menuButton = (ImageView) view.findViewById(R.id.btn_menu);
            pupilLevel = (TextView) view.findViewById(R.id.tv_pupil_level);
            pupilProgress = (CircleProgressView) view.findViewById(R.id.pupil_progress);
            statisticsContainer = (ViewGroup) view.findViewById(R.id.container_statistics);
            delayedPlayContainer = (ViewGroup) view.findViewById(R.id.container_delayed_play);
            outAnimation = AnimationUtils.loadAnimation(questContext, R.anim.slide_out);
            inAnimation = AnimationUtils.loadAnimation(questContext, R.anim.slide_in);
            questContentContainer.setOutAnimation(outAnimation);
            questContentContainer.setInAnimation(inAnimation);
            ((ViewGroup) view).removeAllViews();
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
            boolean showDelayedPlayContainer = questContext.questHasState(DELAYED_PLAY) && !questContext.questHasState(PLAYED);
            delayedPlayContainer.setVisibility(showDelayedPlayContainer ? View.VISIBLE : View.INVISIBLE);
            if (showDelayedPlayContainer) {
                delayedPlayContainer.setAlpha(0);
                delayedPlayContainer.animate().withLayer().alpha(1).setDuration(mContext.getResources().getInteger(R.integer.short_animation_duration));
            }
        }
    }
}