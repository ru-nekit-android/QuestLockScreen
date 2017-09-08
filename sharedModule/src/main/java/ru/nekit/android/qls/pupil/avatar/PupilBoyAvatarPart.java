package ru.nekit.android.qls.pupil.avatar;


import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.nekit.android.shared.R;

public enum PupilBoyAvatarPart implements IPupilAvatarPart {

    BODY("body",
            PupilAvatarPartType.BODY,
            0,
            new int[]{R.drawable.man_body}),
    BOOTS("boots",
            PupilAvatarPartType.BODY,
            R.dimen.boy_boots_top,
            new int[]{R.drawable.man_boots1,
                    R.drawable.man_boots2,
                    R.drawable.man_boots3}),
    PANTS("pants",
            PupilAvatarPartType.BODY,
            R.dimen.boy_pants_top,
            new int[]{R.drawable.man_pants_1,
                    R.drawable.man_pants_2,
                    R.drawable.man_pants_3}),
    T_SHIRT("t_shirt",
            PupilAvatarPartType.BODY,
            R.dimen.boy_tshirt_top,
            new int[]{R.drawable.man_tshirt_1,
                    R.drawable.man_tshirt_2,
                    R.drawable.man_tshirt_3}),
    MOUTH("mouth",
            PupilAvatarPartType.FACE,
            R.dimen.boy_mouth_top,
            new int[]{R.drawable.man_mouth1,
                    R.drawable.man_mouth2,
                    R.drawable.man_mouth3,
                    R.drawable.man_mouth4}),
    NOSE("nose",
            PupilAvatarPartType.FACE,
            R.dimen.boy_nose_top,
            new int[]{R.drawable.man_nose1,
                    R.drawable.man_nose2,
                    R.drawable.man_nose3}),
    EYE("eye", PupilAvatarPartType.FACE,
            R.dimen.boy_eye_top,
            new int[]{R.drawable.man_eye1,
                    R.drawable.man_eye2,
                    R.drawable.man_eye3,
                    R.drawable.man_eye4
            }),
    EYEBROW("eyebrow",
            PupilAvatarPartType.FACE,
            R.dimen.boy_eyebrow_top,
            new int[]{R.drawable.man_eyebrow1,
                    R.drawable.man_eyebrow2
            }),
    HAIR("hair",
            PupilAvatarPartType.FACE,
            R.dimen.boy_hair_top,
            new int[]{R.drawable.man_hair_1,
                    R.drawable.man_hair_2,
                    R.drawable.man_hair_3,
                    R.drawable.man_hair_4,
            });

    @DrawableRes
    public final int[] variants;
    @DimenRes
    public final int y;
    @NonNull
    private final PupilAvatarPartType pupilAvatarPartType;
    @NonNull
    private final String partName;

    PupilBoyAvatarPart(@NonNull String partName, @NonNull PupilAvatarPartType pupilAvatarPartType,
                       @DimenRes int y, @DrawableRes int[] variants) {
        this.partName = partName;
        this.pupilAvatarPartType = pupilAvatarPartType;
        this.variants = variants;
        this.y = y;
    }

    @NonNull
    @Override
    public String getPartName() {
        return partName;
    }

    public int getVariant(int position) {
        return variants[position];
    }

    public int getVariantCount() {
        return variants.length;
    }

    @DimenRes
    @Override
    public int getY() {
        return y;
    }

    @Nullable
    @Override
    public PupilGirlAvatarPart[] getDependentItems() {
        return null;
    }

    @Override
    @NonNull
    public PupilAvatarPartType getPupilAvatarPartType() {
        return pupilAvatarPartType;
    }
}