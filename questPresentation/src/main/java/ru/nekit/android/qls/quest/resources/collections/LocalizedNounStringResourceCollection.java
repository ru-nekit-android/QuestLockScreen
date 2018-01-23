package ru.nekit.android.qls.quest.resources.collections;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.IStringHolder;
import ru.nekit.android.qls.utils.Declension;

public enum LocalizedNounStringResourceCollection implements IStringHolder {

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
            false),
    WINTER(R.string.qvri_winter_title,
            Declension.Gender.FEMALE,
            false),
    FALL(R.string.qvri_fall_title,
            Declension.Gender.FEMALE,
            false, new String[]{"ь"}),
    SPRING(R.string.qvri_spring_title,
            Declension.Gender.FEMALE,
            false),
    SUMMER(R.string.qvri_summer_title,
            Declension.Gender.NEUTER,
            false),
    ORANGE(R.string.qvri_orange_title,
            Declension.Gender.MALE,
            false, new String[]{""}),
    STRAWBERRY(R.string.qvri_strawberry_title,
            Declension.Gender.FEMALE,
            false),
    APPLE(R.string.qvri_apple_title,
            Declension.Gender.NEUTER,
            false),
    PEAR(R.string.qvri_pear_title,
            Declension.Gender.FEMALE,
            false),
    CHERRY(R.string.qvri_cherry_title,
            Declension.Gender.FEMALE,
            false, new String[]{"ю"}),
    RASPBERRY(R.string.qvri_raspberry_title,
            Declension.Gender.FEMALE, false),
    PINEAPPLE(R.string.qvri_pineapple_title,
            Declension.Gender.MALE,
            false,
            new String[]{""}),
    BLACKBERRY(R.string.qvri_blackberry_title,
            Declension.Gender.FEMALE,
            false);

    @StringRes
    private int mTitleResourceId;
    @NonNull
    private Declension.Gender mGender;
    private boolean mIsPlural;
    private String[] mOwnRule;

    LocalizedNounStringResourceCollection(@StringRes int titleResourceId,
                                          @NonNull Declension.Gender gender,
                                          boolean isPlural) {
        mTitleResourceId = titleResourceId;
        mGender = gender;
        mIsPlural = isPlural;
    }

    LocalizedNounStringResourceCollection(@StringRes int titleResourceId,
                                          @NonNull Declension.Gender gender,
                                          boolean isPlural, String[] ownRule) {
        mTitleResourceId = titleResourceId;
        mGender = gender;
        mIsPlural = isPlural;
        mOwnRule = ownRule;
    }

    public boolean hasOwnRule() {
        return mOwnRule != null && mOwnRule.length > 0;
    }

    public String[] getOwnRule() {
        return mOwnRule;
    }

    public Declension.Gender getGender() {
        return mGender;
    }

    public boolean getIsPlural() {
        return mIsPlural;
    }

    @NonNull
    public String getString(@NonNull Context context) {
        return context.getString(mTitleResourceId);
    }

}