package ru.nekit.android.qls.quest.mediator.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;

public class SelectableDrawableResourceAdapter extends DrawableResourceAdapter {

    @NonNull
    private View.OnClickListener mClickListener;
    @NonNull
    private List mTagList;

    public SelectableDrawableResourceAdapter(@LayoutRes int layoutResId,
                                             @NonNull List tagList,
                                             @NonNull List<Integer> imageResourceIds,
                                             @NonNull View.OnClickListener clickListener) {
        super(layoutResId, imageResourceIds);
        mTagList = tagList;
        mClickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(final DrawableResourceViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        View view = holder.getView();
        if (mClickListener != null) {
            view.setOnClickListener(mClickListener);
        }
        view.setTag(mTagList == null ? null : mTagList.get(position));
    }

}
