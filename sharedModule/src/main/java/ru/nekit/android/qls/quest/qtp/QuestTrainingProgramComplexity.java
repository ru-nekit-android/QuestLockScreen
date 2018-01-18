package ru.nekit.android.qls.quest.qtp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.quest.INameHolder;
import ru.nekit.android.shared.R;

public enum QuestTrainingProgramComplexity implements INameHolder {

    HARD(R.string.complexity_hard),
    NORMAL(R.string.complexity_normal),
    EASY(R.string.complexity_easy);

    @StringRes
    private int titleId;

    QuestTrainingProgramComplexity(@StringRes int titleId) {
        this.titleId = titleId;
    }

    @NonNull
    public String getName(@NonNull Context context) {
        return context.getResources().getString(titleId);
    }

}
