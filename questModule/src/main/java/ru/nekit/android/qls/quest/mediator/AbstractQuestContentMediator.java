package ru.nekit.android.qls.quest.mediator;


import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.BounceInterpolator;

public abstract class AbstractQuestContentMediator implements IQuestContentMediator {

    private boolean mIsDestroyed;

    @Override
    public boolean isDestroyed() {
        return mIsDestroyed;
    }

    @Override
    @CallSuper
    public void destroy() {
        mIsDestroyed = true;
    }

    @Override
    public void playAnimationOnDelayedStart(int duration, @Nullable View view) {
        if (view == null) {
            view = getView();
        }
        if (view != null) {
            view.setScaleX(0.1f);
            view.setScaleY(0.1f);
            view.animate().scaleX(1).scaleY(1).setInterpolator(new BounceInterpolator()).setDuration(duration);
        }
    }
}
