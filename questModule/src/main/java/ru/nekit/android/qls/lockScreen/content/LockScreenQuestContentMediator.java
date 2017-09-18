package ru.nekit.android.qls.lockScreen.content;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.text.SimpleDateFormat;
import java.util.Locale;

import at.grabner.circleprogress.CircleProgressView;
import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.LockScreenMediator;
import ru.nekit.android.qls.lockScreen.TransitionChoreograph;
import ru.nekit.android.qls.lockScreen.TransitionChoreograph.Transition;
import ru.nekit.android.qls.lockScreen.window.Window;
import ru.nekit.android.qls.quest.IQuestViewHolder;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestContextEvent;
import ru.nekit.android.qls.quest.QuestVisualBuilder;
import ru.nekit.android.qls.quest.history.QuestHistoryItem;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgram;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgramLevel;
import ru.nekit.android.qls.quest.window.MenuWindowMediator;
import ru.nekit.android.qls.quest.window.PupilStatisticsWindowMediator;
import ru.nekit.android.qls.quest.window.RightAnswerWindowContentViewHolder;
import ru.nekit.android.qls.quest.window.WrongAnswerWindowContentViewHolder;
import ru.nekit.android.qls.utils.KeyboardHost;
import ru.nekit.android.qls.utils.ViewHolder;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static ru.nekit.android.qls.lockScreen.TransitionChoreograph.Transition.QUEST;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.DELAYED_START;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.STARTED;
import static ru.nekit.android.qls.quest.history.QuestHistoryItem.RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD;
import static ru.nekit.android.qls.quest.history.QuestHistoryItem.RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD;

