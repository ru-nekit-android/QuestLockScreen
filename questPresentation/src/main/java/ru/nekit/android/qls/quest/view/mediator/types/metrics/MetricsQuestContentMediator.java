package ru.nekit.android.qls.quest.view.mediator.types.metrics;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.formatter.IQuestTextContentFormatter;
import ru.nekit.android.qls.quest.formatter.MetricsQuestContentFormatter;
import ru.nekit.android.qls.quest.view.mediator.types.simpleExample.SimpleExampleQuestContentMediator;

public class MetricsQuestContentMediator extends SimpleExampleQuestContentMediator {

    @NonNull
    protected IQuestTextContentFormatter createFormatter() {
        return new MetricsQuestContentFormatter();
    }

    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        super.onCreate(questContext);
        mViewHolder.alternativeAnswerInput.setVisibility(View.GONE);
    }

    @Override
    public EditText getAnswerInput() {
        return null;
    }

}
