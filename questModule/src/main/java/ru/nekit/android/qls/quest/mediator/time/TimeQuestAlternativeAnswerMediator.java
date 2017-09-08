package ru.nekit.android.qls.quest.mediator.time;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import ru.nekit.android.qls.quest.mediator.adapter.AbstractSelectableQuestAlternativeAnswerMediator;

public class TimeQuestAlternativeAnswerMediator extends AbstractSelectableQuestAlternativeAnswerMediator {

    @Override
    @NonNull
    public RecyclerView.Adapter createAdapter(@NonNull List list) {
        return new TimeAdapter(getDataList(), this);
    }

    @Override
    protected int getLayoutResId() {
        return 0;
    }


}