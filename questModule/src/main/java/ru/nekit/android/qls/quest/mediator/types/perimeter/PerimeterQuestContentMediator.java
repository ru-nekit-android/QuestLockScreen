package ru.nekit.android.qls.quest.mediator.types.perimeter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.mediator.content.AbstractQuestContentMediator;
import ru.nekit.android.qls.quest.types.PerimeterQuest;

import static android.view.View.GONE;

public class PerimeterQuestContentMediator extends AbstractQuestContentMediator {

    private PerimeterQuestViewHolder mViewHolder;

    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        super.onCreate(questContext);
        PerimeterQuest quest = (PerimeterQuest) mQuest;
        mViewHolder = new PerimeterQuestViewHolder(mQuestContext);
        ViewGroup.LayoutParams figureViewLayoutParams = mViewHolder.figureView.getLayoutParams();
        if (quest.isSquare()) {
            figureViewLayoutParams.width = 100;
            figureViewLayoutParams.height = 100;
            if (quest.getQuestionType() == QuestionType.SOLUTION) {
                mViewHolder.bFigureSideLabel.setVisibility(GONE);
            }
        } else {
            if (quest.getASideSize() > quest.getBSideSize()) {
                figureViewLayoutParams.width = 200;
                figureViewLayoutParams.height = 100;
            } else {
                figureViewLayoutParams.width = 100;
                figureViewLayoutParams.height = 200;
            }
        }
        if (quest.getQuestionType() == QuestionType.SOLUTION) {
            mViewHolder.aFigureSideLabel.setText(String.valueOf(quest.getASideSize()));
            mViewHolder.bFigureSideLabel.setText(String.valueOf(quest.getBSideSize()));
        } else if (quest.getQuestionType() == QuestionType.UNKNOWN_MEMBER) {
            String unknownSideString = mQuestContext.getString(R.string.unknown_side);
            if (quest.isSquare()) {
                mViewHolder.aFigureSideLabel.setText(unknownSideString);
            } else {
                if (quest.unknownMemberIndex == 0) {
                    mViewHolder.aFigureSideLabel.setText(unknownSideString);
                    mViewHolder.bFigureSideLabel.setText(String.valueOf(quest.getBSideSize()));
                } else if (quest.unknownMemberIndex == 1) {
                    mViewHolder.bFigureSideLabel.setText(unknownSideString);
                    mViewHolder.aFigureSideLabel.setText(String.valueOf(quest.getASideSize()));
                }
            }
        }
    }

    @Override
    public View getView() {
        return mViewHolder.getView();
    }

    @Override
    public EditText getAnswerInput() {
        return null;
    }

    @Override
    public boolean includeInLayout() {
        return true;
    }

    @Override
    public void updateSize() {
    }
}