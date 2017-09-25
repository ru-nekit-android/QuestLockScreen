package ru.nekit.android.qls.quest.mediator.colored;

import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.mediator.shared.answer.AbstractListableQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.resourceLibrary.IColoredVisualResourceItemList;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.types.VisualRepresentationalNumberSummandQuest;
import ru.nekit.android.qls.quest.types.model.ColorModel;
import ru.nekit.android.qls.quest.types.model.ContentAndBackgroundColoredModel;

public class ColoredVisualRepresentationQuestAlternativeAnswerMediator
        extends AbstractListableQuestAlternativeAnswerMediator<Pair<IColoredVisualResourceItemList, ContentAndBackgroundColoredModel>,
        ColoredVisualRepresentationQuestAdapter> {

    List<Pair<IColoredVisualResourceItemList, ContentAndBackgroundColoredModel>> mDataList;

    @NonNull
    @Override
    protected List<Pair<IColoredVisualResourceItemList, ContentAndBackgroundColoredModel>> getListData() {
        mDataList = new ArrayList<>();
        final int length = getQuest().getVisualRepresentationList().size();
        QuestResourceLibrary questResourceLibrary = mQuestContext.getQuestResourceLibrary();
        for (int i = 0; i < length; i++) {
            IColoredVisualResourceItemList visualResourceItem =
                    (IColoredVisualResourceItemList)
                            questResourceLibrary.getVisualResourceItem(getQuest().
                                    getVisualRepresentationList().get(i));
            ContentAndBackgroundColoredModel contentAndBackgroundColoredModel =
                    new ContentAndBackgroundColoredModel(ColorModel.getById(getQuest().leftNode[i]),
                            ColorModel.getById(getQuest().rightNode[i]));
            mDataList.add(new Pair<>(visualResourceItem, contentAndBackgroundColoredModel));
        }
        return mDataList;
    }

    private VisualRepresentationalNumberSummandQuest getQuest() {
        return (VisualRepresentationalNumberSummandQuest) mQuest;
    }

    @NonNull
    @Override
    protected ColoredVisualRepresentationQuestAdapter getListAdapter(List<Pair<IColoredVisualResourceItemList,
            ContentAndBackgroundColoredModel>> listData) {
        return new ColoredVisualRepresentationQuestAdapter(mQuestContext, listData, this);
    }

    @Override
    public void onStartQuest(boolean playAnimationOnDelayedStart) {
        super.onStartQuest(playAnimationOnDelayedStart);
        updateSizeInternal(true);
    }

    private void updateSizeInternal(@Size boolean useSizeDevider) {
        int size = Math.min(mRootContentContainer.getWidth(), mRootContentContainer.getHeight());
        int dataListLength = mListAdapter.getItemCount();
        int rowCount = (int) Math.ceil(dataListLength / (float) getColumnCount());
        mListAdapter.setSize(size / (useSizeDevider ? Math.max(getColumnCount(), rowCount) : 1));
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onRightAnswer() {
        super.onRightAnswer();
        Pair<IColoredVisualResourceItemList, ContentAndBackgroundColoredModel>
                item = mDataList.get(getQuest().unknownMemberIndex);
        mDataList.clear();
        mDataList.add(item);
        updateSizeInternal(false);
        mListView.requestLayout();
        return false;
    }
}
