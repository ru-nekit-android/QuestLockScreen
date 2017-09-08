package ru.nekit.android.qls.quest.mediator.metrics;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.formatter.IQuestTextContentFormatter;
import ru.nekit.android.qls.quest.formatter.MetricsQuestContentFormatter;
import ru.nekit.android.qls.quest.mediator.simpleExample.SimpleExampleQuestContentMediator;

public class MetricsQuestContentMediator extends SimpleExampleQuestContentMediator {

    @NonNull
    protected IQuestTextContentFormatter createFormatter() {
        return new MetricsQuestContentFormatter();
    }

    @Override
    public void init(@NonNull QuestContext context) {
        super.init(context);
        mViewHolder.alternativeAnswerInput.setVisibility(View.GONE);
    }

    @Override
    public EditText getAnswerInput() {
        return null;
    }

}
