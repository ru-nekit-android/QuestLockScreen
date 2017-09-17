package ru.nekit.android.qls.quest.mediator.metrics;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.answer.MetricsAlternativeAnswerVariantAdapter;
import ru.nekit.android.qls.quest.mediator.shared.answer.QuestAlternativeAnswerMediator;

public class MetricsQuestAlternativeAnswerMediator extends QuestAlternativeAnswerMediator {

    public MetricsQuestAlternativeAnswerMediator() {
        super(new MetricsAlternativeAnswerVariantAdapter());
    }

    @Override
    public void onCreateQuest(@NonNull QuestContext questContext, @NonNull ViewGroup rootContentContainer) {
        super.onCreateQuest(questContext, rootContentContainer);
        switch (mQuest.getQuestionType()) {

            case COMPARISON:

                fillButtonListWithAvailableVariants();

                break;

        }
    }
}
