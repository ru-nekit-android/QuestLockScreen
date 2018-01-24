package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.Quest;
import ru.nekit.android.qls.quest.answer.common.QuestAnswerChecker;
import ru.nekit.android.qls.quest.resources.collections.CoinVisualResourceCollection;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

public class CoinQuestAnswerChecker extends QuestAnswerChecker<CoinVisualResourceCollection> {

    @Override
    public boolean checkAlternativeInput(@NonNull Quest inQuest, @NonNull CoinVisualResourceCollection answer) {
        NumberSummandQuest quest = (NumberSummandQuest) inQuest;
        switch (inQuest.getQuestionType()) {

            case UNKNOWN_MEMBER:

                return CoinVisualResourceCollection.getById(quest.getAnswer()).nomination == answer.nomination;

            default:

                return true;
        }
    }

    @Override
    public boolean checkStringInput(@NonNull Quest inQuest, @NonNull String value) {
        NumberSummandQuest quest = (NumberSummandQuest) inQuest;
        int answer = 0;
        for (int i = 0; i < quest.leftNode.length; i++) {
            answer += CoinVisualResourceCollection.getById(quest.leftNode[i]).nomination;
        }
        return answer == Integer.valueOf(value);
    }
}