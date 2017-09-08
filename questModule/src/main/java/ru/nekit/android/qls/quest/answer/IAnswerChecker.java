package ru.nekit.android.qls.quest.answer;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.IQuest;

public interface IAnswerChecker {

    boolean checkAlternativeInput(@NonNull IQuest quest, @NonNull Object answer);

    boolean checkStringInputFormat(@NonNull IQuest quest, @NonNull String value);

    boolean checkStringInput(@NonNull IQuest quest, @NonNull String value);

}
