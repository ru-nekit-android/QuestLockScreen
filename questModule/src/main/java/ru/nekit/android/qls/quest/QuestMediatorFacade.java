package ru.nekit.android.qls.quest;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.answer.shared.IAnswerChecker;
import ru.nekit.android.qls.quest.base.Quest;
import ru.nekit.android.qls.quest.mediator.IQuestMediator;
import ru.nekit.android.qls.quest.mediator.answer.IQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.answer.QuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.content.EmptyQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.content.IQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.title.IQuestTitleMediator;
import ru.nekit.android.qls.utils.AnimationUtils;
import ru.nekit.android.qls.utils.KeyboardHost;
import ru.nekit.android.qls.utils.ViewHolder;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.RelativeLayout.ABOVE;
import static android.widget.RelativeLayout.BELOW;
import static ru.nekit.android.qls.lockScreen.content.LockScreenQuestViewContainerMediator.ACTION_SHOW_RIGHT_ANSWER_WINDOW;
import static ru.nekit.android.qls.lockScreen.content.LockScreenQuestViewContainerMediator.ACTION_SHOW_WRONG_ANSWER_WINDOW;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.DELAYED_PLAY;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.PLAYED;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_ATTACH;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_PAUSE;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_PLAY;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_REPLAY;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_RESUME;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_START;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_STOP;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_RIGHT_ANSWER;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_WRONG_ANSWER;

