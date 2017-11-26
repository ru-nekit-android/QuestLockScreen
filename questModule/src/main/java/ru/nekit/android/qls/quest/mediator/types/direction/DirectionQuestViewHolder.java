package ru.nekit.android.qls.quest.mediator.types.direction;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

class DirectionQuestViewHolder extends ViewHolder {

    View targetObject;

    DirectionQuestViewHolder(@NonNull Context context) {
        super(context, R.layout.ql_direction);
        targetObject = getView().findViewById(R.id.view_target_object);
    }

}
