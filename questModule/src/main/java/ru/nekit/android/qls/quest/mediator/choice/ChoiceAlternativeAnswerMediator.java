package ru.nekit.android.qls.quest.mediator.choice;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.mediator.adapter.AbstractSelectableQuestAlternativeAnswerMediator;

public class ChoiceAlternativeAnswerMediator extends AbstractSelectableQuestAlternativeAnswerMediator {

    @Override
    protected int getLayoutResId() {
        return R.layout.ill_choice;
    }
}
