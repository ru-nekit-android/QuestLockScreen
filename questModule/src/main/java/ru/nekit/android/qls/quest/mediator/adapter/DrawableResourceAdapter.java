package ru.nekit.android.qls.quest.mediator.adapter;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

public class DrawableResourceAdapter extends
        RecyclerView.Adapter<DrawableResourceViewHolder> {

    @LayoutRes
    private int mLayoutResId;
    @DrawableRes
    private List<Integer> mImageResourceIds;

    public DrawableResourceAdapter(@LayoutRes int layoutResId,
                                   @NonNull @DrawableRes List<Integer> imageResourceIds) {
        mLayoutResId = layoutResId;
        mImageResourceIds = imageResourceIds;
    }

    @Override
    public DrawableResourceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DrawableResourceViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(mLayoutResId, parent, false));
    }

    @Override
    public void onBindViewHolder(final DrawableResourceViewHolder holder, int position) {
        holder.setImageResource(mImageResourceIds.get(position));
    }

    @Override
    public void onViewDetachedFromWindow(DrawableResourceViewHolder holder) {
        holder.getView().setOnClickListener(null);
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return mImageResourceIds.size();
    }

}