package ru.nekit.android.qls.quest.formatter;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.Quest;

public interface IQuestTextContentFormatter {

    String[] format(@NonNull Context context, @NonNull Quest quest);

    String getMissedCharacter();

}