package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;
import ru.nekit.android.qls.quest.types.TrafficLightType;

public class TrafficLightQuestAnswerChecker extends QuestAnswerChecker {

    @Override
    public boolean checkAlternativeInput(@NonNull IQuest quest, @NonNull Object answer) {
        NumberSummandQuest numberSummandQuest = (NumberSummandQuest) quest;
        TrafficLightType questAnswer = TrafficLightType.fromOrdinal(
                numberSummandQuest.getTypedAnswer());
        TrafficLightType inputAnswer = TrafficLightType.fromOrdinal((int) answer);
        switch (quest.getQuestionType()) {

            case SOLUTION:

                if (inputAnswer == TrafficLightType.RED) {
                    return questAnswer == TrafficLightType.RED || questAnswer == TrafficLightType.YELLOW;
                } else if (inputAnswer == TrafficLightType.GREEN) {
                    return questAnswer == inputAnswer;
                }

            default:

                return false;

        }
    }
}
