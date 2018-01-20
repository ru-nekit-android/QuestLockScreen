package ru.nekit.android.qls.quest.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.INameHolder;
import ru.nekit.android.qls.utils.MathUtils;

public enum TrafficLightModel implements INameHolder {

    GREEN(R.string.go, R.string.cross),
    RED(R.string.stand, R.string.wait);

    @StringRes
    private int[] mTitleIds;

    TrafficLightModel(@StringRes int... titlesIds) {
        mTitleIds = titlesIds;
    }

    public static TrafficLightModel fromOrdinal(int ordinal) {
        return values()[ordinal];
    }

    @NonNull
    @Override
    public String getName(@NonNull Context context) {
        return context.getString(MathUtils.randItem(mTitleIds));
    }

}