public class QuestMediatorFacade implements View.OnClickListener, IQuestMediatorFacade,
        EventBus.IEventHandler,
        View.OnLayoutChangeListener {

    private QuestContext mQuestContext;
    private Quest mQuest;
    private QuestViewHolder mViewHolder;
    @NonNull
    private IQuestTitleMediator mTitleMediator;
    @NonNull
    private IQuestContentMediator mContentMediator;
    @NonNull
    private IQuestAlternativeAnswerMediator mAlternativeAnswerMediator;
    @NonNull
    private IAnswerChecker mAnswerChecker;

    QuestMediatorFacade(@NonNull QuestContext questContext,
                        @NonNull IAnswerChecker answerChecker,
                        @NonNull IQuestTitleMediator questTitleMediator,
                        @Nullable IQuestContentMediator questContentMediator,
                        @Nullable IQuestAlternativeAnswerMediator questAlternativeAnswerMediator) {
        mQuestContext = questContext;
        mAnswerChecker = answerChecker;
        mTitleMediator = questTitleMediator;
        mContentMediator = questContentMediator == null ?
                new EmptyQuestContentMediator() : questContentMediator;
        mAlternativeAnswerMediator = questAlternativeAnswerMediator == null ?
                new QuestAlternativeAnswerMediator() : questAlternativeAnswerMediator;
        questContext.getEventBus().handleEvents(this,
                EVENT_QUEST_ATTACH,
                EVENT_QUEST_START,
                EVENT_QUEST_PLAY,
                EVENT_QUEST_REPLAY,
                EVENT_QUEST_PAUSE,
                EVENT_QUEST_RESUME,
                EVENT_QUEST_STOP,
                EVENT_WRONG_ANSWER,
                EVENT_RIGHT_ANSWER
        );
    }

    @Override
    public void updateSize() {
        /*boolean contentViewIsPresent = false, alternativeAnswerIsPresent = false;
        if (mQuestContext.questHasState(PLAYED) && !mQuestContext.questHasState(PAUSED)) {
            contentViewIsPresent = mContentMediator.getView() != null;
            alternativeAnswerIsPresent =
                    mViewHolder.alternativeAnswerContainer.getVisibility() == VISIBLE;
            if (contentViewIsPresent) {
                mContentMediator.updateSize();
            }
            if (alternativeAnswerIsPresent) {
                mAlternativeAnswerMediator.updateSize();
            }
        }
        if (contentViewIsPresent) {
            mViewHolder.contentContainer.requestLayout();
        }
        if (alternativeAnswerIsPresent) {
            mViewHolder.alternativeAnswerContainer.requestLayout();
        }*/
    }

    private boolean questDelayedPlay() {
        return mQuestContext.questHasState(DELAYED_PLAY);
    }

    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mQuest = questContext.getQuest();
        mViewHolder = new QuestViewHolder(questContext);
        mTitleMediator.onCreate(questContext);
        mContentMediator.onCreate(questContext);
        mAlternativeAnswerMediator.onCreate(questContext);
        mViewHolder.titleContainer.addView(mTitleMediator.getView());
        View contentView = mContentMediator.getView();
        RelativeLayout.LayoutParams answerContainerLayoutParams =
                (RelativeLayout.LayoutParams) mViewHolder.answerContainer.getLayoutParams();
        ViewGroup.LayoutParams alternativeAnswerButtonContainerLayoutParams =
                mViewHolder.alternativeAnswerContainer.getLayoutParams();
        if (mContentMediator.getView() == null) {
            answerContainerLayoutParams.addRule(BELOW, R.id.container_title);
            alternativeAnswerButtonContainerLayoutParams.height = MATCH_PARENT;
        } else {
            answerContainerLayoutParams.addRule(BELOW, 0);
            alternativeAnswerButtonContainerLayoutParams.height = WRAP_CONTENT;
        }
        RelativeLayout.LayoutParams contentContainerLayoutParams =
                (RelativeLayout.LayoutParams) mViewHolder.contentContainer.getLayoutParams();
        if (mContentMediator.includeInLayout()) {
            contentContainerLayoutParams.addRule(ABOVE, R.id.container_answer);
            contentContainerLayoutParams.addRule(BELOW, R.id.container_title);
        } else {
            contentContainerLayoutParams.setMargins(0, 0, 0, 0);
            contentContainerLayoutParams.addRule(ABOVE, 0);
            contentContainerLayoutParams.addRule(BELOW, 0);
        }
        if (contentView != null) {
            mViewHolder.contentContainer.addView(mContentMediator.getView());
        }
        mAlternativeAnswerMediator.setAnswerCallback(questContext);
        mAlternativeAnswerMediator.setAnswerChecker(mAnswerChecker);
        View alternativeAnswerContent = mAlternativeAnswerMediator.getView();
        if (alternativeAnswerContent == null) {
            List<View> buttonList = mAlternativeAnswerMediator.getAnswerButtonList();
            if (buttonList != null) {
                for (View button : buttonList) {
                    mViewHolder.alternativeAnswerContainer.addView(button);
                }
            }
        } else {
            mViewHolder.alternativeAnswerContainer.addView(alternativeAnswerContent);
        }
        boolean answerInputVisible = !alternativeAnswerIsPresent();
        mViewHolder.answerInput.setVisibility(answerInputVisible ? VISIBLE : GONE);
        boolean showAnswerButton = !alternativeAnswerButtonIsPresent() && questContext.answerButtonVisible();
        mViewHolder.answerButton.setVisibility(showAnswerButton ? VISIBLE : GONE);
        if (!answerInputVisible && showAnswerButton) {
            LinearLayout.LayoutParams answerButtonLayoutParameters =
                    (LinearLayout.LayoutParams) mViewHolder.answerButton.getLayoutParams();
            answerButtonLayoutParameters.weight = 1;
            mViewHolder.answerButton.setLayoutParams(answerButtonLayoutParameters);
        }
        mViewHolder.alternativeAnswerContainer.setVisibility(alternativeAnswerButtonIsPresent() ? VISIBLE : GONE);
        if (mViewHolder.answerButton.getVisibility() == VISIBLE) {
            mViewHolder.answerButton.setOnClickListener(this);
        }
        EditText answerInput = null;
        if (alternativeAnswerInputIsPresent()) {
            EditText contentAnswerInput = mContentMediator.getAnswerInput();
            if (contentAnswerInput != null && contentAnswerInput.getVisibility() == VISIBLE) {
                contentAnswerInput.setInputType(mQuest.getAnswerInputType());
                answerInput = contentAnswerInput;
            }
        } else {
            answerInput = mViewHolder.answerInput;
        }
        if (answerInput != null) {
            answerInput.setInputType(mQuest.getAnswerInputType());
            answerInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
            answerInput.setOnEditorActionListener(
                    new EditText.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                answerFunction();
                                return true;
                            }
                            return false;
                        }
                    });
        }
        updateVisibilityOfViews(!questDelayedPlay());
    }

    @Override
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        mTitleMediator.onQuestAttach(mViewHolder.titleContainer);
        mContentMediator.onQuestAttach(mViewHolder.contentContainer);
        mAlternativeAnswerMediator.onQuestAttach(mViewHolder.alternativeAnswerContainer);
        mViewHolder.getView().addOnLayoutChangeListener(this);
    }

    @Override
    public void onQuestStart(boolean delayedPlay) {
        mViewHolder.titleContainer.requestLayout();
        mViewHolder.contentContainer.requestLayout();
        mViewHolder.alternativeAnswerContainer.requestLayout();
        mTitleMediator.onQuestStart(delayedPlay);
        mContentMediator.onQuestStart(delayedPlay);
        mAlternativeAnswerMediator.onQuestStart(delayedPlay);
        requestFocus();
    }

    @Override
    public void onQuestPlay(boolean delayedPlay) {
        updateVisibilityOfViews(true);
        mTitleMediator.onQuestPlay(delayedPlay);
        mContentMediator.onQuestPlay(delayedPlay);
        if (delayedPlay) {
            if (alternativeAnswerButtonIsPresent()) {
                mAlternativeAnswerMediator.onQuestPlay(true);
            } else {
                int duration = mQuestContext.getQuestDelayedPlayAnimationDuration();
                View view = mViewHolder.answerContainer;
                if (view != null) {
                    view.setAlpha(0);
                    view.animate().withLayer().alpha(1).setDuration(duration);
                }
            }
        } else {
            mAlternativeAnswerMediator.onQuestPlay(false);
        }
        requestFocus();
    }

    @Override
    public boolean onRightAnswer() {
        boolean showRightAnswerWindow = true;
        showRightAnswerWindow = showRightAnswerWindow && mTitleMediator.onRightAnswer();
        showRightAnswerWindow = showRightAnswerWindow && mContentMediator.onRightAnswer();
        showRightAnswerWindow = showRightAnswerWindow && mAlternativeAnswerMediator.onRightAnswer();
        return showRightAnswerWindow;
    }

    @Override
    public boolean onWrongAnswer() {
        boolean showWrongAnswerWindow = true;
        showWrongAnswerWindow = showWrongAnswerWindow && mTitleMediator.onWrongAnswer();
        showWrongAnswerWindow = showWrongAnswerWindow && mContentMediator.onWrongAnswer();
        showWrongAnswerWindow = showWrongAnswerWindow && mAlternativeAnswerMediator.onWrongAnswer();
        return showWrongAnswerWindow;
    }

    @Override
    public void onQuestReplay() {
        mTitleMediator.onQuestReplay();
        mContentMediator.onQuestReplay();
        mAlternativeAnswerMediator.onQuestReplay();
        requestFocus();
    }

    @Override
    public void onQuestPause() {
        mTitleMediator.onQuestPause();
        mContentMediator.onQuestPause();
        mAlternativeAnswerMediator.onQuestPause();
    }

    @Override
    public void onQuestResume() {
        mTitleMediator.onQuestResume();
        mContentMediator.onQuestResume();
        mAlternativeAnswerMediator.onQuestResume();
        requestFocus();
    }

    @Override
    public void onQuestStop() {
        mTitleMediator.onQuestStop();
        mContentMediator.onQuestStop();
        mAlternativeAnswerMediator.onQuestStop();
    }

    private boolean alternativeAnswerInputIsPresent() {
        EditText alternativeAnswerInput = mContentMediator.getAnswerInput();
        return alternativeAnswerInput != null && alternativeAnswerInput.getVisibility() == VISIBLE;
    }

    private boolean alternativeAnswerButtonIsPresent() {
        List<View> buttonList = mAlternativeAnswerMediator.getAnswerButtonList();
        View alternativeAnswerView = mAlternativeAnswerMediator.getView();
        return (buttonList != null && buttonList.size() > 0) ||
                (alternativeAnswerView != null && alternativeAnswerView.getVisibility() == VISIBLE);
    }

    private boolean alternativeAnswerIsPresent() {
        return alternativeAnswerButtonIsPresent() || alternativeAnswerInputIsPresent();
    }

    @Override
    public void onClick(View view) {
        answerFunction();
    }

    private void answerFunction() {
        if (!alternativeAnswerButtonIsPresent()) {
            CharSequence answerCharSequence = (alternativeAnswerInputIsPresent() ?
                    mContentMediator.getAnswerInput() : mViewHolder.answerInput).getText();
            if (answerCharSequence != null && answerCharSequence.length() > 0) {
                String answerString = answerCharSequence.toString();
                if (mAnswerChecker.checkStringInputFormat(mQuest, answerString)) {
                    if (mAnswerChecker.checkStringInput(mQuest, answerString)) {
                        mQuestContext.rightAnswer();
                    } else {
                        mQuestContext.wrongAnswer();
                    }
                } else {
                    mQuestContext.wrongStringInputFormat();
                    setErrorColorAndReturnToNormal();
                }
            } else {
                mQuestContext.emptyAnswer();
                setErrorColorAndReturnToNormal();
            }
        }
    }

    private void setErrorColorAndReturnToNormal() {
        AnimationUtils.getColorAnimator(mQuestContext, R.color.green,
                R.color.red,
                mQuestContext.getResources().getInteger(R.integer.short_animation_duration),
                mViewHolder.answerInputContainer, new BounceInterpolator()
        ).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationUtils.getColorAnimator(mQuestContext, R.color.red,
                        R.color.green,
                        mQuestContext.getResources().getInteger(R.integer.short_animation_duration),
                        mViewHolder.answerInputContainer
                ).start();
            }
        }, 1000);
    }

    private ValueAnimator getColorAnimator(boolean reverse) {
        @ColorRes
        int startColor = reverse ? R.color.red
                : R.color.green;
        int endColor = reverse ? R.color.green
                : R.color.red;
        final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                startColor, endColor);
        colorAnimation.setDuration(mQuestContext.getResources().getInteger(R.integer.short_animation_duration));
        colorAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                colorAnimation.removeAllListeners();
                colorAnimation.removeAllUpdateListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mViewHolder.answerInputContainer.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        return colorAnimation;
    }

    public View getView() {
        return mViewHolder.getView();
    }

    @Override
    public void deactivate() {
        getView().removeOnLayoutChangeListener(this);
        mViewHolder.answerInput.setOnEditorActionListener(null);
        mQuestContext.getEventBus().stopHandleEvents(this);
        onQuestStop();
        mTitleMediator.deactivate();
        mContentMediator.deactivate();
        mAlternativeAnswerMediator.deactivate();
    }

    @Override
    public void detachView() {
        mTitleMediator.detachView();
        mContentMediator.detachView();
        mAlternativeAnswerMediator.detachView();
    }

    @Override
    public IQuestMediator getTitleMediator() {
        return mTitleMediator;
    }

    @Override
    public IQuestMediator getContentMediator() {
        return mContentMediator;
    }

    @Override
    public IQuestMediator getAlternativeAnswerMediator() {
        return mAlternativeAnswerMediator;
    }

    private void requestFocus() {
        if (questDelayedPlay() && !mQuestContext.questHasState(PLAYED)) {
            KeyboardHost.hideKeyboard(mQuestContext, mViewHolder.answerInput, null);
        } else {
            if (alternativeAnswerButtonIsPresent()) {
                KeyboardHost.hideKeyboard(mQuestContext, mViewHolder.answerInput);
            }
            if (mContentMediator.getAnswerInput() == null) {
                if (mViewHolder.answerInput.getVisibility() == VISIBLE) {
                    KeyboardHost.showKeyboard(mQuestContext, mViewHolder.answerInput);
                } else {
                    KeyboardHost.hideKeyboard(mQuestContext, mViewHolder.answerInput);
                }
            } else {
                if (mContentMediator.getAnswerInput().getVisibility() == VISIBLE) {
                    KeyboardHost.showKeyboard(mQuestContext, mContentMediator.getAnswerInput());
                }
            }
        }
    }

    @Override
    public void onEvent(@NonNull Intent intent) {
        String action = intent.getAction();
        switch (action) {

            case EVENT_QUEST_ATTACH:

                onQuestAttach(mViewHolder.rootContainer);

                break;

            case EVENT_QUEST_START:

                onQuestStart(questDelayedPlay());

                break;

            case EVENT_QUEST_PLAY:

                onQuestPlay(questDelayedPlay());

                break;

            case EVENT_QUEST_REPLAY:

                onQuestReplay();

                break;

            case EVENT_QUEST_PAUSE:

                onQuestPause();

                break;

            case EVENT_QUEST_RESUME:

                onQuestResume();

                break;

            case EVENT_QUEST_STOP:

                onQuestStop();

                break;

            case EVENT_RIGHT_ANSWER:

                if (onRightAnswer()) {
                    mQuestContext.getEventBus().sendEvent(ACTION_SHOW_RIGHT_ANSWER_WINDOW);
                }

                break;

            case EVENT_WRONG_ANSWER:

                if (onWrongAnswer()) {
                    mQuestContext.getEventBus().sendEvent(ACTION_SHOW_WRONG_ANSWER_WINDOW);
                }

                break;

        }
    }

    private void updateVisibilityOfViews(boolean value) {
        View contentView = mContentMediator.getView();
        if (contentView == null) {
            mViewHolder.contentContainer.setVisibility(View.GONE);
        } else {
            mViewHolder.contentContainer.setVisibility(value ? VISIBLE : INVISIBLE);
        }
        if (alternativeAnswerButtonIsPresent()) {
            mViewHolder.alternativeAnswerContainer.setVisibility(value ? VISIBLE : INVISIBLE);
        } else {
            mViewHolder.answerContainer.setVisibility(value ? VISIBLE : INVISIBLE);
        }
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return getClass().getName();
    }

    @Override
    public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft,
                               int oldTop, int oldRight, int oldBottom) {
        updateSize();
    }

    static class QuestViewHolder extends ViewHolder {

        @NonNull
        final ViewGroup rootContainer, titleContainer, answerContainer, contentContainer,
                alternativeAnswerContainer, answerInputContainer;
        @NonNull
        final View answerButton;
        @NonNull
        final EditText answerInput;

        QuestViewHolder(@NonNull Context context) {
            super(context, R.layout.layout_quest_view);
            rootContainer = (ViewGroup) mView.findViewById(R.id.container_root);
            titleContainer = (ViewGroup) mView.findViewById(R.id.container_title);
            answerContainer = (ViewGroup) mView.findViewById(R.id.container_answer);
            answerInputContainer = (ViewGroup) mView.findViewById(R.id.container_answer_input);
            contentContainer = (ViewGroup) mView.findViewById(R.id.container_content_22);
            alternativeAnswerContainer = (ViewGroup) mView.findViewById(R.id.container_alternative_answer);
            answerButton = mView.findViewById(R.id.btn_answer);
            answerInput = (EditText) mView.findViewById(R.id.input_answer);
        }
    }
}