package ru.nekit.android.qls.lockScreen.content;

import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.quest.mediator.shared.adapter.IContentContainerViewHolder;

public interface ILockScreenContentContainerViewHolder extends IContentContainerViewHolder {

    @NonNull
    View getTitleContentContainer();

}