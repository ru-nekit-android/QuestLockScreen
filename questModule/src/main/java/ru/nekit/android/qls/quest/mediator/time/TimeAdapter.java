package ru.nekit.android.qls.quest.mediator.time;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.types.TimeQuest;

class TimeAdapter extends RecyclerView.Adapter<TimeViewHolder> {

    @NonNull
    private final View.OnClickListener mClickListener;
    @NonNull
    private final List<Integer> mDataList;

    @SuppressWarnings("unchecked")
    TimeAdapter(@NonNull List timeList, @NonNull View.OnClickListener clickListener) {
        mDataList = timeList;
        mClickListener = clickListener;
    }

    @Override
    public TimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TimeViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ql_time_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final TimeViewHolder holder, int position) {
        int time = getTime(position);
        int hour = TimeQuest.getTimeHours(time);
        int minute = TimeQuest.getTimeMinutes(time);
        holder.getHourHand().setRotation(30 * hour + minute / 2);
        holder.getMinuteHand().setRotation(6 * minute);
        holder.getContentContainer().setOnClickListener(mClickListener);
        holder.getContentContainer().setTag(time);
    }

    protected int getTime(int position) {
        return mDataList.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

}