package ru.nekit.android.qls.quest.answer.shared;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.base.Quest;

public interface IAnswerChecker<T> {

    boolean checkAlternativeInput(@NonNull Quest quest, @NonNull T answer);

    boolean checkStringInputFormat(@NonNull Quest quest, @NonNull String value);

    boolean checkStringInput(@NonNull Quest quest, @NonNull String value);

}
