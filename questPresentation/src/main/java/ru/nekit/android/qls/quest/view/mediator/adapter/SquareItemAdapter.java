package ru.nekit.android.qls.quest.view.mediator.adapter;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class SquareItemAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    private int mSize;

    public void setSize(int value) {
        mSize = value;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    @CallSuper
    public void onBindViewHolder(T holder, int position) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height = mSize;
        layoutParams.width = mSize;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

}