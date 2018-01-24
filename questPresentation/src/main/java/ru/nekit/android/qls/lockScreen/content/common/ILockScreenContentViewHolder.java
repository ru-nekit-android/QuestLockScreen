package ru.nekit.android.qls.lockScreen.content.common;

import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.quest.view.mediator.IContentContainerViewHolder;

public interface ILockScreenContentViewHolder extends IContentContainerViewHolder {

    @NonNull
    View getTitleContentContainer();

}