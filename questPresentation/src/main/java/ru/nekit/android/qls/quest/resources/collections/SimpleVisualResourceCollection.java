package ru.nekit.android.qls.quest.resources.collections;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.resources.common.ILocalizedNounStringResourceHolder;
import ru.nekit.android.qls.quest.resources.common.IVisualResourceHolder;

public enum SimpleVisualResourceCollection implements IVisualResourceHolder,
        ILocalizedNounStringResourceHolder {

    MINUS(R.drawable.qvri_minus,
            R.string.qvri_minus_title,
            VisualResourceGroupCollection.MATH_OPERATOR),

    PLUS(R.drawable.qvri_plus,
            R.string.qvri_plus_title,
            VisualResourceGroupCollection.MATH_OPERATOR),

    EQUAL(R.drawable.qvri_equal,
            R.string.qvri_equal_title,
            VisualResourceGroupCollection.MATH_OPERATOR),

    ORANGE(R.drawable.qvri_orange,
            LocalizedNounStringResourceCollection.ORANGE,
            VisualResourceGroupCollection.POMUM),

    STRAWBERRY(R.drawable.qvri_strawberry,
            LocalizedNounStringResourceCollection.STRAWBERRY,
            VisualResourceGroupCollection.BERRY),

    APPLE(R.drawable.qvri_apple,
            LocalizedNounStringResourceCollection.APPLE,
            VisualResourceGroupCollection.POMUM),

    PEAR(R.drawable.qvri_pear,
            LocalizedNounStringResourceCollection.PEAR,
            VisualResourceGroupCollection.POMUM),

    CHERRY(R.drawable.qvri_cherry,
            LocalizedNounStringResourceCollection.CHERRY,
            VisualResourceGroupCollection.BERRY),

    RASPBERRY(R.drawable.qvri_raspberry,
            LocalizedNounStringResourceCollection.RASPBERRY,
            VisualResourceGroupCollection.BERRY),

    PINEAPPLE(R.drawable.qvri_pineapple,
            LocalizedNounStringResourceCollection.PINEAPPLE,
            VisualResourceGroupCollection.POMUM),

    BLACKBERRY(R.drawable.qvri_blackberry,
            LocalizedNounStringResourceCollection.BLACKBERRY,
            VisualResourceGroupCollection.BERRY),

    WINTER(R.drawable.qvri_tree_winter,
            LocalizedNounStringResourceCollection.WINTER,
            VisualResourceGroupCollection.SEASONS),

    SPRING(R.drawable.qvri_tree_spring,
            LocalizedNounStringResourceCollection.SPRING,
            VisualResourceGroupCollection.SEASONS),

    SUMMER(R.drawable.qvri_tree_summer,
            LocalizedNounStringResourceCollection.SUMMER,
            VisualResourceGroupCollection.SEASONS),

    FALL(R.drawable.qvri_tree_fall,
            LocalizedNounStringResourceCollection.FALL,
            VisualResourceGroupCollection.SEASONS);

    @DrawableRes
    private int mDrawableResourceId;
    @StringRes
    private int mNameResourceId;
    @Nullable
    private VisualResourceGroupCollection[] mGroups;
    @javax.annotation.Nullable
    private LocalizedNounStringResourceCollection mLocalizedNounStringResourceCollection;

    SimpleVisualResourceCollection(@DrawableRes int drawableResourceId,
                                   @StringRes int nameResourceId,
                                   @Nullable VisualResourceGroupCollection... groups) {
        mDrawableResourceId = drawableResourceId;
        mNameResourceId = nameResourceId;
        mGroups = groups;
    }

    SimpleVisualResourceCollection(@DrawableRes int drawableResourceId,
                                   @NonNull LocalizedNounStringResourceCollection localizedNounStringResourceCollection,
                                   @Nullable VisualResourceGroupCollection... groups) {
        mDrawableResourceId = drawableResourceId;
        mNameResourceId = -1;
        mLocalizedNounStringResourceCollection = localizedNounStringResourceCollection;
        mGroups = groups;
    }

    int getId() {
        return ordinal();
    }

    @Nullable
    public LocalizedNounStringResourceCollection getLocalStringResource() {
        return mLocalizedNounStringResourceCollection;
    }

    @NonNull
    @Override
    public String getString(@NonNull Context context) {
        return mLocalizedNounStringResourceCollection == null ? context.getString(mNameResourceId) :
                mLocalizedNounStringResourceCollection.getString(context);
    }

    @Nullable
    public VisualResourceGroupCollection[] getGroups() {
        return mGroups;
    }

    @DrawableRes
    public int getDrawableResourceId() {
        return mDrawableResourceId;
    }
}