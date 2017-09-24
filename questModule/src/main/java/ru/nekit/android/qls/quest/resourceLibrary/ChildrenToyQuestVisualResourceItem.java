package ru.nekit.android.qls.quest.resourceLibrary;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.R;

public enum ChildrenToyQuestVisualResourceItem
        implements ITripleContentQuestVisualResourceItem {

    CAR(R.drawable.qvri_car_background,
            R.drawable.qvri_car_content,
            R.drawable.qvri_car_foreground,
            R.string.qvri_car_title,
            QuestVisualResourceGroup.BOY),

    DOLL(0, 0, 0, R.string.qvri_doll_title, QuestVisualResourceGroup.GIRL);

    @DrawableRes
    private int mDrawableContentResourceId, mDrawableBackgroundResourceId, mDrawableForegroundResourceId;
    @StringRes
    private int mTitleResourceId;
    @Nullable
    private QuestVisualResourceGroup[] mGroups;

    ChildrenToyQuestVisualResourceItem(@DrawableRes int drawableBackgroundResourceId,
                                       @DrawableRes int drawableContentResourceId,
                                       @DrawableRes int drawableForegroundResourceId,
                                       @StringRes int titleResourceId,
                                       @Nullable QuestVisualResourceGroup... groups) {
        mDrawableBackgroundResourceId = drawableBackgroundResourceId;
        mDrawableContentResourceId = drawableContentResourceId;
        mDrawableForegroundResourceId = drawableForegroundResourceId;
        mTitleResourceId = titleResourceId;
        mGroups = groups;
    }

    int getId() {
        return ordinal();
    }

    @Override
    public String getTitle(@NonNull Context context) {
        return context.getString(mTitleResourceId);
    }

    @Nullable
    public QuestVisualResourceGroup[] getGroups() {
        return mGroups;
    }

    @DrawableRes
    public int getContentDrawableResourceId() {
        return mDrawableContentResourceId;
    }

    @DrawableRes
    public int getBackgroundDrawableResourceId() {
        return mDrawableBackgroundResourceId;
    }

    @DrawableRes
    public int getForegroundDrawableResourceId() {
        return mDrawableForegroundResourceId;
    }

    @DrawableRes
    public int getDrawableResourceId() {
        return getContentDrawableResourceId();
    }
}