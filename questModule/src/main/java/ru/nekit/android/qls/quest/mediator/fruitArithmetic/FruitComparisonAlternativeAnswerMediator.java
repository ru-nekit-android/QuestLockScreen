package ru.nekit.android.qls.quest.mediator.fruitArithmetic;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.mediator.shared.answer.AbstractSquareImageQuestAlternativeAnswerMediator;
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
    public void onStartQuest(boolean playAnimationOnDelayedStart) {
        super.onStartQuest(playAnimationOnDelayedStart);
        int size = Math.min(mRootContentContainer.getWidth(), mRootContentContainer.getHeight());
        int dataListLength = mListAdapter.getItemCount();
        int rowCount = (int) Math.ceil(dataListLength / (float) getColumnCount());
        mListAdapter.setSize(size / Math.max(rowCount, getColumnCount()));
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateSize() {

    }
}
