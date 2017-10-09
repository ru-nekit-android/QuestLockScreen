package ru.nekit.android.qls.quest.formatter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.base.Quest;
import ru.nekit.android.qls.quest.math.MathematicalSignComparison;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

import static ru.nekit.android.qls.quest.math.MathematicalOperation.ADDITION;
import static ru.nekit.android.qls.quest.math.MathematicalOperation.SUBTRACTION;

public class TextQuestContentFormatter implements IQuestTextContentFormatter {

    @NonNull
    private String missingCharacter;

    @NonNull
    @Override
    public String getMissedCharacter() {
        return missingCharacter;
    }

    private String getSignForValue(int value) {
        return (value > 0 ? ADDITION : SUBTRACTION).toString();
    }

    private String getValue(int value) {
        return String.valueOf(Math.abs(value));
    }

    public String[] format(@NonNull Context context, @NonNull Quest quest) {
        NumberSummandQuest numberSummandQuest = (NumberSummandQuest) quest;
        missingCharacter = context.getString(R.string.missed_character);
        String joinCharacter = context.getString(R.string.join_character);
        String[] resultStringArray = new String[2];
        List<String> stringList = new ArrayList<>();

        int index;
        switch (numberSummandQuest.getQuestionType()) {

            case COMPARISON:

                for (int value : numberSummandQuest.leftNode) {
                    stringList.add(getSignForValue(value));
                    stringList.add(getValue(value));
                }
                stringList.remove(0);
                stringList.add(missingCharacter);
                index = stringList.size();
                for (int value : numberSummandQuest.rightNode) {
                    stringList.add(getSignForValue(value));
                    stringList.add(getValue(value));
                }
                stringList.remove(index);
                resultStringArray[0] = TextUtils.join(joinCharacter, stringList);

                break;

            case SOLUTION:

                for (int value : numberSummandQuest.leftNode) {
                    stringList.add(getSignForValue(value));
                    stringList.add(getValue(value));
                }
                stringList.remove(0);
                stringList.add(MathematicalSignComparison.EQUAL.toString());
                stringList.add("");
                resultStringArray[0] = TextUtils.join(joinCharacter, stringList);

                break;

            case UNKNOWN_OPERATION:

                for (int value : numberSummandQuest.leftNode) {
                    stringList.add(getSignForValue(value));
                    stringList.add(getValue(value));
                }
                stringList.remove(0);
                stringList.set(numberSummandQuest.unknownOperatorIndex * 2 + 1, missingCharacter);
                stringList.add(MathematicalSignComparison.EQUAL.toString());
                int length = stringList.size();
                for (int value : numberSummandQuest.rightNode) {
                    stringList.add(getSignForValue(value));
                    stringList.add(getValue(value));
                }
                stringList.remove(length);
                resultStringArray[0] = TextUtils.join(joinCharacter, stringList);

                break;

            case UNKNOWN_MEMBER:

                for (int i = 0; i <= numberSummandQuest.unknownMemberIndex; i++) {
                    int value = numberSummandQuest.leftNode[i];
                    stringList.add(getSignForValue(value));
                    stringList.add(getValue(value));
                }
                if (stringList.size() > 1) {
                    stringList.remove(stringList.size() - 1);
                    stringList.remove(0);
                }
                stringList.add("");
                resultStringArray[0] = TextUtils.join(joinCharacter, stringList);
                stringList.clear();
                stringList.add("");
                for (int i = numberSummandQuest.unknownMemberIndex + 1;
                     i < numberSummandQuest.leftNode.length; i++) {
                    int value = numberSummandQuest.leftNode[i];
                    stringList.add(getSignForValue(value));
                    stringList.add(getValue(value));
                }
                stringList.add(MathematicalSignComparison.EQUAL.toString());
                index = stringList.size();
                for (int value : numberSummandQuest.rightNode) {
                    stringList.add(getSignForValue(value));
                    stringList.add(getValue(value));
                }
                stringList.remove(index);
                resultStringArray[1] = TextUtils.join(joinCharacter, stringList);

                break;
        }
        return resultStringArray;
    }

}
