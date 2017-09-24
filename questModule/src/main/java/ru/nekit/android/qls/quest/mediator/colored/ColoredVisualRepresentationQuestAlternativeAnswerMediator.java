package ru.nekit.android.qls.quest.mediator.colored;

import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.mediator.shared.answer.AbstractListableQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.resourceLibrary.ITripleContentQuestVisualResourceItem;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.types.VisualRepresentationalNumberSummandQuest;
import ru.nekit.android.qls.quest.types.model.ColorModel;
import ru.nekit.android.qls.quest.types.model.ContentAndBackgroundColoredModel;

public class ColoredVisualRepresentationQuestAlternativeAnswerMediator
        extends AbstractListableQuestAlternativeAnswerMediator<Pair<ITripleContentQuestVisualResourceItem, ContentAndBackgroundColoredModel>,
        ColoredVisualRepresentationQuestAdapter> {

    List<Pair<ITripleContentQuestVisualResourceItem, ContentAndBackgroundColoredModel>> mDataList;

    @NonNull
    @Override
    protected List<Pair<ITripleContentQuestVisualResourceItem, ContentAndBackgroundColoredModel>> getListData() {
        mDataList = new ArrayList<>();
        final int length = getQuest().getVisualRepresentationList().size();
        QuestResourceLibrary questResourceLibrary = mQuestContext.getQuestResourceLibrary();
        for (int i = 0; i < length; i++) {
            ITripleContentQuestVisualResourceItem visualResourceItem =
                    (ITripleContentQuestVisualResourceItem)
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
    protected ColoredVisualRepresentationQuestAdapter getListAdapter(List<Pair<ITripleContentQuestVisualResourceItem,
            ContentAndBackgroundColoredModel>> listData) {
        return new ColoredVisualRepresentationQuestAdapter(mQuestContext, listData, this);
    }

    @Override
    public void onStartQuest(boolean playAnimationOnDelayedStart) {
        super.onStartQuest(playAnimationOnDelayedStart);
        updateSizeInternal(getColumnCount());
    }

    private void updateSizeInternal(int columnCount) {
        int size = Math.min(mRootContentContainer.getWidth(), mRootContentContainer.getHeight());
        int dataListLength = mListAdapter.getItemCount();
        int rowCount = (int) Math.ceil(dataListLength / (float) getColumnCount());
        mListAdapter.setSize(size / Math.max(columnCount, rowCount));
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAnswer(boolean isRight) {
        super.onAnswer(isRight);

        if (isRight) {
            Pair<ITripleContentQuestVisualResourceItem, ContentAndBackgroundColoredModel>
                    item = mDataList.get(getQuest().unknownMemberIndex), actualItem;
            actualItem = item;

            for (int i = 0; i < mDataList.size(); i++) {
                mListAdapter.notifyItemRemoved(i);
            }
            mDataList.clear();
            mDataList.add(item);
            mListAdapter.notifyItemInserted(0);
            updateSizeInternal(1);
        }
    }
}
