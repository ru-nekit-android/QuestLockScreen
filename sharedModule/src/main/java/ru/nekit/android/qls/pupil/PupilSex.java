package ru.nekit.android.qls.pupil;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import ru.nekit.android.qls.quest.ITitleable;
import ru.nekit.android.shared.R;

public enum PupilSex implements ITitleable {

    GIRL(R.string.pupil_sex_girl_title),
    BOY(R.string.pupil_sex_boy_title);

    private final int mTitleResourceId;

    PupilSex(@StringRes int titleResourceId) {
        mTitleResourceId = titleResourceId;
    }

    @Override
    public String getTitle(@NonNull Context context) {
        return context.getResources().getString(mTitleResourceId);
    }
}
