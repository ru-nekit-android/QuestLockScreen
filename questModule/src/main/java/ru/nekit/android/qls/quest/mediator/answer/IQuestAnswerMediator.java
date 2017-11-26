package ru.nekit.android.qls.quest.mediator.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.answer.common.IAnswerCallback;
import ru.nekit.android.qls.quest.answer.common.IAnswerChecker;
import ru.nekit.android.qls.quest.mediator.IQuestMediator;

public interface IQuestAnswerMediator extends IQuestMediator {

    void setAnswerCallback(@NonNull IAnswerCallback answerCallback);

    void setAnswerChecker(@NonNull IAnswerChecker answerChecker);

}