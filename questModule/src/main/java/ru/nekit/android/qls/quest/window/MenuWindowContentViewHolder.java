package ru.nekit.android.qls.quest.window;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.window.WindowContentViewHolder;

class MenuWindowContentViewHolder extends WindowContentViewHolder {

    ViewSwitcher contentContainer;
    View unlockButton, phoneButton;
    ViewGroup buttonContainer;
    TextView titleView;

    MenuWindowContentViewHolder(@NonNull Context context) {
        super(context, R.layout.wc_menu);
        unlockButton = mView.findViewById(R.id.btn_unlock);
        phoneButton = mView.findViewById(R.id.btn_phone);
        contentContainer = (ViewSwitcher) mView.findViewById(R.id.container_content);
        buttonContainer = (ViewGroup) mView.findViewById(R.id.container_button);
        titleView = (TextView) mView.findViewById(R.id.tv_title);
    }
}