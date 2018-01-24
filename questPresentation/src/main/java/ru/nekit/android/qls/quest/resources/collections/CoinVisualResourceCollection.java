package ru.nekit.android.qls.quest.resources.collections;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.resources.common.IVisualResourceHolder;

public enum CoinVisualResourceCollection implements IVisualResourceHolder {

    ONE(R.drawable.background_coin_one, R.string.qvri_coin_one_title, 1, 205),
    TWO(R.drawable.background_coin_two, R.string.qvri_coin_two_title, 2, 230),
    FIVE(R.drawable.background_coin_five, R.string.qvri_coin_five_title, 5, 250),
    TEN(R.drawable.background_coin_ten, R.string.qvri_coin_ten_title, 10, 220);

    public final int mNomination, mRelativeSizeValue;
    @DrawableRes
    private final int mDrawableResourceId;
    private final int mStringResourceId;

    CoinVisualResourceCollection(@DrawableRes int drawableResourceId,
                                 @StringRes int stringResourceId,
                                 int nomination,
                                 int relativeSizeValue) {
        mDrawableResourceId = drawableResourceId;
        mStringResourceId = stringResourceId;
        mNomination = nomination;
        mRelativeSizeValue = relativeSizeValue;
    }

    @Nullable
    public static CoinVisualResourceCollection getById(int id) {
        return values()[id];
    }

    public static int getMaxRelativeSizeValue() {
        int maxRelativeSizeValue = 0;
        for (CoinVisualResourceCollection coinVisualResourceCollection : values()) {
            maxRelativeSizeValue = Math.max(coinVisualResourceCollection.mRelativeSizeValue,
                    maxRelativeSizeValue);
        }
        return maxRelativeSizeValue;
    }

    public int getId() {
        return ordinal();
    }

    @Nullable
    @Override
    public VisualResourceGroupCollection[] getGroups() {
        return new VisualResourceGroupCollection[]{
                VisualResourceGroupCollection.COINS
        };
    }

    @DrawableRes
    @Override
    public int getDrawableResourceId() {
        return mDrawableResourceId;
    }

    @NonNull
    @Override
    public String getString(@NonNull Context context) {
        return context.getString(mStringResourceId);
    }
}