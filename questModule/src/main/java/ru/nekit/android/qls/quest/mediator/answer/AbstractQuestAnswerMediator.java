package ru.nekit.android.qls.quest.mediator.answer;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.answer.common.IAnswerCallback;
import ru.nekit.android.qls.quest.answer.common.IAnswerChecker;
import ru.nekit.android.qls.quest.common.Quest;

public abstract class AbstractQuestAnswerMediator implements IQuestAnswerMediator {

    protected QuestContext mQuestContext;
    protected Quest mQuest;
    protected ViewGroup mRootContentContainer;
    protected IAnswerChecker mAnswerChecker;
    protected IAnswerCallback mAnswerCallback;

    @CallSuper
    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mQuest = questContext.getQuest();
    }

    @Override
    public void onQuestStart(boolean delayedPlay) {

    }

    @CallSuper
    @Override
    public void onQuestPlay(boolean delayedPlay) {
        if (delayedPlay) {
            startDelayedPlayAnimation();
        }
    }

    private void startDelayedPlayAnimation() {
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
                    .setDuration(mQuestContext.getQuestDelayedPlayAnimationDuration());
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
    public void onQuestReplay() {

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
    public boolean onWrongAnswer() {
        return true;
    }

    @CallSuper
    @Override
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        mRootContentContainer = rootContentContainer;
    }
}