package ru.nekit.android.qls.quest.resources.collections;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.IStringHolder;

public enum LocalizedAdjectiveStringResourceCollection implements IStringHolder {

    WHITE(R.string.color_white),
    BLACK(R.string.color_black),
    RED(R.string.color_red),
    GREEN(R.string.color_green);

    @StringRes
    private int mStringResourceId;

    LocalizedAdjectiveStringResourceCollection(@StringRes int titleResourceId) {
        mStringResourceId = titleResourceId;
    }

    @NonNull
    public String getString(@NonNull Context context) {
        return context.getString(mStringResourceId);
    }

}