package ru.nekit.android.qls.quest.mediator.types.direction;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.answer.common.AnswerType;
import ru.nekit.android.qls.quest.mediator.answer.QuestSwipeAnswerMediator;
import ru.nekit.android.qls.quest.window.AnswerWindow;
import ru.nekit.android.qls.utils.AnimationUtils;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class DirectionQuestAnswerMediator extends QuestSwipeAnswerMediator {

    private DirectionQuestViewHolder mViewHolder;
    private Handler mHideWrongMessageExecutor;
    private Runnable mHideWrongMessage = new Runnable() {
        @Override
        public void run() {
            mViewHolder.wrongMessage.setVisibility(INVISIBLE);
        }
    };

    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        super.onCreate(questContext);
        mViewHolder = new DirectionQuestViewHolder(questContext);
    }

    @Override
    public void detachView() {
        if (mHideWrongMessageExecutor != null) {
            mHideWrongMessageExecutor.removeCallbacks(mHideWrongMessage);
        }
        super.detachView();
    }

    @Override
    public void onQuestResume() {
        super.onQuestResume();
        mViewHolder.wrongMessage.setVisibility(INVISIBLE);
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
            mViewHolder.rightMessage.setVisibility(VISIBLE);
            mViewHolder.rightMessage.setText(R.string.quest_direction_right_answer_message);
            new AnswerWindow.Builder(mQuestContext, AnswerWindow.Variant.RIGHT).
                    setStyle(R.style.Window_RightAnswer_Simple).
                    setToolContent(R.layout.wc_right_answer_tool).
                    create().
                    open();
        } else if (answerType == AnswerType.WRONG) {
            mViewHolder.wrongMessage.setVisibility(VISIBLE);
            mViewHolder.wrongMessage.setText(R.string.quest_direction_wrong_answer_message);
            AnimationUtils.shake(mViewHolder.targetView);
            new AnswerWindow.Builder(mQuestContext, AnswerWindow.Variant.WRONG).
                    setStyle(R.style.Window_WrongAnswer_Simple).
                    setToolContent(R.layout.wc_wrong_answer_tool).
                    create().
                    open();
        } else if (answerType == AnswerType.EMPTY) {
            mViewHolder.wrongMessage.setVisibility(VISIBLE);
            mViewHolder.wrongMessage.setText(R.string.quest_direction_empty_answer_message);
            AnimationUtils.shake(mViewHolder.targetView);
            if (mHideWrongMessageExecutor == null) {
                mHideWrongMessageExecutor = new Handler();
            } else {
                mHideWrongMessageExecutor.removeCallbacks(mHideWrongMessage);
            }
            mHideWrongMessageExecutor.postDelayed(mHideWrongMessage, 2000);
        }
        return false;
    }
}