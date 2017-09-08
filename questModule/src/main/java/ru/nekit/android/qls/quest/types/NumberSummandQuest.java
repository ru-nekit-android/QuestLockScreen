package ru.nekit.android.qls.quest.types;

import android.text.InputType;

import ru.nekit.android.qls.quest.math.MathematicalOperation;
import ru.nekit.android.qls.quest.math.MathematicalSignComparison;
import ru.nekit.android.qls.utils.MathUtils;

public class NumberSummandQuest extends BaseQuest {

    /*
                     /(<,=,>)\
                    /         \
                  []           []
            leftNode           rightNode

    Example: 1 + 2 = 3 - 4 -> [1, 2]=[3, -4]
    */

    public int[] leftNode, rightNode;
    public int unknownMemberIndex, unknownOperatorIndex;

    public int getLeftNodeSum() {
        return MathUtils.sum(leftNode);
    }

    public int getRightNodeSum() {
        return MathUtils.sum(rightNode);
    }

    public MathematicalSignComparison getSign() {
        int leftNodeSum = MathUtils.sum(leftNode);
        int rightNodeSum = MathUtils.sum(rightNode);
        if (leftNodeSum > rightNodeSum) {
            return MathematicalSignComparison.GREATER;
        } else if (leftNodeSum < rightNodeSum) {
            return MathematicalSignComparison.LESS;
        }
        return MathematicalSignComparison.EQUAL;
    }

    public int getUnknownMember() {
        return leftNode[unknownMemberIndex];
    }

    @Override
    public Object getAnswer() {
        int solution = 0;
        switch (getQuestionType()) {
            case SOLUTION:

                //rightNode is null!!!
                solution = getLeftNodeSum();

                break;

            case UNKNOWN_MEMBER:

                solution = Math.abs(getUnknownMember());

                break;
        }
        return solution;
    }

    public int getTypedAnswer() {
        return (int) getAnswer();
    }

    @Override
    public Class getAnswerClass() {
        switch (getQuestionType()) {
            case UNKNOWN_MEMBER:
            case SOLUTION:
                return Integer.class;
            case COMPARISON:
                return MathematicalSignComparison.class;
            case UNKNOWN_OPERATION:
                return MathematicalOperation.class;
        }
        return Void.class;
    }

    @Override
    public int getAnswerInputType() {
        return InputType.TYPE_CLASS_NUMBER;
    }
}