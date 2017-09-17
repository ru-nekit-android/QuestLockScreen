package ru.nekit.android.qls.quest.answer.shared;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.IQuest;

public interface IAnswerChecker<T> {

    boolean checkAlternativeInput(@NonNull IQuest quest, @NonNull T answer);

    boolean checkStringInputFormat(@NonNull IQuest quest, @NonNull String value);

    boolean checkStringInput(@NonNull IQuest quest, @NonNull String value);

}
