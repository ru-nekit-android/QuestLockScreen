package ru.nekit.android.qls.quest.mediator;

import android.support.annotation.NonNull;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.utils.ViewHolder;

class QuestTitleViewHolder extends ViewHolder {

    TextView titleView;

    QuestTitleViewHolder(@NonNull QuestContext context) {
        super(context, R.layout.layout_quest_title);
        titleView = (TextView) mView.findViewById(R.id.tv_title);
    }

}