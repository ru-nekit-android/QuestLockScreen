package ru.nekit.android.qls.quest.mediator.types.perimeter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

/**
 * Created by nekit on 17.01.17.
 */
class PerimeterQuestViewHolder extends ViewHolder {

    TextView aFigureSideLabel, bFigureSideLabel;
    View figureView;

    PerimeterQuestViewHolder(@NonNull Context context) {
        super(context, R.layout.ql_perimeter);
        figureView = mView.findViewById(R.id.view_figure);
        aFigureSideLabel = (TextView) mView.findViewById(R.id.field_figure_side_a);
        bFigureSideLabel = (TextView) mView.findViewById(R.id.field_figure_side_b);
    }

}
