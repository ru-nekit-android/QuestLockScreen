package ru.nekit.android.qls.quest.resources.collections;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.resources.common.IVisualQuestResourceHolder;

public enum SimpleQuestVisualQuestResourceCollection implements IVisualQuestResourceHolder {

    MINUS(R.drawable.qvri_minus,
            R.string.qvri_minus_title,
            VisualQuestResourceGroupCollection.MATH_OPERATOR),

    PLUS(R.drawable.qvri_plus,
            R.string.qvri_plus_title,
            VisualQuestResourceGroupCollection.MATH_OPERATOR),

    EQUAL(R.drawable.qvri_equal,
            R.string.qvri_equal_title,
            VisualQuestResourceGroupCollection.MATH_OPERATOR),

    ORANGE(R.drawable.qvri_orange,
            R.string.qvri_orange_title,
            VisualQuestResourceGroupCollection.POMUM),

    STRAWBERRY(R.drawable.qvri_strawberry,
            R.string.qvri_strawberry_title,
            VisualQuestResourceGroupCollection.BERRY),

    APPLE(R.drawable.qvri_apple,
            R.string.qvri_apple_title,
            VisualQuestResourceGroupCollection.POMUM),

    PEAR(R.drawable.qvri_pear,
            R.string.qvri_pear_title,
            VisualQuestResourceGroupCollection.POMUM),

    CHERRY(R.drawable.qvri_cherry,
            R.string.qvri_cherry_title,
            VisualQuestResourceGroupCollection.BERRY),

    RASPBERRY(R.drawable.qvri_raspberry,
            R.string.qvri_raspberry_title,
            VisualQuestResourceGroupCollection.BERRY),

    WINTER(R.drawable.qvri_tree_winter,
            R.string.qvri_winter_title,
            VisualQuestResourceGroupCollection.SEASONS),

    SPRING(R.drawable.qvri_tree_spring, R.string.qvri_spring_title,
            VisualQuestResourceGroupCollection.SEASONS),

    SUMMER(R.drawable.qvri_tree_summer, R.string.qvri_summer_title,
            VisualQuestResourceGroupCollection.SEASONS),

    FALL(R.drawable.qvri_tree_fall, R.string.qvri_fall_title,
            VisualQuestResourceGroupCollection.SEASONS),

    PINEAPPLE(R.drawable.qvri_pineapple, R.string.qvri_pineapple_title,
            VisualQuestResourceGroupCollection.POMUM),

    BLACKBERRY(R.drawable.qvri_blackberry, R.string.qvri_blackberry_title,
            VisualQuestResourceGroupCollection.BERRY);

    @DrawableRes
    private int mDrawableResourceId;
    @StringRes
    private int mNameResourceId;
    @Nullable
    private VisualQuestResourceGroupCollection[] mGroups;

    SimpleQuestVisualQuestResourceCollection(@DrawableRes int drawableResourceId,
                                             @StringRes int nameResourceId,
                                             @Nullable VisualQuestResourceGroupCollection... groups) {
        mDrawableResourceId = drawableResourceId;
        mNameResourceId = nameResourceId;
        mGroups = groups;
    }

    int getId() {
        return ordinal();
    }

    @NonNull
    @Override
    public String getName(@NonNull Context context) {
        return context.getString(mNameResourceId);
    }

    @Nullable
    public VisualQuestResourceGroupCollection[] getGroups() {
        return mGroups;
    }

    @DrawableRes
    public int getDrawableResourceId() {
        return mDrawableResourceId;
    }
}