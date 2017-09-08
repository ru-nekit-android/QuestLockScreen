package ru.nekit.android.qls.quest.qtp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.quest.ITitleable;
import ru.nekit.android.shared.R;

public enum QuestTrainingProgramComplexity implements ITitleable {

    HARD(R.string.complexity_hard),
    NORMAL(R.string.complexity_normal),
    EASY(R.string.complexity_easy);

    @StringRes
    private int titleId;

    QuestTrainingProgramComplexity(@StringRes int titleId) {
        this.titleId = titleId;
    }

    public String getTitle(@NonNull Context context) {
        return context.getResources().getString(titleId);
    }

}
