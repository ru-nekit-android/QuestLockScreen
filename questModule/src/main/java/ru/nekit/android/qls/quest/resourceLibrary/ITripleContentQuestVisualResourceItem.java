package ru.nekit.android.qls.quest.resourceLibrary;

import android.support.annotation.DrawableRes;

public interface ITripleContentQuestVisualResourceItem extends IQuestVisualResourceItem {

    @DrawableRes
    int getContentDrawableResourceId();

    @DrawableRes
    int getBackgroundDrawableResourceId();

    @DrawableRes
    int getForegroundDrawableResourceId();

}
