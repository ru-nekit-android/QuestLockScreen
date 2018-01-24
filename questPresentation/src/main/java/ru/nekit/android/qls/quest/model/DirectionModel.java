package ru.nekit.android.qls.quest.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.IStringHolder;

public enum DirectionModel implements IStringHolder {

    UP(R.string.quest_direction_title_up),
    RIGHT(R.string.quest_direction_title_right),
    DOWN(R.string.quest_direction_title_down),
    LEFT(R.string.quest_direction_title_left);

    @StringRes
    private final int titleResId;

    DirectionModel(@StringRes int titleResId) {
        this.titleResId = titleResId;
    }

    public static DirectionModel fromOrdinal(int ordinal) {
        return values()[ordinal];
    }

    @NonNull
    public String getString(@NonNull Context context) {
        return context.getString(titleResId);
    }
}
