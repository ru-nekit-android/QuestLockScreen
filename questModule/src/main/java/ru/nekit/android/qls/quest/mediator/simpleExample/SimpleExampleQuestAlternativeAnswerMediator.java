package ru.nekit.android.qls.quest.mediator.simpleExample;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.mediator.QuestAlternativeAnswerMediator;

/**
 * Created by nekit on 18.01.17.
 */

public class SimpleExampleQuestAlternativeAnswerMediator extends QuestAlternativeAnswerMediator {

    @Override
    public void init(@NonNull QuestContext context) {
        super.init(context);
        switch (mQuest.getQuestionType()) {

            case UNKNOWN_OPERATION:
            case COMPARISON:

                fillButtonListWithAvailableVariants();

                break;

        }
    }
}
