package ru.nekit.android.qls.quest;

import android.content.Context;
import android.content.Intent;
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
import ru.nekit.android.qls.lockScreen.window.Window;
import ru.nekit.android.qls.quest.answer.shared.IAnswerChecker;
import ru.nekit.android.qls.quest.base.Quest;
import ru.nekit.android.qls.quest.mediator.IQuestMediator;
import ru.nekit.android.qls.quest.mediator.answer.IQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.answer.QuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.content.EmptyQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.content.IQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.title.IQuestTitleMediator;
import ru.nekit.android.qls.quest.window.RightAnswerWindow;
import ru.nekit.android.qls.quest.window.WrongAnswerWindowContentViewHolder;
import ru.nekit.android.qls.utils.KeyboardHost;
import ru.nekit.android.qls.utils.ViewHolder;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.RelativeLayout.ABOVE;
import static android.widget.RelativeLayout.BELOW;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.DELAYED_START;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.STARTED;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_ATTACH;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_PAUSE;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_RESTART;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_RESUME;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_SHOW;
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
                EVENT_QUEST_SHOW,
                EVENT_QUEST_START,
                EVENT_QUEST_RESTART,
                EVENT_QUEST_PAUSE,
                EVENT_QUEST_RESUME,
                EVENT_QUEST_STOP,
                EVENT_WRONG_ANSWER,
                EVENT_RIGHT_ANSWER
        );
    }

    @Override
    public void updateSize() {
        boolean contentViewIsPresent = false, alternativeAnswerIsPresent = false;
        //if (mQuestContext.questHasState(STARTED) && !mQuestContext.questHasState(PAUSED))
        {
            contentViewIsPresent = mContentMediator.getView() != null;
            alternativeAnswerIsPresent =
                    mViewHolder.alternativeAnswerContainer.getVisibility() == VISIBLE;
            if (contentViewIsPresent) {
                //mContentMediator.updateSize();
            }
            if (alternativeAnswerIsPresent) {
                // mAlternativeAnswerMediator.updateSize();
            }
        }
        /*if (contentViewIsPresent) {
            mViewHolder.contentContainer.requestLayout();
        }
        if (alternativeAnswerIsPresent) {
            mViewHolder.alternativeAnswerContainer.requestLayout();
        }*/
    }

    private boolean questDelayedStart() {
        return mQuestContext.questHasState(DELAYED_START);
    }

    @Override
    public void create(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mQuest = questContext.getQuest();
        mViewHolder = new QuestViewHolder(questContext);
        mTitleMediator.create(questContext);
        mContentMediator.create(questContext);
        mAlternativeAnswerMediator.create(questContext);
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
        mViewHolder.getView().addOnLayoutChangeListener(this);
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
        updateVisibilityOfViews(!questDelayedStart());
    }

    @Override
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        mTitleMediator.onQuestAttach(mViewHolder.titleContainer);
        mContentMediator.onQuestAttach(mViewHolder.contentContainer);
        mAlternativeAnswerMediator.onQuestAttach(mViewHolder.alternativeAnswerContainer);
    }

    @Override
    public void onQuestShow() {
        mTitleMediator.onQuestShow();
        mContentMediator.onQuestShow();
        mAlternativeAnswerMediator.onQuestShow();
    }

    @Override
    public void onQuestStart(boolean delayedStart) {
        updateVisibilityOfViews(true);
        mViewHolder.titleContainer.requestLayout();
        mViewHolder.contentContainer.requestLayout();
        mViewHolder.alternativeAnswerContainer.requestLayout();
        mTitleMediator.onQuestStart(delayedStart);
        mContentMediator.onQuestStart(delayedStart);
        if (delayedStart) {
            if (alternativeAnswerButtonIsPresent()) {
                mAlternativeAnswerMediator.onQuestStart(true);
            } else {
                int duration = mQuestContext.getQuestDelayedStartAnimationDuration();
                View view = mViewHolder.answerContainer;
                if (view != null) {
                    view.setScaleX(0f);
                    view.setScaleY(0f);
                    view.animate().withLayer().scaleX(1).scaleY(1).
                            setInterpolator(new BounceInterpolator()).setDuration(duration);
                }
            }
        } else {
            mAlternativeAnswerMediator.onQuestStart(false);
        }
        requestFocus();
    }

    @Override
    public boolean onRightAnswer() {
        boolean showRightAnswerWindow = true;
        showRightAnswerWindow = showRightAnswerWindow && mTitleMediator.onRightAnswer();
        showRightAnswerWindow = showRightAnswerWindow && mContentMediator.onRightAnswer();
        showRightAnswerWindow = showRightAnswerWindow && mAlternativeAnswerMediator.onRightAnswer();
        if (showRightAnswerWindow) {
            new RightAnswerWindow.Builder(mQuestContext).
                    setContent(R.layout.wc_right_answer).
                    setStyle(R.style.Window_RightAnswer).
                    open();
        }
        return showRightAnswerWindow;
    }

    @Override
    public void onWrongAnswer() {
        new Window(mQuestContext,
                new WrongAnswerWindowContentViewHolder(mQuestContext),
                R.style.Window_WrongAnswer).open();
        mTitleMediator.onWrongAnswer();
        mContentMediator.onWrongAnswer();
        mAlternativeAnswerMediator.onWrongAnswer();
    }

    @Override
    public void onQuestRestart() {
        mTitleMediator.onQuestRestart();
        mContentMediator.onQuestRestart();
        mAlternativeAnswerMediator.onQuestRestart();
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
    public void deactivate() {
        getView().removeOnLayoutChangeListener(this);
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
        if (questDelayedStart() && !mQuestContext.questHasState(STARTED)) {
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

            case EVENT_QUEST_SHOW:

                onQuestShow();

                break;

            case EVENT_QUEST_START:

                onQuestStart(questDelayedStart());

                break;

            case EVENT_QUEST_RESTART:

                onQuestRestart();

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

                onRightAnswer();

                break;

            case EVENT_WRONG_ANSWER:

                onWrongAnswer();

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

    @Override
    public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft,
                               int oldTop, int oldRight, int oldBottom) {
        updateSize();
    }

    static class QuestViewHolder extends ViewHolder {

        @NonNull
        final ViewGroup rootContainer, titleContainer, answerContainer, contentContainer, alternativeAnswerContainer;
        @NonNull
        final View answerButton;
        @NonNull
        final EditText answerInput;

        QuestViewHolder(@NonNull Context context) {
            super(context, R.layout.layout_quest);
            rootContainer = (ViewGroup) mView.findViewById(R.id.container_root);
            titleContainer = (ViewGroup) mView.findViewById(R.id.container_title);
            answerContainer = (ViewGroup) mView.findViewById(R.id.container_answer);
            contentContainer = (ViewGroup) mView.findViewById(R.id.container_content);
            alternativeAnswerContainer = (ViewGroup) mView.findViewById(R.id.container_alternative_answer);
            answerButton = mView.findViewById(R.id.btn_answer);
            answerInput = (EditText) mView.findViewById(R.id.input_answer);
        }
    }
}