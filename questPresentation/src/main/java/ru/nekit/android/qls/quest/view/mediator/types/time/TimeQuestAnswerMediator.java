package ru.nekit.android.qls.quest.view.mediator.types.time;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import java.util.List;

import ru.nekit.android.qls.quest.types.NumberSummandQuest;
import ru.nekit.android.qls.quest.view.mediator.answer.AbstractListableQuestAnswerMediator;

public class TimeQuestAnswerMediator
        extends AbstractListableQuestAnswerMediator<Integer, TimeAdapter> {


    @Override
    public void onQuestPlay(boolean delayedPlay) {
        updateListAdapter();
        super.onQuestPlay(delayedPlay);
    }

    @Override
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        super.onQuestAttach(rootContentContainer);
        updateListAdapter();
    }

    private void updateListAdapter() {
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