package ru.nekit.android.qls.quest.resourceLibrary;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.R;

public enum BaseQuestVisualResourceItem implements IQuestVisualResourceItem {

    MINUS(R.drawable.qvri_minus,
            R.string.qvri_minus_title,
            QuestVisualResourceGroup.MATH_OPERATOR),

    PLUS(R.drawable.qvri_plus,
            R.string.qvri_plus_title,
            QuestVisualResourceGroup.MATH_OPERATOR),

    EQUAL(R.drawable.qvri_equal,
            R.string.qvri_equal_title,
            QuestVisualResourceGroup.MATH_OPERATOR),

    ORANGE(R.drawable.qvri_orange,
            R.string.qvri_orange_title,
            QuestVisualResourceGroup.POMUM),

    STRAWBERRY(R.drawable.qvri_strawberry,
            R.string.qvri_strawberry_title,
            QuestVisualResourceGroup.BERRY),

    APPLE(R.drawable.qvri_apple,
            R.string.qvri_apple_title,
            QuestVisualResourceGroup.POMUM),

    PEAR(R.drawable.qvri_pear,
            R.string.qvri_pear_title,
            QuestVisualResourceGroup.POMUM),

    CHERRY(R.drawable.qvri_cherry,
            R.string.qvri_cherry_title,
            QuestVisualResourceGroup.BERRY),

    RASPBERRY(R.drawable.qvri_raspberry,
            R.string.qvri_raspberry_title,
            QuestVisualResourceGroup.BERRY),

    WINTER(R.drawable.qvri_tree_winter,
            R.string.qvri_winter_title,
            QuestVisualResourceGroup.SEASONS),

    SPRING(R.drawable.qvri_tree_spring, R.string.qvri_spring_title,
            QuestVisualResourceGroup.SEASONS),

    SUMMER(R.drawable.qvri_tree_summer, R.string.qvri_summer_title,
            QuestVisualResourceGroup.SEASONS),

    FALL(R.drawable.qvri_tree_fall, R.string.qvri_fall_title,
            QuestVisualResourceGroup.SEASONS),

    PINEAPPLE(R.drawable.qvri_pineapple, R.string.qvri_pineapple_title,
            QuestVisualResourceGroup.POMUM),

    BLACKBERRY(R.drawable.qvri_blackberry, R.string.qvri_blackberry_title,
            QuestVisualResourceGroup.BERRY);

    @DrawableRes
    private int mDrawableResourceId;
    @StringRes
    private int mTitleResourceId;
    @Nullable
    private QuestVisualResourceGroup[] mGroups;

    BaseQuestVisualResourceItem(@DrawableRes int drawableResourceId,
                                @StringRes int titleResourceId,
                                @Nullable QuestVisualResourceGroup... groups) {
        mDrawableResourceId = drawableResourceId;
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
    public int getDrawableResourceId() {
        return mDrawableResourceId;
    }
}