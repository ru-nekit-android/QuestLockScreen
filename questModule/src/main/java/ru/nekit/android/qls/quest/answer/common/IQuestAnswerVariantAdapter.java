package ru.nekit.android.qls.quest.answer.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface IQuestAnswerVariantAdapter<T> {

    @Nullable
    String adapt(@NonNull Context context, @NonNull T answerVariant);

}
