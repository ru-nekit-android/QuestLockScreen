package ru.nekit.android.qls.quest.mediator.time;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.mediator.adapter.IContentContainerViewHolder;

class TimeViewHolder extends RecyclerView.ViewHolder implements IContentContainerViewHolder {

    @NonNull
    private final View mView, mContentContainer, hourHand, minuteHand;

    TimeViewHolder(@NonNull View view) {
        super(view);
        mView = view;
        mContentContainer = view.findViewById(R.id.container_content);
        hourHand = view.findViewById(R.id.hour_hand);
        minuteHand = view.findViewById(R.id.minute_hand);
    }

    @NonNull
    @Override
    public View getView() {
        return mView;
    }

    @Override
    public View getContentContainer() {
        return mContentContainer;
    }

    @NonNull
    public View getHourHand() {
        return hourHand;
    }

    @NonNull
    public View getMinuteHand() {
        return minuteHand;
    }
}