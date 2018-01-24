package ru.nekit.android.qls.quest.view.mediator.types.time;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.types.TimeQuest;
import ru.nekit.android.qls.quest.view.mediator.adapter.SquareItemAdapter;

class TimeAdapter extends SquareItemAdapter<TimeAdapter.TimeViewHolder> {

    @NonNull
    private final View.OnClickListener mClickListener;
    @NonNull
    private final List<Integer> mTimeListData;

    TimeAdapter(@NonNull List<Integer> timeListData, @NonNull View.OnClickListener clickListener) {
        mTimeListData = timeListData;
        mClickListener = clickListener;
    }

    @Override
    public TimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TimeViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ill_time, parent, false));
    }

    @Override
    public void onViewDetachedFromWindow(TimeViewHolder holder) {
        holder.view.setOnClickListener(null);
    }

    @Override
    public void onBindViewHolder(final TimeViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int time = getTime(position);
        int hour = TimeQuest.getTimeHours(time);
        int minute = TimeQuest.getTimeMinutes(time);
        holder.hourHand.setRotation(30 * hour + minute / 2);
        holder.minuteHand.setRotation(6 * minute);
        holder.view.setOnClickListener(mClickListener);
        holder.view.setTag(time);
    }

    protected int getTime(int position) {
        return mTimeListData.get(position);
    }

    @Override
    public int getItemCount() {
        return mTimeListData.size();
    }

    static class TimeViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        final View view, hourHand, minuteHand;

        TimeViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            hourHand = view.findViewById(R.id.hour_hand);
            minuteHand = view.findViewById(R.id.minute_hand);
        }
    }
}