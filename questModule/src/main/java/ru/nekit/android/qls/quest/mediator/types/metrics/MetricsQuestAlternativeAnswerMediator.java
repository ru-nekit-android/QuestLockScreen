package ru.nekit.android.qls.quest.mediator.types.metrics;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.answer.MetricsAlternativeAnswerVariantAdapter;
import ru.nekit.android.qls.quest.mediator.answer.QuestAlternativeAnswerMediator;

public class MetricsQuestAlternativeAnswerMediator extends QuestAlternativeAnswerMediator {

    public MetricsQuestAlternativeAnswerMediator() {
        super(new MetricsAlternativeAnswerVariantAdapter());
    }

    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        super.onCreate(questContext);
        switch (mQuest.getQuestionType()) {

            case COMPARISON:

                fillButtonListWithAvailableVariants();

                break;

        }
    }
}
