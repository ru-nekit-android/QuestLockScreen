package ru.nekit.android.qls.quest.answer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface IAlternativeAnswerVariantAdapter {

    @Nullable
    String adapt(@NonNull Context context, @NonNull Object answerVariant);

}
