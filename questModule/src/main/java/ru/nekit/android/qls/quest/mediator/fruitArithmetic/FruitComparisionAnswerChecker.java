package ru.nekit.android.qls.quest.mediator.fruitArithmetic;

import android.support.annotation.NonNull;
import android.util.SparseIntArray;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.answer.IAnswerChecker;
import ru.nekit.android.qls.quest.types.FruitArithmeticQuest;
import ru.nekit.android.qls.quest.types.IComparisionTypeQuest;

public class FruitComparisionAnswerChecker implements IAnswerChecker {

    @Override
    public boolean checkAlternativeInput(@NonNull IQuest quest, @NonNull Object answer) {
        SparseIntArray map = new SparseIntArray();
        FruitArithmeticQuest fruitArithmeticQuest = (FruitArithmeticQuest) quest;
        final int length = fruitArithmeticQuest.leftNode.length;
        int i = 0;
        for (; i < length; i++) {
            int key = fruitArithmeticQuest.leftNode[i];
            map.append(key, map.get(key, 0) + 1);
        }
        boolean isMax = fruitArithmeticQuest.getComparisonType() == IComparisionTypeQuest.COMPARISON_TYPE_MAX;
        int value = isMax ? 0 : Integer.MAX_VALUE;
        for (i = 0; i < map.size(); i++) {
            int item = map.valueAt(i);
            value = isMax ? Math.max(value, item) : Math.min(value, item);
        }
        return map.get((int) answer) == value;
    }

    @Override
    public boolean checkStringInputFormat(@NonNull IQuest quest, @NonNull String value) {
        return false;
    }

    @Override
    public boolean checkStringInput(@NonNull IQuest quest, @NonNull String value) {
        return false;
    }
}
