package ru.nekit.android.qls.quest.types.model;


import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import ru.nekit.android.qls.R;

public enum CoinModel {

    ONE(1, 205, R.drawable.background_coin_one),
    TWO(2, 230, R.drawable.background_coin_two),
    FIVE(5, 250, R.drawable.background_coin_five),
    TEN(10, 220, R.drawable.background_coin_ten);

    public final int background, nomination, relativeSizeValue;

    CoinModel(int nomination, int relativeSizeValue, @DrawableRes int background) {
        this.nomination = nomination;
        this.relativeSizeValue = relativeSizeValue;
        this.background = background;
    }

    @Nullable
    public static CoinModel getByNomination(int value) {
        for (CoinModel coinModel : values()) {
            if (value == coinModel.nomination) {
                return coinModel;
            }
        }
        return null;
    }

    public static int getMaxRelativeSizeValue() {
        int maxRelativeSizeValue = 0;
        for (CoinModel coinModel : values()) {
            maxRelativeSizeValue = Math.max(coinModel.relativeSizeValue, maxRelativeSizeValue);
        }
        return maxRelativeSizeValue;
    }
}
