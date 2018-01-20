package ru.nekit.android.qls.quest.model;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.INameHolder;

public enum ColorModel implements INameHolder {

    WHITE(R.color.white, R.string.color_white),
    BLACK(R.color.black, R.string.color_black),
    RED(R.color.red, R.string.color_red),
    GREEN(R.color.green, R.string.color_green);

    @ColorRes
    private final int colorResId;
    @StringRes
    private final int titleResId;

    ColorModel(@ColorRes int colorResId, @StringRes int titleResId) {
        this.colorResId = colorResId;
        this.titleResId = titleResId;
    }

    public static ColorModel getById(int itemId) {
        return values()[itemId];
    }

    @ColorInt
    public int getColor(@NonNull Context context) {
        return context.getResources().getColor(colorResId);
    }

    public int getColorResId() {
        return colorResId;
    }

    @NonNull
    public String getName(@NonNull Context context) {
        return context.getString(titleResId);
    }

    public int getId() {
        return ordinal();
    }
}