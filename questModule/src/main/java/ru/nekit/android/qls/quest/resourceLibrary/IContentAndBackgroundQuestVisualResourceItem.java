package ru.nekit.android.qls.quest.resourceLibrary;

import android.support.annotation.DrawableRes;

public interface IContentAndBackgroundQuestVisualResourceItem extends IQuestVisualResourceItem {

    @DrawableRes
    int getContentDrawableResourceId();

    @DrawableRes
    int getBackgroundDrawableResourceId();

}
