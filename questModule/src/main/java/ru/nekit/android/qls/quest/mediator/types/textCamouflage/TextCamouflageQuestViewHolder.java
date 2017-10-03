package ru.nekit.android.qls.quest.mediator.types.textCamouflage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

/**
 * Created by nekit on 17.01.17.
 */
class TextCamouflageQuestViewHolder extends ViewHolder {

    RecyclerView textViewGrid;

    TextCamouflageQuestViewHolder(@NonNull Context context) {
        super(context, R.layout.ql_text_camouflage);
        textViewGrid = (RecyclerView) mView.findViewById(R.id.grid_text_view);
    }

}
