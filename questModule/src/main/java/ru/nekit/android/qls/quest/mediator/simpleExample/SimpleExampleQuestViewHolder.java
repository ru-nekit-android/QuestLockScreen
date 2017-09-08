package ru.nekit.android.qls.quest.mediator.simpleExample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.ViewHolder;

/**
 * Created by nekit on 17.01.17.
 */
public class SimpleExampleQuestViewHolder extends ViewHolder {

    public EditText alternativeAnswerInput;
    TextView rightSideView;
    TextView leftSideView;

    SimpleExampleQuestViewHolder(@NonNull Context context) {
        super(context, R.layout.ql_simple_example);
        alternativeAnswerInput = (EditText) mView.findViewById(R.id.alternative_answer_input);
        leftSideView = (TextView) mView.findViewById(R.id.tv_left_side);
        rightSideView = (TextView) mView.findViewById(R.id.right_side_view);
    }

    @NonNull
    public View getView() {
        return mView;
    }
}
