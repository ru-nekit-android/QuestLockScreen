package ru.nekit.android.qls.quest.mediator.types.colored;

import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.answer.common.AnswerType;
import ru.nekit.android.qls.quest.mediator.answer.AbstractListableQuestAnswerMediator;
import ru.nekit.android.qls.quest.resources.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resources.common.IColorfullVisualResourceHolder;
import ru.nekit.android.qls.quest.resources.struct.PairColorStruct;
import ru.nekit.android.qls.quest.types.VisualRepresentationalNumberSummandQuest;
import ru.nekit.android.qls.window.AnswerWindow;

public class ColoredVisualRepresentationQuestAnswerMediator
        extends AbstractListableQuestAnswerMediator<Pair<IColorfullVisualResourceHolder, PairColorStruct>,
        ColoredVisualRepresentationQuestAdapter> {

    private List<Pair<IColorfullVisualResourceHolder, PairColorStruct>> mDataList;

    @NonNull
    @Override
    protected List<Pair<IColorfullVisualResourceHolder, PairColorStruct>> getListData() {
        mDataList = new ArrayList<>();
        final int length = getQuest().getVisualRepresentationList().size();
        QuestResourceLibrary questResourceLibrary = mQuestContext.getQuestResourceLibrary();
        for (int i = 0; i < length; i++) {
            IColorfullVisualResourceHolder coloredVisualResource = (IColorfullVisualResourceHolder)
                    questResourceLibrary.getVisualQuestResource(getQuest().
                            getVisualRepresentationList().get(i));
            PairColorStruct pairColorStruct =
                    new PairColorStruct(getQuest().leftNode[i], getQuest().rightNode[i]);
            mDataList.add(new Pair<>(coloredVisualResource, pairColorStruct));
        }
        return mDataList;
    }

    private VisualRepresentationalNumberSummandQuest getQuest() {
        return (VisualRepresentationalNumberSummandQuest) mQuest;
    }

    @NonNull
    @Override
    protected ColoredVisualRepresentationQuestAdapter getListAdapter(List<Pair<IColorfullVisualResourceHolder,
            PairColorStruct>> listData) {
        return new ColoredVisualRepresentationQuestAdapter(mQuestContext, listData, this);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        mDataList.clear();
        mDataList = null;
    }

    @Override
    public void onQuestPlay(boolean delayedPlay) {
        updateListAdapter(true);
        super.onQuestPlay(delayedPlay);
    }

    @Override
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        super.onQuestAttach(rootContentContainer);
        updateListAdapter(true);
    }

    @Override
    public void onQuestReplay() {
        super.onQuestReplay();
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
    public boolean onAnswer(@NonNull AnswerType answerType) {
        if (answerType == AnswerType.RIGHT) {
            Pair<IColorfullVisualResourceHolder, PairColorStruct> item =
                    mDataList.get(getQuest().unknownMemberIndex);
            mDataList.clear();
            mDataList.add(item);
            updateListAdapter(false);
            mListView.requestLayout();
            mQuestContext.openAnswerWindow(AnswerWindow.Type.RIGHT,
                    R.style.Window_RightAnswer_Simple,
                    R.layout.wc_right_answer_simple_content,
                    R.layout.wc_right_answer_tool_simple_content);
            return false;
        }
        return super.onAnswer(answerType);
    }
}
