package ru.nekit.android.qls.quest.mediator.shared.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.utils.IViewHolder;

public interface IContentContainerViewHolder extends IViewHolder {

    @NonNull
    View getContentContainer();
}
