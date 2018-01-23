package ru.nekit.android.qls.lockScreen.content.common;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.lockScreen.TransitionChoreograph;

public abstract class BaseLockScreenContentMediator {

    @NonNull
    protected TransitionChoreograph mTransitionChoreograph;

    public void setTransitionChoreograph(@NonNull TransitionChoreograph choreograph) {
        mTransitionChoreograph = choreograph;
    }

    @NonNull
    public abstract ILockScreenContentViewHolder getViewHolder();

    public abstract void deactivate();

    public abstract void detachView();

    public abstract void attachView();

}