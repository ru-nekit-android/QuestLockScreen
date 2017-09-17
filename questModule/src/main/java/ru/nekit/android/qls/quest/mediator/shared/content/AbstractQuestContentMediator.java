package ru.nekit.android.qls.quest.mediator.shared.content;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;

public abstract class AbstractQuestContentMediator implements IQuestContentMediator {

    protected QuestContext mQuestContext;
    @NonNull
    protected ViewGroup mRootContentContainer;
    protected IQuest mQuest;

    @Override
    public void deactivate() {

    }

    @Override
    public void detachView() {

    }

    @CallSuper
    @Override
    public void onCreateQuest(@NonNull QuestContext questContext,
                              @NonNull ViewGroup rootContentContainer) {
        mQuestContext = questContext;
        mRootContentContainer = rootContentContainer;
        mQuest = questContext.getQuest();
    }

    @CallSuper
    @Override
    public void onStartQuest(boolean playAnimationOnDelayedStart) {
        if (playAnimationOnDelayedStart) {
            View view = getView();
            if (view != null) {
                view.setScaleX(0.1f);
                view.setScaleY(0.1f);
                view.animate().scaleX(1).scaleY(1).setInterpolator(new BounceInterpolator()).
                        setDuration(mQuestContext.getQuestDelayedStartAnimationDuration());
            }
        }
    }
}