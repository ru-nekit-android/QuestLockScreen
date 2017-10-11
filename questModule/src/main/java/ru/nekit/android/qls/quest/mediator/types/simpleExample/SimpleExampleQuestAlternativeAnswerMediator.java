package ru.nekit.android.qls.quest.mediator.types.simpleExample;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.mediator.answer.QuestAlternativeAnswerMediator;

public class SimpleExampleQuestAlternativeAnswerMediator extends QuestAlternativeAnswerMediator {

    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        super.onCreate(questContext);
        switch (mQuest.getQuestionType()) {

            case UNKNOWN_OPERATION:
            case COMPARISON:

                fillButtonListWithAvailableVariants();

                break;

        }
    }
}
