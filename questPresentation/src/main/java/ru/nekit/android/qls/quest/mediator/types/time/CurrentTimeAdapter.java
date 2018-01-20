package ru.nekit.android.qls.quest.mediator.types.time;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;

import ru.nekit.android.qls.quest.types.CurrentTimeQuest;
import ru.nekit.android.qls.utils.TimeUtils;

class CurrentTimeAdapter extends TimeAdapter {

    @NonNull
    private final CurrentTimeQuest mQuest;

    CurrentTimeAdapter(@NonNull CurrentTimeQuest quest,
                       @NonNull List<Integer> listData,
                       @NonNull View.OnClickListener clickListener) {
        super(listData, clickListener);
        mQuest = quest;
    }

    @Override
    protected int getTime(int position) {
        int delta = (int) ((TimeUtils.getCurrentTime() - mQuest.getTimeStamp()) / 1000 / 60);
        return mQuest.leftNode[position] + delta;
    }

    @Override
    public void onBindViewHolder(final TimeViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.view.setTag(position);
    }
}