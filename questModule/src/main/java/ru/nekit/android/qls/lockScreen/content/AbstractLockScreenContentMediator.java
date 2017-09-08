package ru.nekit.android.qls.lockScreen.content;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.lockScreen.TransitionChoreograph;

public abstract class AbstractLockScreenContentMediator {

    TransitionChoreograph mTransitionChoreograph;

    public void setTransitionChoreograph(TransitionChoreograph choreograph) {
        mTransitionChoreograph = choreograph;
    }

    @NonNull
    public abstract ILockScreenContentContainerViewHolder getViewHolder();

    public abstract void destroy();

    public abstract void detachView();

    public abstract void attachView();

}