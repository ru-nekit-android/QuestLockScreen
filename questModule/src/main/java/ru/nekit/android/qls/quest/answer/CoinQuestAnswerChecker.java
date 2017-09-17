package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.answer.shared.QuestAnswerChecker;
import ru.nekit.android.qls.quest.types.CoinModel;

public class CoinQuestAnswerChecker extends QuestAnswerChecker<CoinModel> {

    @Override
    public boolean checkAlternativeInput(@NonNull IQuest quest, @NonNull CoinModel answer) {

        switch (quest.getQuestionType()) {

            case UNKNOWN_MEMBER:

                return (int) quest.getAnswer() == answer.nomination;

            default:

                return true;
        }
    }
}
