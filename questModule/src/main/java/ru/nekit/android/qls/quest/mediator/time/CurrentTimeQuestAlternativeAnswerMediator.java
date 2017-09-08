package ru.nekit.android.qls.quest.mediator.time;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestContextEvent;
import ru.nekit.android.qls.quest.mediator.adapter.AbstractSelectableQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.types.CurrentTimeQuest;

public class CurrentTimeQuestAlternativeAnswerMediator extends AbstractSelectableQuestAlternativeAnswerMediator
        implements EventBus.IEventHandler {

    private CurrentTimeAdapter mAdapter;

    @Override
    @NonNull
    public RecyclerView.Adapter createAdapter(@NonNull List dataList) {
        return mAdapter = new CurrentTimeAdapter((CurrentTimeQuest) mQuest, dataList, this);
    }

    @Override
    protected int getLayoutResId() {
        return 0;
    }

    @Override
    public void init(@NonNull QuestContext questContext) {
        super.init(questContext);
        mQuestContext.getEventBus().handleEvents(this, QuestContextEvent.EVENT_TIC_TAC);
    }

    @Override
    public void destroy() {
        mQuestContext.getEventBus().stopHandleEvents(this);
        super.destroy();
    }

    @Override
    public void onEvent(@NonNull Intent intent) {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return getClass().getName();
    }
}