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
import ru.nekit.android.qls.quest.resources.common.IColorfullVisualQuestResourceHolder;
import ru.nekit.android.qls.quest.resources.struct.PairColorStruct;
import ru.nekit.android.qls.quest.types.VisualRepresentationalNumberSummandQuest;
import ru.nekit.android.qls.quest.window.AnswerWindow;

public class ColoredVisualRepresentationQuestAnswerMediator
        extends AbstractListableQuestAnswerMediator<Pair<IColorfullVisualQuestResourceHolder, PairColorStruct>,
        ColoredVisualRepresentationQuestAdapter> {

    private List<Pair<IColorfullVisualQuestResourceHolder, PairColorStruct>> mDataList;

    @NonNull
    @Override
    protected List<Pair<IColorfullVisualQuestResourceHolder, PairColorStruct>> getListData() {
        mDataList = new ArrayList<>();
        final int length = getQuest().getVisualRepresentationList().size();
        QuestResourceLibrary questResourceLibrary = mQuestContext.getQuestResourceLibrary();
        for (int i = 0; i < length; i++) {
            IColorfullVisualQuestResourceHolder coloredVisualResource = (IColorfullVisualQuestResourceHolder)
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
    protected ColoredVisualRepresentationQuestAdapter getListAdapter(List<Pair<IColorfullVisualQuestResourceHolder,
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
            Pair<IColorfullVisualQuestResourceHolder, PairColorStruct> item =
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
