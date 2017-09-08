package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.types.CoinModel;

public class CoinQuestAnswerChecker extends QuestAnswerChecker {

    @Override
    public boolean checkAlternativeInput(@NonNull IQuest quest, @NonNull Object answer) {

        switch (quest.getQuestionType()) {

            case UNKNOWN_MEMBER:

                return (int) quest.getAnswer() == ((CoinModel) answer).nomination;

            default:

                return true;
        }
    }
}
