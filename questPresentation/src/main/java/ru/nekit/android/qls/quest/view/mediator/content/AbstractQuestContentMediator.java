package ru.nekit.android.qls.quest.view.mediator.content;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import ru.nekit.android.qls.quest.Quest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.answer.common.AnswerType;

public abstract class AbstractQuestContentMediator implements IQuestContentMediator {

    protected QuestContext mQuestContext;
    protected ViewGroup mRootContentContainer;
    protected Quest mQuest;

    @Override
    public void detachView() {

    }

    @CallSuper
    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mQuest = questContext.getQuest();
    }

    @CallSuper
    @Override
    public void onQuestPlay(boolean delayedPlay) {
        if (delayedPlay) {
            playDelayedStartAnimation();
        }
    }

    @Override
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        mRootContentContainer = rootContentContainer;
    }

    @Override
    public void onQuestStart(boolean delayedPlay) {

    }

    protected void playDelayedStartAnimation() {
        View view = getView();
        if (view != null) {
            view.setScaleX(0f);
            view.setScaleY(0f);
            final View finalView = view;
            view.animate().withLayer().withEndAction(new Runnable() {
                @Override
                public void run() {
                    finalView.setLayerType(View.LAYER_TYPE_NONE, null);
                }
            }).scaleX(1).scaleY(1).setInterpolator(new BounceInterpolator()).
                    setDuration(mQuestContext.getQuestDelayedPlayAnimationDuration());
        }
    }

    @Override
    public void onQuestPause() {

    }

    @Override
    public void onQuestResume() {

    }

    @Override
    public void onQuestStop() {

    }

    @Override
    public void onQuestReplay() {

    }

    @CallSuper
    @Override
    public void deactivate() {

    }

    @Override
    public boolean onAnswer(@NonNull AnswerType answerType) {
        return true;
    }
}