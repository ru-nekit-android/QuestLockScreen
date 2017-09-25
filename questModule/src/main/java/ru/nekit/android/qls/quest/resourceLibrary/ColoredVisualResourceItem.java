package ru.nekit.android.qls.quest.resourceLibrary;


import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

public class ColoredVisualResourceItem {

    @DrawableRes
    public final int drawableResourceId;
    @NonNull
    public final ColorType colorType;

    public ColoredVisualResourceItem(@DrawableRes int drawableResourceId, ColorType colorType) {
        this.drawableResourceId = drawableResourceId;
        this.colorType = colorType;
    }

    public ColoredVisualResourceItem(@DrawableRes int drawableResourceId) {
        this.drawableResourceId = drawableResourceId;
        this.colorType = ColorType.NONE;
    }

    public enum ColorType {

        NONE,
        COLOR_AS_CONTENT,
        COLOR_AS_BACKGROUND

    }
}
