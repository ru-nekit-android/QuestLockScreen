package ru.nekit.android.qls.lockScreen.window;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

public abstract class WindowContentViewHolder extends ViewHolder {

    @NonNull
    final private View closeButton;

    public WindowContentViewHolder(@NonNull Context context, int layoutId) {
        super(context, layoutId);
        closeButton = mView.findViewById(getCloseButtonId());
    }

    @NonNull
    View getCloseButton() {
        return closeButton;
    }

    @IdRes
    protected int getCloseButtonId() {
        return R.id.btn_close;
    }
}