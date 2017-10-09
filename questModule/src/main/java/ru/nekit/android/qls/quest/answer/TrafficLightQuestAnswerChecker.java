package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.answer.shared.QuestAnswerChecker;
import ru.nekit.android.qls.quest.base.Quest;
import ru.nekit.android.qls.quest.model.TrafficLightModel;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

import static ru.nekit.android.qls.quest.model.TrafficLightModel.RED;

public class TrafficLightQuestAnswerChecker extends QuestAnswerChecker<Integer> {

    @Override
    public boolean checkAlternativeInput(@NonNull Quest quest, @NonNull Integer answer) {
        NumberSummandQuest numberSummandQuest = (NumberSummandQuest) quest;
        TrafficLightModel questAnswer = TrafficLightModel.fromOrdinal(
                numberSummandQuest.getAnswer());
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
