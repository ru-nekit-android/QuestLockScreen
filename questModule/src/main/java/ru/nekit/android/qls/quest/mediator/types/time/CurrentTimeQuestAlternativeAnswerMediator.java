package ru.nekit.android.qls.quest.mediator.types.time;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.List;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.quest.QuestContextEvent;
import ru.nekit.android.qls.quest.types.CurrentTimeQuest;

public class CurrentTimeQuestAlternativeAnswerMediator extends TimeQuestAlternativeAnswerMediator
        implements EventBus.IEventHandler {

    @Override
    public void onQuestStart(boolean delayedStart) {
        super.onQuestStart(delayedStart);
        mQuestContext.getEventBus().handleEvents(this, QuestContextEvent.EVENT_TIC_TAC);
    }

    @Override
    public void onQuestRestart() {
        super.onQuestRestart();
        mQuestContext.getEventBus().handleEvents(this, QuestContextEvent.EVENT_TIC_TAC);
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

    @Override
    public void onQuestPause() {

    }

    @Override
    public void onQuestResume() {

    }

    @Override
    public void onQuestStop() {
        mQuestContext.getEventBus().stopHandleEvents(this);
        super.onQuestStop();
    }
}