public class LockScreenQuestContentMediator extends AbstractLockScreenContentMediator
        implements View.OnClickListener, EventBus.IEventHandler {

    @NonNull
    private final QuestContext mQuestContext;
    private QuestVisualBuilder mCurrentQuestVisualBuilder, mPreviousQuestVisualBuilder;
    private Window mRightAnswerWindow;
    private final Window.WindowListener mWindowListener = new Window.WindowListener() {

        @Override
        public void onWindowOpen(@NonNull Window window) {

        }

        @Override
        public void onWindowOpened(@NonNull Window window) {
        }

        @Override
        public void onWindowClose(@NonNull Window window, boolean internal) {

        }

        @Override
        public void onWindowClosed(@NonNull Window window, boolean internal) {
            if (internal) {
                if (window == mRightAnswerWindow) {
                    mTransitionChoreograph.goCurrentTransition();
                }
            } else {
                if (allowToDestroy()) {
                    mQuestContext.getEventBus().sendEvent(LockScreenMediator.ACTION_CLOSE);
                }
            }
        }
    };
    private QuestContentViewHolder mViewHolder;
    private Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (animation == mViewHolder.inAnimation) {
                updateQuestTitle();
                mViewHolder.updateViewVisibility(mQuestContext.getQuestState());
            } else if (animation == mViewHolder.outAnimation) {
                mPreviousQuestVisualBuilder.getQuestMediatorFacade().detachView();
                mPreviousQuestVisualBuilder = null;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    public LockScreenQuestContentMediator(@NonNull final QuestContext questContext) {
        mQuestContext = questContext;
        mViewHolder = new QuestContentViewHolder(questContext);
        mViewHolder.menuButton.setOnClickListener(this);
        mViewHolder.statisticsContainer.setOnClickListener(this);
        mViewHolder.delayedStartContainer.setOnClickListener(this);
        mViewHolder.inAnimation.setAnimationListener(mAnimationListener);
        mViewHolder.outAnimation.setAnimationListener(mAnimationListener);
        questContext.getEventBus().handleEvents(this,
                TransitionChoreograph.EVENT_TRANSITION_CHANGED,
                QuestContextEvent.EVENT_RIGHT_ANSWER,
                QuestContextEvent.EVENT_WRONG_ANSWER
        );
        mCurrentQuestVisualBuilder = new QuestVisualBuilder(questContext);
        mCurrentQuestVisualBuilder.create(mViewHolder.questContentContainer);
        updatePupilStatisticsView();
    }

    private void updateQuestTitle() {
        int questSeriesLength = mQuestContext.getQuestSeriesLength();
        String questSeriesString = questSeriesLength > 1 ? String.format("%s из %s",
                questSeriesLength - mTransitionChoreograph.getQuestSeriesCounterValue() + 1,
                questSeriesLength) : "";
        Spannable questSeriesSpan = new SpannableString(questSeriesString);
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(
                mQuestContext.getResources().getDimensionPixelSize(R.dimen.quest_series_size), false);
        questSeriesSpan.setSpan(sizeSpan, 0, questSeriesString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        String questName = mQuestContext.getQuest().getQuestType().getTitle(mQuestContext);
        Spannable questNameSpan = new SpannableString(String.format("%s", questName));
        SpannableStringBuilder spannableResult = new SpannableStringBuilder();
        spannableResult.append(questNameSpan);
        spannableResult.append(" ");
        spannableResult.append(questSeriesSpan);
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
        mViewHolder.pupilLevel.setText(String.format("%s", currentQTPLevel.toString()));
    }

    @NonNull
    @Override
    public ILockScreenContentContainerViewHolder getViewHolder() {
        return mViewHolder;
    }

    private boolean allowToDestroy() {
        return Window.getWindowStack().isEmpty();
    }

    @Override
    public void destroy() {
        Window.closeAllWindows();
        mQuestContext.getEventBus().stopHandleEvents(this);
        mViewHolder.menuButton.setOnClickListener(null);
        mViewHolder.statisticsContainer.setOnClickListener(null);
        mViewHolder.inAnimation.setAnimationListener(null);
        if (mPreviousQuestVisualBuilder != null) {
            mPreviousQuestVisualBuilder.getQuestMediatorFacade().deactivate();
            mPreviousQuestVisualBuilder.getQuestMediatorFacade().detachView();
        }
        mCurrentQuestVisualBuilder.getQuestMediatorFacade().deactivate();
        mCurrentQuestVisualBuilder.getQuestMediatorFacade().detachView();
    }

    @Override
    public void detachView() {
        mViewHolder.detachQuestView();
    }

    @Override
    public void attachView() {
        updateQuestTitle();
        attachQuestView();
        mViewHolder.updateViewVisibility(mQuestContext.getQuestState());
    }

    private void attachQuestView() {
        mViewHolder.attachQuestContent(mCurrentQuestVisualBuilder.getQuestMediatorFacade().getView());
        mQuestContext.createAndStartQuestIfAble();
    }

    @Override
    public void onClick(View view) {
        if (view == mViewHolder.statisticsContainer) {
            KeyboardHost.hideKeyboard(mQuestContext, view, new Runnable() {
                @Override
                public void run() {
                    PupilStatisticsWindowMediator.openWindow(mQuestContext, mWindowListener);
                }
            });
        } else if (view == mViewHolder.menuButton) {
            KeyboardHost.hideKeyboard(mQuestContext, view, new Runnable() {
                @Override
                public void run() {
                    MenuWindowMediator.openWindow(mQuestContext, mWindowListener);
                }
            });
        } else if (view == mViewHolder.delayedStartContainer) {
            if (mQuestContext.startQuestIfAble()) {
                mViewHolder.updateViewVisibility(mQuestContext.getQuestState());
            }
        }
    }

    @Override
    public void onEvent(@NonNull Intent intent) {
        String action = intent.getAction();
        switch (action) {

            case QuestContextEvent.EVENT_RIGHT_ANSWER:

                final QuestHistoryItem.Pair questHistoryPair =
                        intent.getParcelableExtra(QuestHistoryItem.Pair.NAME);
                KeyboardHost.hideKeyboard(mQuestContext, mViewHolder.getView(), new Runnable() {
                    @Override
                    public void run() {
                        updatePupilStatisticsView();

                        RightAnswerWindowContentViewHolder content =
                                new RightAnswerWindowContentViewHolder(mQuestContext);
                        mRightAnswerWindow = Window.open(mQuestContext,
                                content,
                                R.style.Window_RightAnswer,
                                mWindowListener
                        );
                        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss.SSS",
                                Locale.getDefault());
                        String text = mQuestContext.getString(R.string.congratulation_for_right_answer);
                        text += "\n" + String.format(mQuestContext.getString(R.string.right_answer_time_formatter),
                                dateFormat.format(questHistoryPair.questHistory.time));
                        if ((questHistoryPair.questHistory.recordType & RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD) != 0) {
                            text += "\n";
                            text += String.format(mQuestContext.getString(R.string.right_answer_best_time_update_formatter),
                                    dateFormat.format(questHistoryPair.questHistory.time));
                        }
                        if ((questHistoryPair.globalQuestHistory.recordType & RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD) != 0) {
                            text += "\n";
                            text += String.format(mQuestContext.getString(R.string.right_answer_series_length_update_formatter),
                                    questHistoryPair.globalQuestHistory.rightAnswerSeries);
                        }
                        content.titleView.setText(text);
                    }
                });

                break;

            case QuestContextEvent.EVENT_WRONG_ANSWER:

                Window.open(mQuestContext,
                        new WrongAnswerWindowContentViewHolder(mQuestContext),
                        R.style.Window_WrongAnswer,
                        mWindowListener
                );

                break;

            case TransitionChoreograph.EVENT_TRANSITION_CHANGED:

                Transition transition = mTransitionChoreograph.getCurrentTransition();
                if (transition == QUEST) {
                    mPreviousQuestVisualBuilder = mCurrentQuestVisualBuilder;
                    mCurrentQuestVisualBuilder.getQuestMediatorFacade().deactivate();
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
        public void attachQuestContent(@NonNull View questView) {
            boolean contentContainerHasNoChild = questContentContainer.getChildCount() == 0;
            questContentContainer.addView(questView);
            if (!contentContainerHasNoChild) {
                questContentContainer.showNext();
                questContentContainer.removeViewAt(0);
            }
        }

        @Override
        public void detachQuestView() {
            questContentContainer.removeAllViews();
        }

        void updateViewVisibility(int flags) {
            boolean showDelayStartView = (flags & DELAYED_START) != 0 && (flags & STARTED) == 0;
            delayedStartContainer.setVisibility(showDelayStartView ? VISIBLE : INVISIBLE);
            if (showDelayStartView) {
                delayedStartContainer.setAlpha(0);
                delayedStartContainer.animate().alpha(1).setDuration(mContext.getResources().getInteger(R.integer.short_animation_duration));
            }
        }
    }
}