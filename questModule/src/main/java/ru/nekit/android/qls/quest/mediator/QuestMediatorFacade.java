package ru.nekit.android.qls.quest.mediator;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import ru.nekit.android.qls.CONST;
import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestContextEvent;
import ru.nekit.android.qls.quest.answer.IAnswerChecker;
import ru.nekit.android.qls.utils.KeyboardHost;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.RelativeLayout.ABOVE;
import static android.widget.RelativeLayout.BELOW;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.DELAYED_START;

public class QuestMediatorFacade implements View.OnClickListener, IQuestMediatorFacade, EventBus.IEventHandler {

    private QuestContext mQuestContext;
    private IQuest mQuest;
    private QuestViewHolder mViewHolder;
    @NonNull
    private IQuestTitleMediator mTitleMediator;
    @NonNull
    private IQuestContentMediator mContentMediator;
    @NonNull
    private IQuestAlternativeAnswerMediator mAlternativeAnswerMediator;
    @NonNull
    private IAnswerChecker mAnswerChecker;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            updateSizeInternal();
        }
    };

    public QuestMediatorFacade(@NonNull IAnswerChecker answerChecker,
                               @NonNull IQuestTitleMediator questTitleMediator,
                               @Nullable IQuestContentMediator questContentMediator,
                               @Nullable IQuestAlternativeAnswerMediator questAlternativeAnswerMediator

    ) {
        mTitleMediator = questTitleMediator;
        mContentMediator = questContentMediator == null ?
                new EmptyContentMediator() : questContentMediator;
        mAlternativeAnswerMediator = questAlternativeAnswerMediator == null ?
                new QuestAlternativeAnswerMediator() : questAlternativeAnswerMediator;
        mAnswerChecker = answerChecker;
    }

    private void updateSizeInternal() {
        int width = getView().getWidth();
        int height = getView().getHeight();
        updateSize(width, height);
    }

    @Override
    public void updateSize(int width, int height) {
        boolean contentViewIsPresent = mContentMediator.getView() != null
                && !mContentMediator.isDestroyed();
        boolean alternativeAnswerIsPresent = !mAlternativeAnswerMediator.isDestroyed();
        if (mQuestContext.hasQuestState(QuestContext.QuestState.STARTED)) {
            if (contentViewIsPresent) {
                mContentMediator.updateSize(mViewHolder.contentContainer.getWidth(),
                        mViewHolder.contentContainer.getHeight());
            }
            if (alternativeAnswerIsPresent) {
                mAlternativeAnswerMediator.updateSize(
                        mViewHolder.alternativeAnswerButtonContainer.getWidth(),
                        mViewHolder.alternativeAnswerButtonContainer.getHeight()
                );
            }
        }
        if (contentViewIsPresent) {
            mViewHolder.contentContainer.requestLayout();
        }
        if (alternativeAnswerIsPresent &&
                mViewHolder.alternativeAnswerButtonContainer.getVisibility() == VISIBLE) {
            mViewHolder.alternativeAnswerButtonContainer.requestLayout();
        }
    }

    @Override
    public void playAnimationOnDelayedStart(int duration, @Nullable View view) {
        if (alternativeAnswerButtonIsPresent()) {
            mAlternativeAnswerMediator.playAnimationOnDelayedStart(duration,
                    mViewHolder.alternativeAnswerButtonContainer);
        } else {
            view = mViewHolder.answerContainer;
            if (view != null) {
                view.setScaleX(0.1f);
                view.setScaleY(0.1f);
                view.animate().scaleX(1).scaleY(1).setInterpolator(new BounceInterpolator()).setDuration(duration);
            }
        }

    }

    private boolean questDelayedStart() {
        return mQuestContext.hasQuestState(DELAYED_START);
    }

    @Override
    public void init(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mQuest = questContext.getQuest();
        mViewHolder = new QuestViewHolder(mQuestContext);
        mTitleMediator.init(mQuestContext);
        mViewHolder.titleContainer.addView(mTitleMediator.getView());
        mContentMediator.init(mQuestContext);
        View contentView = mContentMediator.getView();
        RelativeLayout.LayoutParams answerContainerLayoutParams =
                (RelativeLayout.LayoutParams) mViewHolder.answerContainer.getLayoutParams();
        ViewGroup.LayoutParams alternativeAnswerButtonContainerLayoutParams =
                mViewHolder.alternativeAnswerButtonContainer.getLayoutParams();
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
        mViewHolder.answerContainer.requestLayout();
        mViewHolder.alternativeAnswerButtonContainer.requestLayout();
        if (contentView != null) {
            mViewHolder.contentContainer.addView(mContentMediator.getView());
        }
        mAlternativeAnswerMediator.setAnswerCallback(mQuestContext);
        mAlternativeAnswerMediator.setAnswerChecker(mAnswerChecker);
        mAlternativeAnswerMediator.init(mQuestContext);
        View alternativeAnswerView = mAlternativeAnswerMediator.getView();
        if (alternativeAnswerView == null) {
            List<View> buttonList = mAlternativeAnswerMediator.getAnswerButtonList();
            if (buttonList != null) {
                for (View button : buttonList) {
                    mViewHolder.alternativeAnswerButtonContainer.addView(button);
                }
            }
        } else {
            mViewHolder.alternativeAnswerButtonContainer.addView(alternativeAnswerView);
        }
        boolean answerInputVisible = !alternativeAnswerIsPresent();
        mViewHolder.answerInput.setVisibility(answerInputVisible ? VISIBLE : GONE);
        boolean showAnswerButton = !alternativeAnswerButtonIsPresent() && mQuestContext.answerButtonVisible();
        mViewHolder.answerButton.setVisibility(showAnswerButton ? VISIBLE : GONE);
        if (!answerInputVisible && showAnswerButton) {
            LinearLayout.LayoutParams answerButtonLayoutParameters =
                    (LinearLayout.LayoutParams) mViewHolder.answerButton.getLayoutParams();
            answerButtonLayoutParameters.weight = 1;
            mViewHolder.answerButton.setLayoutParams(answerButtonLayoutParameters);
        }
        mViewHolder.alternativeAnswerButtonContainer.setVisibility(alternativeAnswerButtonIsPresent() ? VISIBLE : GONE);
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
        getView().getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        questContext.getEventBus().handleEvents(this,
                QuestContextEvent.EVENT_QUEST_INIT,
                QuestContextEvent.EVENT_QUEST_START,
                QuestContextEvent.EVENT_QUEST_RESUME);
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
                        KeyboardHost.hideKeyboard(mQuestContext, getView());
                    } else {
                        mQuestContext.wrongAnswer();
                    }
                } else {
                    mQuestContext.wrongStringInputFormat();
                }
            } else {
                mQuestContext.emptyAnswer();
            }
        }
    }

    public View getView() {
        return mViewHolder.getView();
    }

    @Override
    public void destroy() {
        getView().getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        mQuestContext.getEventBus().stopHandleEvents(this);
        mTitleMediator.destroy();
        mContentMediator.destroy();
        mAlternativeAnswerMediator.destroy();
        KeyboardHost.hideKeyboard(mQuestContext, getView());
    }

    @Override
    public boolean isDestroyed() {
        return false;
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

    @Override
    public void onEvent(@NonNull Intent intent) {
        String action = intent.getAction();

        switch (action) {

            case QuestContextEvent.EVENT_QUEST_INIT:

                updateSizeInternal();
                updateVisibilityOfViews(!questDelayedStart());

                break;

            case QuestContextEvent.EVENT_QUEST_START:

                requestFocus();
                updateVisibilityOfViews(true);
                if (CONST.PLAY_ANIMATION_ON_DELAYED_START &&
                        questDelayedStart()) {
                    int duration = mQuestContext.getQuestDelayedStartAnimationDuration();
                    mContentMediator.playAnimationOnDelayedStart(duration, null);
                    mAlternativeAnswerMediator.playAnimationOnDelayedStart(duration,
                            mViewHolder.alternativeAnswerButtonContainer);
                    playAnimationOnDelayedStart(duration, null);
                }

                break;

            case QuestContextEvent.EVENT_QUEST_RESUME:

                requestFocus();

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
        mViewHolder.answerContainer.setVisibility(value ? VISIBLE : INVISIBLE);
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return getClass().getName();
    }
}