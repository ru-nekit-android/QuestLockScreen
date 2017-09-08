package ru.nekit.android.qls.pupil.avatar;

import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface IPupilAvatarPart {

    int getVariant(int position);

    int getVariantCount();

    @DimenRes
    int getY();

    @NonNull
    String getPartName();

    @Nullable
    IPupilAvatarPart[] getDependentItems();

    @NonNull
    PupilAvatarPartType getPupilAvatarPartType();

}
