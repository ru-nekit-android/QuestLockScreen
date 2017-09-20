package ru.nekit.android.qls.quest.mediator.colors;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.mediator.shared.answer.AbstractListableQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.types.model.ColorModel;
import ru.nekit.android.qls.quest.types.quest.NumberSummandQuest;

public class ColorsQuestAlternativeAnswerMediator
        extends AbstractListableQuestAlternativeAnswerMediator<ColorModel, ColorsAdapter> {

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
        mListAdapter.setSize(size / Math.max(2, rowCount));
        mListAdapter.notifyDataSetChanged();
    }

    @NonNull
    @Override
    protected List<ColorModel> getListData() {
        List<ColorModel> colorModelList = new ArrayList<>();
        for (int itemId : getQuest().leftNode) {
            colorModelList.add(ColorModel.getById(itemId));
        }
        return colorModelList;
    }

    private NumberSummandQuest getQuest() {
        return (NumberSummandQuest) mQuest;
    }

    @NonNull
    @Override
    protected ColorsAdapter getListAdapter(List<ColorModel> listData) {
        return new ColorsAdapter(mQuestContext, listData, this);
    }
}