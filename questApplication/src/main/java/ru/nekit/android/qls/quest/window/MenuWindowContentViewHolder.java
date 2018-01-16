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

    final ViewSwitcher contentContainer;
    final View unlockButton, phoneButton;
    final ViewGroup buttonContainer;
    final TextView titleView;

    MenuWindowContentViewHolder(@NonNull Context context) {
        super(context, R.layout.wc_menu);
        unlockButton = view.findViewById(R.id.btn_unlock);
        phoneButton = view.findViewById(R.id.btn_phone);
        contentContainer = (ViewSwitcher) view.findViewById(R.id.container_content);
        buttonContainer = (ViewGroup) view.findViewById(R.id.container_button);
        titleView = (TextView) view.findViewById(R.id.tv_title);
    }
}