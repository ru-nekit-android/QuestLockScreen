package ru.nekit.android.qls.quest.mediator.types.direction;

import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.answer.common.AnswerType;
import ru.nekit.android.qls.quest.mediator.answer.QuestSwipeAnswerMediator;
import ru.nekit.android.qls.quest.window.AnswerWindow.Type;
import ru.nekit.android.qls.utils.AnimationUtils;


public class DirectionQuestAnswerMediator extends QuestSwipeAnswerMediator {

    private DirectionQuestViewHolder mViewHolder;

    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        super.onCreate(questContext);
        mViewHolder = new DirectionQuestViewHolder(questContext);
    }

    @NonNull
    @Override
    protected View getTargetView() {
        return mViewHolder.targetView;
    }

    @Override
    public View getView() {
        return mViewHolder.view;
    }

    @Override
    public boolean onAnswer(@NonNull AnswerType answerType) {
        if (answerType == AnswerType.RIGHT) {
            mQuestContext.openAnswerWindow(Type.RIGHT,
                    R.style.Window_RightAnswer_Simple,
                    R.layout.wc_right_answer_simple_content,
                    R.layout.wc_right_answer_tool_simple_content);
        } else if (answerType == AnswerType.WRONG) {
            AnimationUtils.shake(mViewHolder.targetView);
            mQuestContext.openAnswerWindow(Type.WRONG,
                    R.style.Window_WrongAnswer_Simple,
                    R.layout.wc_wrong_answer_simple_content,
                    R.layout.wc_wrong_answer_tool_simple_content);
        } else if (answerType == AnswerType.EMPTY) {
            AnimationUtils.shake(mViewHolder.targetView);
        }
        return false;
    }
}