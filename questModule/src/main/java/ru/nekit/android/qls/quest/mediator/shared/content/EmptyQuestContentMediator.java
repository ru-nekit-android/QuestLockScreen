package ru.nekit.android.qls.quest.mediator.shared.content;

import android.view.View;
import android.widget.EditText;

public class EmptyQuestContentMediator extends AbstractQuestContentMediator {

    @Override
    public EditText getAnswerInput() {
        return null;
    }

    @Override
    public boolean includeInLayout() {
        return true;
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public void updateSize() {

    }
}