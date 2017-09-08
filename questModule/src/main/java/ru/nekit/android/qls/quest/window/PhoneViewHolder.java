package ru.nekit.android.qls.quest.window;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

class PhoneViewHolder extends ViewHolder {

    RecyclerView allowContactsListView;

    PhoneViewHolder(@NonNull Context context) {
        super(context, R.layout.wsc_phone);
        allowContactsListView = (RecyclerView) getView().findViewById(R.id.list_phone_contacts);
    }
}
