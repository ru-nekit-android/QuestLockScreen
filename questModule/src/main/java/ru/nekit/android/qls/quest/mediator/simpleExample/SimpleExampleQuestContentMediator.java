package ru.nekit.android.qls.quest.mediator.simpleExample;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.formatter.IQuestTextContentFormatter;
import ru.nekit.android.qls.quest.formatter.TextQuestContentFormatter;
import ru.nekit.android.qls.quest.mediator.shared.content.AbstractQuestContentMediator;

import static android.view.View.GONE;

public class SimpleExampleQuestContentMediator extends AbstractQuestContentMediator {

    protected SimpleExampleQuestViewHolder mViewHolder;

    @NonNull
    protected IQuestTextContentFormatter createFormatter() {
        return new TextQuestContentFormatter();
    }

    @Override
    public void onCreateQuest(@NonNull QuestContext questContext, @NonNull ViewGroup rootContentContainer) {
        super.onCreateQuest(questContext, rootContentContainer);
        mViewHolder = new SimpleExampleQuestViewHolder(mQuestContext);
        IQuestTextContentFormatter formatter = createFormatter();
        String[] questStringList = formatter.format(mQuestContext, mQuest);
        switch (mQuest.getQuestionType()) {

            case SOLUTION:

                mViewHolder.rightSideView.setVisibility(GONE);
                mViewHolder.leftSideView.setText(questStringList[0]);
                mViewHolder.alternativeAnswerInput.setHint(formatter.getMissedCharacter());

                break;

            case UNKNOWN_MEMBER:

                mViewHolder.leftSideView.setText(questStringList[0]);
                mViewHolder.rightSideView.setText(questStringList[1]);
                mViewHolder.alternativeAnswerInput.setHint(formatter.getMissedCharacter());

                break;

            case COMPARISON:

                mViewHolder.alternativeAnswerInput.setVisibility(GONE);
                mViewHolder.rightSideView.setVisibility(GONE);
                mViewHolder.leftSideView.setText(questStringList[0]);

                break;

            case UNKNOWN_OPERATION:

                mViewHolder.alternativeAnswerInput.setVisibility(GONE);
                mViewHolder.rightSideView.setVisibility(GONE);
                mViewHolder.leftSideView.setText(questStringList[0]);

                break;

        }
    }

    @Override
    public void onStartQuest(boolean playAnimationOnDelayedStart) {
        super.onStartQuest(playAnimationOnDelayedStart);
    }

    @Override
    public View getView() {
        return mViewHolder.getView();
    }

    @Override
    public EditText getAnswerInput() {
        return mViewHolder.alternativeAnswerInput;
    }

    @Override
    public boolean includeInLayout() {
        return true;
    }

    @Override
    public void updateSize() {

    }

}
