package ru.nekit.android.qls.quest.window;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.window.WindowContentViewHolder;

public class RightAnswerWindowContentViewHolder extends WindowContentViewHolder {

    public TextView titleView;

    public RightAnswerWindowContentViewHolder(@NonNull Context context,
                                              @LayoutRes int layoutRedId) {
        super(context, layoutRedId);
        titleView = (TextView) mView.findViewById(R.id.tv_title);
    }

    @Override
    protected int getCloseButtonId() {
        return R.id.btn_ok;
    }

}