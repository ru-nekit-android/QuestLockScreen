package ru.nekit.android.qls.quest.mediator.types.direction;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

class DirectionQuestViewHolder extends ViewHolder {

    public TextView rightMessage, wrongMessage;
    public View targetView;

    DirectionQuestViewHolder(@NonNull Context context) {
        super(context, R.layout.ql_direction);
        targetView = view.findViewById(R.id.view_target);
        rightMessage = (TextView) view.findViewById(R.id.tv_message_right);
        wrongMessage = (TextView) view.findViewById(R.id.tv_message_wrong);
    }

}