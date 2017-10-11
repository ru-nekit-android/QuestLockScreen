package ru.nekit.android.qls.quest.mediator.types.fruitArithmetic;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.mediator.answer.AbstractSquareImageQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

public class FruitComparisonAlternativeAnswerMediator extends
        AbstractSquareImageQuestAlternativeAnswerMediator {

    @Override
    protected int getColumnCount() {
        int i = 1;
        while (i * i < getQuest().leftNode.length) {
            i++;
        }
        return i;
    }

    @NonNull
    @Override
    protected List<Integer> getListData() {
        return getQuest().getLeftNodeAsList();
    }

    private NumberSummandQuest getQuest() {
        return ((NumberSummandQuest) mQuest);
    }

    @LayoutRes
    protected int getListItemLayoutResId() {
        return R.layout.ill_fruit;
    }

    @Override
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        super.onQuestAttach(rootContentContainer);
        updateListAdapter();
    }

    @Override
    public void onQuestStart(boolean delayedPlay) {
        super.onQuestStart(delayedPlay);
        if (!delayedPlay) {
            updateListAdapter();
        }
    }

    @Override
    public void onQuestPlay(final boolean delayedPlay) {
        if (delayedPlay) {
            updateListAdapter();
        }
        super.onQuestPlay(delayedPlay);
    }

    private void updateListAdapter() {
        int size = Math.min(mRootContentContainer.getWidth(), mRootContentContainer.getHeight());
        int dataListLength = mListAdapter.getItemCount();
        int rowCount = (int) Math.ceil(dataListLength / (float) getColumnCount());
        mListAdapter.setSize(size / Math.max(rowCount, getColumnCount()));
        mListAdapter.notifyDataSetChanged();
    }
}
