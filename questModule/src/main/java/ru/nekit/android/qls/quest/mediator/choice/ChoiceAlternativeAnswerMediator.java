package ru.nekit.android.qls.quest.mediator.choice;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.mediator.shared.answer.AbstractSquareImageQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.types.quest.NumberSummandQuest;

public class ChoiceAlternativeAnswerMediator
        extends AbstractSquareImageQuestAlternativeAnswerMediator {

    @Override
    public void onStartQuest(boolean playAnimationOnDelayedStart) {
        super.onStartQuest(playAnimationOnDelayedStart);
        int size = Math.min(mRootContentContainer.getWidth(), mRootContentContainer.getHeight());
        int dataListLength = mListAdapter.getItemCount();
        int rowCount = (int) Math.ceil(dataListLength / (float) getColumnCount());
        mListAdapter.setSize(size / rowCount);
        mListAdapter.notifyDataSetChanged();
    }

    private NumberSummandQuest getQuest() {
        return (NumberSummandQuest) mQuest;
    }

    @Override
    @LayoutRes
    protected int getListItemLayoutResId() {
        return R.layout.ill_choice;
    }

    @NonNull
    @Override
    protected List<Integer> getListData() {
        return getQuest().getLeftNodeAsList();
    }

}