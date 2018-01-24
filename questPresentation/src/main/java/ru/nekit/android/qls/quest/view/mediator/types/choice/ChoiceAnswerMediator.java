package ru.nekit.android.qls.quest.view.mediator.types.choice;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;
import ru.nekit.android.qls.quest.view.mediator.answer.AbstractSquareImageQuestAnswerMediator;

public class ChoiceAnswerMediator
        extends AbstractSquareImageQuestAnswerMediator {

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
    public void onQuestPlay(boolean delayedPlay) {
        if (delayedPlay) {
            updateListAdapter();
        }
        super.onQuestPlay(delayedPlay);
    }

    private void updateListAdapter() {
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