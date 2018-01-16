package ru.nekit.android.qls.quest.mediator.types.simpleExample;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.mediator.answer.ButtonsQuestAnswerMediator;

public class SimpleExampleQuestAnswerMediator extends ButtonsQuestAnswerMediator {

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
