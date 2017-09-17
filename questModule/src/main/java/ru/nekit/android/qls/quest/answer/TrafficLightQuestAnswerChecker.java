package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.answer.shared.QuestAnswerChecker;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;
import ru.nekit.android.qls.quest.types.TrafficLightType;

import static ru.nekit.android.qls.quest.types.TrafficLightType.RED;

public class TrafficLightQuestAnswerChecker extends QuestAnswerChecker<Integer> {

    @Override
    public boolean checkAlternativeInput(@NonNull IQuest quest, @NonNull Integer answer) {
        NumberSummandQuest numberSummandQuest = (NumberSummandQuest) quest;
        TrafficLightType questAnswer = TrafficLightType.fromOrdinal(
                numberSummandQuest.getTypedAnswer());
        TrafficLightType inputAnswer = TrafficLightType.fromOrdinal(answer);
        switch (quest.getQuestionType()) {

            case SOLUTION:

                if (inputAnswer == RED) {
                    return questAnswer == RED || questAnswer == TrafficLightType.YELLOW;
                } else if (inputAnswer == TrafficLightType.GREEN) {
                    return questAnswer == inputAnswer;
                }

            default:

                return false;

        }
    }
}
