package ru.nekit.android.qls.quest.resources.collections;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.resources.common.ILocalizedAdjectiveStringResourceHolder;

public enum ColorResourceCollection implements ILocalizedAdjectiveStringResourceHolder {

    WHITE(R.color.white, LocalizedAdjectiveStringResourceCollection.WHITE),
    BLACK(R.color.black, LocalizedAdjectiveStringResourceCollection.BLACK),
    RED(R.color.red, LocalizedAdjectiveStringResourceCollection.RED),
    GREEN(R.color.green, LocalizedAdjectiveStringResourceCollection.GREEN);

    @ColorRes
    private final int mColorResId;
    @NonNull
    private final LocalizedAdjectiveStringResourceCollection mAdjectiveStringResourceCollection;

    ColorResourceCollection(@ColorRes int colorResId,
                            @NonNull LocalizedAdjectiveStringResourceCollection adjectiveStringResourceCollection) {
        mColorResId = colorResId;
        mAdjectiveStringResourceCollection = adjectiveStringResourceCollection;
    }

    public static ColorResourceCollection getById(int itemId) {
        return values()[itemId];
    }

    @NonNull
    public LocalizedAdjectiveStringResourceCollection getLocalStringResource() {
        return mAdjectiveStringResourceCollection;
    }

    @NonNull
    public String getString(@NonNull Context context) {
        return mAdjectiveStringResourceCollection.getString(context);
    }

    @ColorInt
    public int getColor(@NonNull Context context) {
        return context.getResources().getColor(mColorResId);
    }

    public int getColorResId() {
        return mColorResId;
    }


    public int getId() {
        return ordinal();
    }
}