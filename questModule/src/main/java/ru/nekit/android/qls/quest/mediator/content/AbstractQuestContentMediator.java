package ru.nekit.android.qls.quest.mediator.content;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;

public abstract class AbstractQuestContentMediator implements IQuestContentMediator {

    protected QuestContext mQuestContext;
    protected ViewGroup mRootContentContainer;
    protected IQuest mQuest;

    @Override
    public void detachView() {

    }

    @CallSuper
    @Override
    public void create(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mQuest = questContext.getQuest();
    }

    @CallSuper
    @Override
    public void onQuestStart(boolean delayedStart) {
        if (delayedStart) {
            playDelayedStartAnimation();
        }
    }

    @Override
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        mRootContentContainer = rootContentContainer;
    }

    @Override
    public void onQuestShow() {

    }

    protected void playDelayedStartAnimation() {
        View view = getView();
        if (view != null) {
            view.setScaleX(0.1f);
            view.setScaleY(0.1f);
            final View finalView = view;
            view.animate().withLayer().withEndAction(new Runnable() {
                @Override
                public void run() {
                    finalView.setLayerType(View.LAYER_TYPE_NONE, null);
                }
            }).scaleX(1).scaleY(1).setInterpolator(new BounceInterpolator()).
                    setDuration(mQuestContext.getQuestDelayedStartAnimationDuration());
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
    public void onQuestRestart() {

    }

    @CallSuper
    @Override
    public void deactivate() {

    }

    @Override
    public boolean onRightAnswer() {
        return true;
    }

    @Override
    public void onWrongAnswer() {
    }
}