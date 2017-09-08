package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.math.MathematicalSignComparison;
import ru.nekit.android.qls.quest.types.MetricsQuest;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

public class MetricsQuestAnswerChecker extends QuestAnswerChecker {

    @Override
    public boolean checkAlternativeInput(@NonNull IQuest quest, @NonNull Object answer) {
        NumberSummandQuest numberSummandQuest = (NumberSummandQuest) quest;
        switch (quest.getQuestionType()) {

            case COMPARISON:

                MathematicalSignComparison answerSign = (MathematicalSignComparison) answer;
                int leftMetricsSum = getMetricsSum(numberSummandQuest.leftNode);
                int rightMetricsSum = getMetricsSum(numberSummandQuest.rightNode);
                MathematicalSignComparison metricsSign = MathematicalSignComparison.EQUAL;
                if (leftMetricsSum > rightMetricsSum) {
                    metricsSign = MathematicalSignComparison.GREATER;
                } else if (leftMetricsSum < rightMetricsSum) {
                    metricsSign = MathematicalSignComparison.LESS;
                }
                return metricsSign == answerSign;

            default:

                return false;

        }
    }

    private int getMetricsSum(int[] node) {
        int metricsSum = 0;
        int[] converterValues = new int[]{MetricsQuest.METRICS_CENTIMETER_IN_METER,
                MetricsQuest.METRICS_CENTIMETER_IN_DECIMETER, 1};
        for (int i = 0; i < MetricsQuest.METRICS_ITEM_COUNT; i++) {
            metricsSum += converterValues[i] * node[i];
        }
        return metricsSum;
    }

    @Override
    public boolean checkStringInput(@NonNull IQuest quest, @NonNull String value) {
        NumberSummandQuest numberSummandQuest = (NumberSummandQuest) quest;
        switch (quest.getQuestionType()) {
            case SOLUTION:
                return getMetricsSum(numberSummandQuest.leftNode) == Integer.valueOf(value);
            default:
                return false;
        }
    }
}
