package ru.nekit.android.qls.quest.mediator.types.simpleExample;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.mediator.answer.QuestAlternativeAnswerMediator;

public class SimpleExampleQuestAlternativeAnswerMediator extends QuestAlternativeAnswerMediator {

    @Override
    public void activate(@NonNull QuestContext questContext, @NonNull ViewGroup rootContentContainer) {
        super.activate(questContext, rootContentContainer);
        switch (mQuest.getQuestionType()) {

            case UNKNOWN_OPERATION:
            case COMPARISON:

                fillButtonListWithAvailableVariants();

                break;

        }
    }
}
