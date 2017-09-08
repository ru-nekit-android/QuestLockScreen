package ru.nekit.android.qls.quest.mediator;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import ru.nekit.android.qls.quest.QuestContext;

class EmptyContentMediator extends AbstractQuestContentMediator {


    @Override
    public EditText getAnswerInput() {
        return null;
    }

    @Override
    public boolean includeInLayout() {
        return true;
    }

    @Override
    public void init(@NonNull QuestContext questContext) {

    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public void updateSize(int width, int height) {

    }
}