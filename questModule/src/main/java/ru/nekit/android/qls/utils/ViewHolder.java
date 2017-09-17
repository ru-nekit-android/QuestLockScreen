package ru.nekit.android.qls.utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

public class ViewHolder implements IViewHolder {

    @NonNull
    final protected View mView;
    protected Context mContext;

    public ViewHolder(@NonNull Context context, @LayoutRes int layoutId) {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(layoutId, null);
    }

    @NonNull
    public View getView() {
        return mView;
    }
}