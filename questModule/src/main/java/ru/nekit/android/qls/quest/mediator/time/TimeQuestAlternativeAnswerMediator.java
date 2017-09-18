package ru.nekit.android.qls.quest.mediator.time;

import android.support.annotation.NonNull;

import java.util.List;

import ru.nekit.android.qls.quest.mediator.shared.answer.AbstractListableQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

public class TimeQuestAlternativeAnswerMediator
        extends AbstractListableQuestAlternativeAnswerMediator<Integer, TimeAdapter> {

    @Override
    public void onStartQuest(boolean playAnimationOnDelayedStart) {
        super.onStartQuest(playAnimationOnDelayedStart);
        int size;
        int dataListLength = mListAdapter.getItemCount();
        int rowCount = dataListLength / getColumnCount();
        if (rowCount > getColumnCount()) {
            size = Math.max(mRootContentContainer.getWidth(), mRootContentContainer.getHeight());
        } else {
            size = Math.min(mRootContentContainer.getWidth(), mRootContentContainer.getHeight());
        }
        mListAdapter.setSize(size / rowCount);
        mListAdapter.notifyDataSetChanged();
    }

    @NonNull
    @Override
    protected List<Integer> getListData() {
        return ((NumberSummandQuest) mQuest).getLeftNodeAsList();
    }

    @NonNull
    @Override
    protected TimeAdapter getListAdapter(List<Integer> listData) {
        return new TimeAdapter(listData, this);
    }
}