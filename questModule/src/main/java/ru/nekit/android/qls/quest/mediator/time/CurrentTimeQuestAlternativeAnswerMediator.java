package ru.nekit.android.qls.quest.mediator.time;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import java.util.List;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestContextEvent;
import ru.nekit.android.qls.quest.types.CurrentTimeQuest;

public class CurrentTimeQuestAlternativeAnswerMediator extends TimeQuestAlternativeAnswerMediator
        implements EventBus.IEventHandler {

    @Override
    public void onCreateQuest(@NonNull QuestContext questContext, @NonNull ViewGroup rootContentContainer) {
        super.onCreateQuest(questContext, rootContentContainer);
        mQuestContext.getEventBus().handleEvents(this, QuestContextEvent.EVENT_TIC_TAC);
    }

    @Override
    public void detachView() {
        mQuestContext.getEventBus().stopHandleEvents(this);
        super.detachView();
    }

    @Override
    public void onEvent(@NonNull Intent intent) {
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return getClass().getName();
    }

    private CurrentTimeQuest getQuest() {
        return (CurrentTimeQuest) mQuest;
    }

    @NonNull
    @Override
    protected CurrentTimeAdapter getListAdapter(List<Integer> listData) {
        return new CurrentTimeAdapter(getQuest(), listData, this);
    }
}