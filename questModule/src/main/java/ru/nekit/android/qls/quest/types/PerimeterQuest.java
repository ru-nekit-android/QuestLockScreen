package ru.nekit.android.qls.quest.types;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.common.Quest;

public class PerimeterQuest extends NumberSummandQuest {

    public PerimeterQuest(@NonNull Quest quest) {
        setQuestType(QuestType.PERIMETER);
        setQuestionType(quest.getQuestionType());
        NumberSummandQuest inQuest = (NumberSummandQuest) quest;
        int[] values = new int[2];
        values[0] = inQuest.leftNode[0];
        values[1] = inQuest.leftNode[1];
        if (values[1] == 0) {
            values[1] = values[0];
        }
        leftNode = values;
        unknownMemberIndex = inQuest.unknownMemberIndex;
        unknownOperatorIndex = inQuest.unknownOperatorIndex;
        if (quest.getAvailableAnswerVariants() != null) {
            setAvailableAnswerVariants(quest.getAvailableAnswerVariants());
        }
    }

    public String getFigureName(@NonNull Context context) {
        return context.getString(isSquare() ? R.string.square_figure_name : R.string.rectangle_figure_name);
    }

    public boolean isSquare() {
        return getASideSize() == getBSideSize();
    }

    public int getASideSize() {
        return leftNode[0];
    }

    public int getBSideSize() {
        return leftNode[1];
    }

    public int getPerimeter() {
        return (getASideSize() + getBSideSize()) * 2;
    }

    @Override
    public Integer getAnswer() {
        switch (getQuestionType()) {

            case SOLUTION:

                return getPerimeter();

        }
        return super.getAnswer();
    }
}