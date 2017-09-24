package ru.nekit.android.qls.quest.formatter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.types.MetricsQuest;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

public class MetricsQuestContentFormatter implements IQuestTextContentFormatter {

    private static int[] METRICS_UNIT_SHORT = new int[]{R.string.meter_short, R.string.decimeter_short, R.string.centimeter_short};
    private static String missingCharacter;
    private Context mContext;

    public MetricsQuestContentFormatter() {
    }

    private String getMetricsUnit(int index) {
        return mContext.getString(METRICS_UNIT_SHORT[index]);
    }

    private List<String> toStringList(int[] array) {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < MetricsQuest.METRICS_ITEM_COUNT; i++) {
            int value = array[i];
            if (value != 0) {
                stringList.add(String.valueOf(value));
                stringList.add(getMetricsUnit(i));
            }
        }
        return stringList;
    }

    public String[] format(@NonNull Context context, @NonNull IQuest quest) {
        mContext = context;
        NumberSummandQuest numberSummandQuest = (NumberSummandQuest) quest;
        missingCharacter = context.getString(R.string.missed_character);
        String joinCharacter = context.getString(R.string.join_character);
        List<String> stringList = new ArrayList<>();
        switch (numberSummandQuest.getQuestionType()) {

            case COMPARISON:

                stringList.addAll(toStringList(numberSummandQuest.leftNode));
                stringList.add(missingCharacter);
                stringList.addAll(toStringList(numberSummandQuest.rightNode));

                break;

            case SOLUTION:

                stringList.addAll(toStringList(numberSummandQuest.leftNode));

                break;
        }
        return new String[]{TextUtils.join(joinCharacter, stringList)};
    }

    @Override
    public String getMissedCharacter() {
        return missingCharacter;
    }

}
