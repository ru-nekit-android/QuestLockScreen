package ru.nekit.android.qls.utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

public class ViewHolder {

    @NonNull
    final public View view;
    @NonNull
    final protected Context mContext;

    public ViewHolder(@NonNull Context context, @LayoutRes int layoutId) {
        mContext = context;
        view = LayoutInflater.from(context).inflate(layoutId, null);
    }
}