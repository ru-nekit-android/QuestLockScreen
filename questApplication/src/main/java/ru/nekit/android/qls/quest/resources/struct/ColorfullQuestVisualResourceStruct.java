package ru.nekit.android.qls.quest.resources.struct;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

public class ColorfullQuestVisualResourceStruct {

    @DrawableRes
    public final int drawableResourceId;
    @NonNull
    public ColorType colorType;

    public ColorfullQuestVisualResourceStruct(@DrawableRes int drawableResourceId, @NonNull ColorType colorType) {
        this.drawableResourceId = drawableResourceId;
        this.colorType = colorType;
    }

    public enum ColorType {

        NONE,
        PRIMARY,
        SECONDARY,
        PRIMARY_INVERSE,
        SECONDARY_INVERSE

    }
}