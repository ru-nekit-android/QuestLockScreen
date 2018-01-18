package ru.nekit.android.qls.quest.types;

import ru.nekit.android.qls.quest.Quest;
import ru.nekit.android.qls.quest.QuestionType;

public class MetricsQuest extends NumberSummandQuest {

    public static final int METRICS_ITEM_COUNT = 3;
    public static final int METRICS_CENTIMETER_IN_METER = 100;
    public static final int METRICS_CENTIMETER_IN_DECIMETER = 10;

    MetricsQuest(QuestionType type) {
        setQuestionType(type);
    }

    public static Quest convert(Quest quest) {
        NumberSummandQuest inQuest = (NumberSummandQuest) quest;
        if (quest.getQuestionType() == QuestionType.SOLUTION) {
            avoidSimpleSolution(inQuest.leftNode);
        } else if (quest.getQuestionType() == QuestionType.COMPARISON) {
            avoidSimpleSolution(inQuest.leftNode);
            avoidSimpleSolution(inQuest.rightNode);
        }
        return quest;
    }

    private static void avoidSimpleSolution(int[] value) {
        if (value != null && value[0] == 0 && value[1] == 0) {
            int addValue = METRICS_CENTIMETER_IN_DECIMETER;
            if (value[2] < addValue) {
                value[2] += addValue;
            }
            value[1] = value[2] / addValue;
            value[2] = value[2] % addValue;
        }
    }
}