package ru.nekit.android.qls.quest.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.ITitleable;

public enum DirectionModel implements ITitleable {

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

    public String getTitle(@NonNull Context context) {
        return context.getResources().getString(titleResId);
    }
}
