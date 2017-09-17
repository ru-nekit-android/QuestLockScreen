package ru.nekit.android.qls.quest.mediator.shared.answer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;

import ru.nekit.android.qls.quest.answer.shared.IAnswerCallback;
import ru.nekit.android.qls.quest.answer.shared.IAnswerChecker;
import ru.nekit.android.qls.quest.mediator.IQuestMediator;

public interface IQuestAlternativeAnswerMediator extends IQuestMediator {

    @Nullable
    List<View> getAnswerButtonList();

    void setAnswerCallback(@NonNull IAnswerCallback answerCallback);

    void setAnswerChecker(@NonNull IAnswerChecker answerChecker);
}
