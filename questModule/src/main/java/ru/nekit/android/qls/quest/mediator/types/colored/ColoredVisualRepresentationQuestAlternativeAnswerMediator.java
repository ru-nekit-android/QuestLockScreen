package ru.nekit.android.qls.quest.mediator.types.colored;

import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.base.PrimaryAndSecondaryColor;
import ru.nekit.android.qls.quest.mediator.answer.AbstractListableQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.resourceLibrary.IColoredVisualResource;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.types.VisualRepresentationalNumberSummandQuest;
import ru.nekit.android.qls.quest.window.RightAnswerWindow;

import static ru.nekit.android.qls.quest.QuestContext.QuestState.DELAYED_START;

public class ColoredVisualRepresentationQuestAlternativeAnswerMediator
        extends AbstractListableQuestAlternativeAnswerMediator<Pair<IColoredVisualResource, PrimaryAndSecondaryColor>,
        ColoredVisualRepresentationQuestAdapter> {

    private List<Pair<IColoredVisualResource, PrimaryAndSecondaryColor>> mDataList;

    @NonNull
    @Override
    protected List<Pair<IColoredVisualResource, PrimaryAndSecondaryColor>> getListData() {
        mDataList = new ArrayList<>();
        final int length = getQuest().getVisualRepresentationList().size();
        QuestResourceLibrary questResourceLibrary = mQuestContext.getQuestResourceLibrary();
        for (int i = 0; i < length; i++) {
            IColoredVisualResource coloredVisualResource = (IColoredVisualResource)
                    questResourceLibrary.getVisualResourceItem(getQuest().
                            getVisualRepresentationList().get(i));
            PrimaryAndSecondaryColor primaryAndSecondaryColor =
                    new PrimaryAndSecondaryColor(getQuest().leftNode[i], getQuest().rightNode[i]);
            mDataList.add(new Pair<>(coloredVisualResource, primaryAndSecondaryColor));
        }
        return mDataList;
    }

    private VisualRepresentationalNumberSummandQuest getQuest() {
        return (VisualRepresentationalNumberSummandQuest) mQuest;
    }

    @NonNull
    @Override
    protected ColoredVisualRepresentationQuestAdapter getListAdapter(List<Pair<IColoredVisualResource,
            PrimaryAndSecondaryColor>> listData) {
        return new ColoredVisualRepresentationQuestAdapter(mQuestContext, listData, this);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        mDataList.clear();
        mDataList = null;
    }

    @Override
    public void onStartQuest(boolean delayedStart) {
        updateListAdapter(true);
        super.onStartQuest(delayedStart);
    }

    @Override
    public void onCreateQuest() {
        if (mQuestContext.questHasState(DELAYED_START)) {
            updateListAdapter(true);
        }
        super.onCreateQuest();
    }

    @Override
    public void onRestartQuest() {
        super.onRestartQuest();
        updateListAdapter(true);
    }

    private void updateListAdapter(boolean useSizeDivider) {
        int size = Math.min(mRootContentContainer.getWidth(), mRootContentContainer.getHeight());
        int dataListLength = mListAdapter.getItemCount();
        int rowCount = (int) Math.ceil(dataListLength / (float) getColumnCount());
        mListAdapter.setSize(size / (useSizeDivider ? Math.max(getColumnCount(), rowCount) : 1));
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onRightAnswer() {
        super.onRightAnswer();
        Pair<IColoredVisualResource, PrimaryAndSecondaryColor> item =
                mDataList.get(getQuest().unknownMemberIndex);
        mDataList.clear();
        mDataList.add(item);
        updateListAdapter(false);
        mListView.requestLayout();
        new RightAnswerWindow.Builder(mQuestContext).
                setContent(R.layout.wc_right_answer_simple).
                setStyle(R.style.Window_RightAnswer_Simple).
                open();
        return false;
    }
}
