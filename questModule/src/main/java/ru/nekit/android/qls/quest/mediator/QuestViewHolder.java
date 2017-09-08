package ru.nekit.android.qls.quest.mediator;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.utils.ViewHolder;

class QuestViewHolder extends ViewHolder {

    ViewGroup titleContainer, answerContainer, contentContainer, alternativeAnswerButtonContainer;
    ImageButton answerButton;
    EditText answerInput;

    QuestViewHolder(@NonNull QuestContext context) {
        super(context, R.layout.layout_quest);
        titleContainer = (ViewGroup) mView.findViewById(R.id.container_title);
        answerContainer = (ViewGroup) mView.findViewById(R.id.container_answer);
        contentContainer = (ViewGroup) mView.findViewById(R.id.container_content);
        alternativeAnswerButtonContainer = (ViewGroup) mView.findViewById(R.id.container_alternative_answer_buttons);
        answerButton = (ImageButton) mView.findViewById(R.id.btn_answer);
        answerInput = (EditText) mView.findViewById(R.id.input_answer);
    }

}
