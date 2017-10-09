package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.answer.shared.QuestAnswerChecker;
import ru.nekit.android.qls.quest.base.Quest;
import ru.nekit.android.qls.quest.math.MathematicalOperation;
import ru.nekit.android.qls.quest.math.MathematicalSignComparison;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

import static ru.nekit.android.qls.quest.math.MathematicalOperation.ADDITION;
import static ru.nekit.android.qls.quest.math.MathematicalOperation.SUBTRACTION;

public class SimpleExampleAnswerChecker extends QuestAnswerChecker<Object> {

    @Override
    public boolean checkAlternativeInput(@NonNull Quest quest, @NonNull Object answer) {
        NumberSummandQuest numberSummandQuest = (NumberSummandQuest) quest;
        boolean result = false;
        switch (quest.getQuestionType()) {

            case COMPARISON:

                MathematicalSignComparison sign = (MathematicalSignComparison) answer;
                result = numberSummandQuest.getSign() == sign;

                break;

            case UNKNOWN_OPERATION:

                MathematicalOperation operation = ADDITION;
                MathematicalOperation answerOperation = (MathematicalOperation) answer;
                if (numberSummandQuest.leftNode[numberSummandQuest.unknownOperatorIndex + 1] < 0) {
                    operation = SUBTRACTION;
                }
                result = operation == answerOperation;

                break;

        }

        return result;
    }
}
