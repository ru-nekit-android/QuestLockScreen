package ru.nekit.android.qls.quest.view.mediator.types.perimeter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

class PerimeterQuestViewHolder extends ViewHolder {

    TextView aFigureSideLabel, bFigureSideLabel;
    View figureView;

    PerimeterQuestViewHolder(@NonNull Context context) {
        super(context, R.layout.ql_perimeter);
        figureView = view.findViewById(R.id.view_figure);
        aFigureSideLabel = (TextView) view.findViewById(R.id.field_figure_side_a);
        bFigureSideLabel = (TextView) view.findViewById(R.id.field_figure_side_b);
    }

}
