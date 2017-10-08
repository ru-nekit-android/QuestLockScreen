package ru.nekit.android.qls.quest.mediator.answer;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.answer.shared.IAlternativeAnswerVariantAdapter;
import ru.nekit.android.qls.quest.answer.shared.IAnswerCallback;
import ru.nekit.android.qls.quest.answer.shared.IAnswerChecker;

public class QuestAlternativeAnswerMediator implements View.OnClickListener,
        IQuestAlternativeAnswerMediator {

    protected QuestContext mQuestContext;
    protected IQuest mQuest;
    protected ViewGroup mRootContentContainer;
    protected List<View> mButtonList;
    private IAnswerChecker mAnswerChecker;
    private IAnswerCallback mAnswerCallback;
    @Nullable
    private IAlternativeAnswerVariantAdapter mButtonListAdapter;

    public QuestAlternativeAnswerMediator() {
    }

    public QuestAlternativeAnswerMediator(@Nullable IAlternativeAnswerVariantAdapter buttonListAdapter) {
        mButtonListAdapter = buttonListAdapter;
    }

    @CallSuper
    @Override
    public void create(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mQuest = questContext.getQuest();
        mButtonList = new ArrayList<>();
    }

    @Override
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        mRootContentContainer = rootContentContainer;
    }

    @Override
    public void onQuestShow() {

    }

    @CallSuper
    @Override
    public void onQuestStart(boolean delayedStart) {
        if (delayedStart) {
            playDelayedStartAnimation();
        }
    }

    protected void playDelayedStartAnimation() {
        View view = mRootContentContainer;
        if (getView() != null) {
            view = getView();
        }
        if (view != null) {
            view.setScaleX(0);
            view.setScaleY(0);
            final View finalView = view;
            view.animate().withLayer().withEndAction(new Runnable() {
                @Override
                public void run() {
                    finalView.setLayerType(View.LAYER_TYPE_NONE, null);
                }
            }).scaleX(1).scaleY(1).setInterpolator(new BounceInterpolator())
                    .setDuration(mQuestContext.getQuestDelayedStartAnimationDuration());
        }
    }

    @Override
    public void onQuestPause() {

    }

    @Override
    public void onQuestResume() {

    }

    @CallSuper
    @Override
    public void onQuestRestart() {

    }

    @CallSuper
    @Override
    public void onQuestStop() {

    }

    @CallSuper
    @Override
    public void deactivate() {

    }

    @Override
    public void detachView() {

    }

    @Override
    final public void setAnswerCallback(@NonNull IAnswerCallback answerCallback) {
        mAnswerCallback = answerCallback;
    }

    @Override
    final public void setAnswerChecker(@NonNull IAnswerChecker answerChecker) {
        mAnswerChecker = answerChecker;
    }

    private View createSimpleButton(String label, Object tag) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        View button = createButton(tag, params);
        button.setLayoutParams(params);
        if (button instanceof TextView) {
            ((TextView) button).setText(label);
        }
        button.setTag(tag);
        button.setOnClickListener(this);
        return button;
    }

    @NonNull
    protected View createButton(Object tag, @NonNull LinearLayout.LayoutParams layoutParams) {
        return mQuestContext.createButton();
    }

    protected void fillButtonListWithAvailableVariants() {
        Object[] availableVariants = mQuest.getAvailableAnswerVariants();
        if (availableVariants != null) {
            for (Object variant : availableVariants) {
                String label = mButtonListAdapter == null ? variant.toString() :
                        mButtonListAdapter.adapt(mQuestContext, variant);
                if (label != null) {
                    mButtonList.add(createSimpleButton(label, variant));
                }
            }
        }
    }

    @Nullable
    @Override
    public List<View> getAnswerButtonList() {
        return mButtonList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onClick(@NonNull View view) {
        if (mAnswerChecker.checkAlternativeInput(mQuest, view.getTag())) {
            mAnswerCallback.rightAnswer();
        } else {
            mAnswerCallback.wrongAnswer();
        }
    }

    @Override
    public void updateSize() {
    }

    @Override
    public View getView() {
        return null;
    }


    @Override
    public boolean onRightAnswer() {
        return true;
    }

    @Override
    public void onWrongAnswer() {

    }
}