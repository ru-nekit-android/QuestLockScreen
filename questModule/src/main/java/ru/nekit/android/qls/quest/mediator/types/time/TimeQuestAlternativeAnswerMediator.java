package ru.nekit.android.qls.quest.mediator.types.time;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import java.util.List;

import ru.nekit.android.qls.quest.mediator.answer.AbstractListableQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

import static ru.nekit.android.qls.quest.QuestContext.QuestState.DELAYED_START;

public class TimeQuestAlternativeAnswerMediator
        extends AbstractListableQuestAlternativeAnswerMediator<Integer, TimeAdapter> {


    @Override
    public void onQuestStart(boolean delayedStart) {
        updateListAdapter();
        super.onQuestStart(delayedStart);
    }

    @Override
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        if (mQuestContext.questHasState(DELAYED_START)) {
            updateListAdapter();
        }
        super.onQuestAttach(rootContentContainer);
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