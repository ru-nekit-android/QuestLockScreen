package ru.nekit.android.qls.quest.window;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.window.WindowContentViewHolder;

public class AnswerWindowContainer extends WindowContentViewHolder {

    @NonNull
    final ViewGroup contentContainer, toolContainer;

    AnswerWindowContainer(@NonNull Context context) {
        super(context, R.layout.wc_answer);
        toolContainer = (ViewGroup) view.findViewById(R.id.container_tool);
        contentContainer = (ViewGroup) view.findViewById(R.id.container_content);
    }

    @Override
    protected int getCloseButtonId() {
        return R.id.btn_ok;
    }

}