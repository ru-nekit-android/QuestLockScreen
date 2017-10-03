package ru.nekit.android.qls.quest.mediator.types.time;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.mediator.adapter.SquareItemAdapter;
import ru.nekit.android.qls.quest.types.TimeQuest;
import ru.nekit.android.qls.utils.IViewHolder;

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
        holder.getView().setOnClickListener(null);
    }

    @Override
    public void onBindViewHolder(final TimeViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int time = getTime(position);
        int hour = TimeQuest.getTimeHours(time);
        int minute = TimeQuest.getTimeMinutes(time);
        holder.getHourHand().setRotation(30 * hour + minute / 2);
        holder.getMinuteHand().setRotation(6 * minute);
        holder.getView().setOnClickListener(mClickListener);
        holder.getView().setTag(time);
    }

    protected int getTime(int position) {
        return mTimeListData.get(position);
    }

    @Override
    public int getItemCount() {
        return mTimeListData.size();
    }

    static class TimeViewHolder extends RecyclerView.ViewHolder implements IViewHolder {

        @NonNull
        private final View mView, hourHand, minuteHand;

        TimeViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            hourHand = view.findViewById(R.id.hour_hand);
            minuteHand = view.findViewById(R.id.minute_hand);
        }

        @NonNull
        @Override
        public View getView() {
            return mView;
        }

        @NonNull
        View getHourHand() {
            return hourHand;
        }

        @NonNull
        View getMinuteHand() {
            return minuteHand;
        }
    }
}