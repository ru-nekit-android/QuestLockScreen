package ru.nekit.android.qls.lockScreen.content;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.lockScreen.TransitionChoreograph;

public abstract class AbstractLockScreenContentMediator {

    @NonNull
    protected TransitionChoreograph mTransitionChoreograph;

    public void setTransitionChoreograph(@NonNull TransitionChoreograph choreograph) {
        mTransitionChoreograph = choreograph;
    }

    @NonNull
    public abstract ILockScreenContentContainerViewHolder getContentContainerViewHolder();

    public abstract void deactivate();

    public abstract void detachView();

    public abstract void attachView();

}