package ru.nekit.android.qls.quest.formatter;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.IQuest;

public interface IQuestTextContentFormatter {

    String[] format(@NonNull Context context, @NonNull IQuest quest);

    String getMissedCharacter();

}