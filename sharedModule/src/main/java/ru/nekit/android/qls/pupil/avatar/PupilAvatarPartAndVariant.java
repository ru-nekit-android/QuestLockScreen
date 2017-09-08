package ru.nekit.android.qls.pupil.avatar;

import android.support.annotation.NonNull;

public class PupilAvatarPartAndVariant {

    public final IPupilAvatarPart pupilAvatarPart;
    public final int variant;

    public PupilAvatarPartAndVariant(@NonNull IPupilAvatarPart pupilAvatarPart, int variant) {
        this.pupilAvatarPart = pupilAvatarPart;
        this.variant = variant;
    }
}
