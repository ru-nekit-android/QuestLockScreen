package ru.nekit.android.qls.quest.mediator.types.metrics;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.answer.MetricsQuestAnswerVariantAdapter;
import ru.nekit.android.qls.quest.mediator.answer.ButtonsQuestAnswerMediator;

public class MetricsQuestAnswerMediator extends ButtonsQuestAnswerMediator {

    public MetricsQuestAnswerMediator() {
        super(new MetricsQuestAnswerVariantAdapter());
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
