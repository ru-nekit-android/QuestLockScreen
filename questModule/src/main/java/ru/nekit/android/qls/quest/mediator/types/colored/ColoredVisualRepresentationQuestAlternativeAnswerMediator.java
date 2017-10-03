package ru.nekit.android.qls.quest.mediator.types.colored;

import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.base.PrimaryAndSecondaryColorModelHolder;
import ru.nekit.android.qls.quest.mediator.answer.AbstractListableQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.model.ColorModel;
import ru.nekit.android.qls.quest.resourceLibrary.IColoredVisualResourceModelList;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.types.VisualRepresentationalNumberSummandQuest;

public class ColoredVisualRepresentationQuestAlternativeAnswerMediator
        extends AbstractListableQuestAlternativeAnswerMediator<Pair<IColoredVisualResourceModelList, PrimaryAndSecondaryColorModelHolder>,
        ColoredVisualRepresentationQuestAdapter> {

    private List<Pair<IColoredVisualResourceModelList, PrimaryAndSecondaryColorModelHolder>> mDataList;

    @NonNull
    @Override
    protected List<Pair<IColoredVisualResourceModelList, PrimaryAndSecondaryColorModelHolder>> getListData() {
        mDataList = new ArrayList<>();
        final int length = getQuest().getVisualRepresentationList().size();
        QuestResourceLibrary questResourceLibrary = mQuestContext.getQuestResourceLibrary();
        for (int i = 0; i < length; i++) {
            IColoredVisualResourceModelList visualResourceItem =
                    (IColoredVisualResourceModelList)
                            questResourceLibrary.getVisualResourceItem(getQuest().
                                    getVisualRepresentationList().get(i));
            PrimaryAndSecondaryColorModelHolder contentAndBackgroundColoredModel =
                    new PrimaryAndSecondaryColorModelHolder(ColorModel.getById(getQuest().leftNode[i]),
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
    protected ColoredVisualRepresentationQuestAdapter getListAdapter(List<Pair<IColoredVisualResourceModelList,
            PrimaryAndSecondaryColorModelHolder>> listData) {
        return new ColoredVisualRepresentationQuestAdapter(mQuestContext, listData, this);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        mDataList.clear();
        mDataList = null;
    }

    @Override
    public void onStartQuest(boolean playAnimationOnDelayedStart) {
        super.onStartQuest(playAnimationOnDelayedStart);
        updateListAdapter(true);
    }

    @Override
    public void onRestartQuest() {
        super.onRestartQuest();
        updateListAdapter(true);
    }

    private void updateListAdapter(@Size boolean useSizeDivider) {
        int size = Math.min(mRootContentContainer.getWidth(), mRootContentContainer.getHeight());
        int dataListLength = mListAdapter.getItemCount();
        int rowCount = (int) Math.ceil(dataListLength / (float) getColumnCount());
        mListAdapter.setSize(size / (useSizeDivider ? Math.max(getColumnCount(), rowCount) : 1));
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onRightAnswer() {
        super.onRightAnswer();
        Pair<IColoredVisualResourceModelList, PrimaryAndSecondaryColorModelHolder>
                item = mDataList.get(getQuest().unknownMemberIndex);
        mDataList.clear();
        mDataList.add(item);
        updateListAdapter(false);
        mListView.requestLayout();
        return false;
    }
}
