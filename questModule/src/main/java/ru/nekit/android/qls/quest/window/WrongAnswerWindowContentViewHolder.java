package ru.nekit.android.qls.quest.window;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.window.WindowContentViewHolder;

public class WrongAnswerWindowContentViewHolder extends WindowContentViewHolder {

    public WrongAnswerWindowContentViewHolder(@NonNull Context context) {
        super(context, R.layout.wc_wrong_answer);
        TextView titleView = (TextView) mView.findViewById(R.id.tv_title);
        titleView.setText(R.string.wrong_answer);
    }

    @Override
    protected int getCloseButtonId() {
        return R.id.btn_ok;
    }
}
