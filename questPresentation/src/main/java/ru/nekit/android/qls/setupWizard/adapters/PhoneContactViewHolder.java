package ru.nekit.android.qls.setupWizard.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ru.nekit.android.qls.R;

class PhoneContactViewHolder extends RecyclerView.ViewHolder {

    View actionButton;
    TextView titleView, informationView;

    PhoneContactViewHolder(View view) {
        super(view);
        titleView = (TextView) view.findViewById(R.id.tv_title);
        informationView = (TextView) view.findViewById(R.id.tv_information);
        actionButton = view.findViewById(R.id.btn_action);
    }
}
