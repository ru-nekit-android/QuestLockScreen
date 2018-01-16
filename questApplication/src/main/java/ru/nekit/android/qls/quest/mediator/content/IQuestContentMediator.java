package ru.nekit.android.qls.quest.mediator.content;

import android.widget.EditText;

import ru.nekit.android.qls.quest.mediator.IQuestMediator;

public interface IQuestContentMediator extends IQuestMediator {

    //TODO: used in only in a unused quest - remove?
    EditText getAnswerInput();

    boolean includeInLayout();

}