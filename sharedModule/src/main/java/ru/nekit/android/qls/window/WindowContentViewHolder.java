package ru.nekit.android.qls.window;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.utils.ViewHolder;
import ru.nekit.android.shared.R;

public class WindowContentViewHolder extends ViewHolder {

    public WindowContentViewHolder(@NonNull Context context, @LayoutRes int layoutId) {
        super(context, layoutId);
    }

    @NonNull
    View getCloseButton() {
        return view.findViewById(getCloseButtonId());
    }

    @IdRes
    protected int getCloseButtonId() {
        return R.id.btn_close;
    }
}