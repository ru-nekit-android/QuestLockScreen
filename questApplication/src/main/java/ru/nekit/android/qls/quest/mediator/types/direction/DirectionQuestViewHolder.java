package ru.nekit.android.qls.quest.mediator.types.direction;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

class DirectionQuestViewHolder extends ViewHolder {

    public View targetView;

    DirectionQuestViewHolder(@NonNull Context context) {
        super(context, R.layout.ql_direction);
        targetView = view.findViewById(R.id.view_target);
    }

}