package ru.nekit.android.qls.quest.resourceLibrary;


import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

public class ColoredVisualResourceItem {

    @DrawableRes
    public final int drawableResourceId;
    @NonNull
    public ColorType colorType;

    public ColoredVisualResourceItem(@DrawableRes int drawableResourceId,
                                     @NonNull ColorType colorType) {
        this.drawableResourceId = drawableResourceId;
        this.colorType = colorType;
    }

    public ColoredVisualResourceItem(@DrawableRes int drawableResourceId) {
        this.drawableResourceId = drawableResourceId;
        this.colorType = ColorType.NONE;
    }

    public enum ColorType {

        NONE,
        AS_PRIMARY,
        AS_SECONDARY,
        INVERSE_AS_PRIMARY,
        INVERSE_AS_SECONDARY;

    }
}
