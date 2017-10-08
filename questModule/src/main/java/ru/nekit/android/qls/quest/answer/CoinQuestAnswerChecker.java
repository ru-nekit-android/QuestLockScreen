package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.answer.shared.QuestAnswerChecker;
import ru.nekit.android.qls.quest.model.CoinModel;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

public class CoinQuestAnswerChecker extends QuestAnswerChecker<CoinModel> {

    @Override
    public boolean checkAlternativeInput(@NonNull IQuest quest, @NonNull CoinModel answer) {

        switch (quest.getQuestionType()) {

            case UNKNOWN_MEMBER:

                return CoinModel.getById((int) quest.getAnswer()).nomination == answer.nomination;

            default:

                return true;
        }
    }

    @Override
    public boolean checkStringInput(@NonNull IQuest inQuest, @NonNull String value) {
        NumberSummandQuest quest = (NumberSummandQuest) inQuest;
        int answer = 0;
        for (int i = 0; i < quest.leftNode.length; i++) {
            answer += CoinModel.getById(quest.leftNode[i]).nomination;
        }
        return answer == Integer.valueOf(value);
    }
}
