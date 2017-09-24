package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.answer.shared.QuestAnswerChecker;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;
import ru.nekit.android.qls.quest.types.model.TrafficLightModel;

import static ru.nekit.android.qls.quest.types.model.TrafficLightModel.RED;

public class TrafficLightQuestAnswerChecker extends QuestAnswerChecker<Integer> {

    @Override
    public boolean checkAlternativeInput(@NonNull IQuest quest, @NonNull Integer answer) {
        NumberSummandQuest numberSummandQuest = (NumberSummandQuest) quest;
        TrafficLightModel questAnswer = TrafficLightModel.fromOrdinal(
                numberSummandQuest.getTypedAnswer());
        TrafficLightModel inputAnswer = TrafficLightModel.fromOrdinal(answer);
        switch (quest.getQuestionType()) {

            case SOLUTION:

                if (inputAnswer == RED) {
                    return questAnswer == RED || questAnswer == TrafficLightModel.YELLOW;
                } else if (inputAnswer == TrafficLightModel.GREEN) {
                    return questAnswer == inputAnswer;
                }

            default:

                return false;

        }
    }
}
