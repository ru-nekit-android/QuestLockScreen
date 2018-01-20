package ru.nekit.android.qls.quest.resources.collections;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.INameHolder;
import ru.nekit.android.qls.utils.Declension;

public enum LocalizedStringResourceCollection implements INameHolder {

    CAR(R.string.qvri_car_title,
            Declension.Gender.FEMALE,
            false),
    BOOTS(R.string.qvri_doll_boots_title,
            Declension.Gender.MALE,
            true),
    DOLL_SKIRT(R.string.qvri_doll_skirt_title,
            Declension.Gender.FEMALE,
            false),
    DOLL_BLOUSE(R.string.qvri_doll_blouse_title,
            Declension.Gender.FEMALE,
            false);

    @StringRes
    private int mTitleResourceId;
    @NonNull
    private Declension.Gender mGender;
    private boolean mIsPlural;

    LocalizedStringResourceCollection(@StringRes int titleResourceId,
                                      @NonNull Declension.Gender gender,
                                      boolean isPlural) {
        mTitleResourceId = titleResourceId;
        mGender = gender;
        mIsPlural = isPlural;
    }

    public Declension.Gender getGender() {
        return mGender;
    }

    public boolean getIsPlural() {
        return mIsPlural;
    }

    public String getName(@NonNull Context context) {
        return context.getString(mTitleResourceId);
    }

}