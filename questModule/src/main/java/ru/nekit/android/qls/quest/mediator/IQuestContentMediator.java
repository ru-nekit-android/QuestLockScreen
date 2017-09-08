package ru.nekit.android.qls.quest.mediator;

import android.widget.EditText;

public interface IQuestContentMediator extends IQuestMediator {

    EditText getAnswerInput();

    boolean includeInLayout();

}