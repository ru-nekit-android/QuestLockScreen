package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.Quest;
import ru.nekit.android.qls.quest.answer.common.QuestAnswerChecker;
import ru.nekit.android.qls.quest.model.TrafficLightModel;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

import static ru.nekit.android.qls.quest.model.TrafficLightModel.fromOrdinal;

public class TrafficLightQuestAnswerChecker extends QuestAnswerChecker<Integer> {

    @Override
    public boolean checkAlternativeInput(@NonNull Quest quest, @NonNull Integer answer) {
        NumberSummandQuest numberSummandQuest = (NumberSummandQuest) quest;
        TrafficLightModel questAnswer = fromOrdinal(
                numberSummandQuest.getAnswer());
        TrafficLightModel inputAnswer = fromOrdinal(answer);
        switch (quest.getQuestionType()) {

            case SOLUTION:

                return questAnswer == inputAnswer;

            default:

                return false;

        }
    }
}
