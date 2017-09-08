package ru.nekit.android.qls.quest.mediator.metrics;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.answer.MetricsAlternativeAnswerVariantAdapter;
import ru.nekit.android.qls.quest.mediator.QuestAlternativeAnswerMediator;

public class MetricsQuestAlternativeAnswerMediator extends QuestAlternativeAnswerMediator {

    public MetricsQuestAlternativeAnswerMediator() {
        super(new MetricsAlternativeAnswerVariantAdapter());
    }

    @Override
    public void init(@NonNull QuestContext context) {
        super.init(context);
        switch (mQuest.getQuestionType()) {

            case COMPARISON:

                fillButtonListWithAvailableVariants();

                break;

        }
    }
}
