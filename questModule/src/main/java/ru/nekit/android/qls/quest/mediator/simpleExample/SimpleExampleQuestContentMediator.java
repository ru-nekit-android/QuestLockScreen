package ru.nekit.android.qls.quest.mediator.simpleExample;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.formatter.IQuestTextContentFormatter;
import ru.nekit.android.qls.quest.formatter.TextQuestContentFormatter;
import ru.nekit.android.qls.quest.mediator.AbstractQuestContentMediator;

import static android.view.View.GONE;

public class SimpleExampleQuestContentMediator extends AbstractQuestContentMediator {

    protected SimpleExampleQuestViewHolder mViewHolder;

    @NonNull
    protected IQuestTextContentFormatter createFormatter() {
        return new TextQuestContentFormatter();
    }

    @Override
    public void init(@NonNull QuestContext questContext) {
        IQuest quest = questContext.getQuest();
        mViewHolder = new SimpleExampleQuestViewHolder(questContext);
        IQuestTextContentFormatter formatter = createFormatter();
        String[] questStringList = formatter.format(questContext, quest);
        switch (quest.getQuestionType()) {

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
    public void updateSize(int width, int height) {

    }

}
