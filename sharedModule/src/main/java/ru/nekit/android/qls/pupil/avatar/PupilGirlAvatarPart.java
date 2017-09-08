package ru.nekit.android.qls.pupil.avatar;


import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.nekit.android.shared.R;

public enum PupilGirlAvatarPart implements IPupilAvatarPart {

    HAIR_BACKGROUND("hair_background",
            PupilAvatarPartType.FACE,
            0,
            new int[]{R.drawable.girl_hair_background1,
                    R.drawable.girl_hair_background2,
                    R.drawable.girl_hair_background3,
                    R.drawable.girl_hair_background4}),
    BODY("body",
            PupilAvatarPartType.BODY,
            R.dimen.girl_body_top,
            new int[]{R.drawable.girl_body1,
                    R.drawable.girl_body2}),
    BOOTS("boots",
            PupilAvatarPartType.BODY,
            R.dimen.girl_boots_top,
            new int[]{R.drawable.girl_boots1,
                    R.drawable.girl_boots2,
                    R.drawable.girl_boots3}),
    DRESS("dress",
            PupilAvatarPartType.BODY,
            R.dimen.girl_dress_top,
            new int[]{
                    R.drawable.girl_dress1,
                    R.drawable.girl_dress2,
                    R.drawable.girl_dress3,
                    R.drawable.girl_dress4,
            }),
    EYEBROW("eyebrow",
            PupilAvatarPartType.FACE,
            R.dimen.girl_eyebrow_top,
            new int[]{R.drawable.girl_eyebrow1,
                    R.drawable.girl_eyebrow2,
                    R.drawable.girl_eyebrow3,
                    R.drawable.girl_eyebrow4
            }),
    HAIR("hair",
            PupilAvatarPartType.FACE,
            R.dimen.girl_hair_top,
            new int[]{
                    R.drawable.girl_hair1,
                    R.drawable.girl_hair2,
                    R.drawable.girl_hair3,
                    R.drawable.girl_hair4,
            }, HAIR_BACKGROUND),
    MOUTH("mouth",
            PupilAvatarPartType.FACE,
            R.dimen.girl_mouth_top,
            new int[]{R.drawable.girl_mouth1,
                    R.drawable.girl_mouth2,
                    R.drawable.girl_mouth3,
                    R.drawable.girl_mouth4}),
    NOSE("nose",
            PupilAvatarPartType.FACE,
            R.dimen.girl_nose_top,
            new int[]{R.drawable.girl_nose1,
                    R.drawable.girl_nose2,
                    R.drawable.girl_nose3,
                    R.drawable.girl_nose4
            }),
    EYE("eye",
            PupilAvatarPartType.FACE,
            R.dimen.girl_eye_top,
            new int[]{R.drawable.girl_eye1,
                    R.drawable.girl_eye2,
                    R.drawable.girl_eye3,
                    R.drawable.girl_eye4
            });

    @NonNull
    private final PupilAvatarPartType pupilAvatarPartType;
    @DrawableRes
    public int[] variants;
    @DimenRes
    public int y;
    @NonNull
    private String partName;
    private PupilGirlAvatarPart[] dependentItems;

    PupilGirlAvatarPart(@NonNull String partName, @NonNull PupilAvatarPartType pupilAvatarPartType,
                        @DimenRes int y, @DrawableRes int[] variants) {
        this.partName = partName;
        this.pupilAvatarPartType = pupilAvatarPartType;
        this.variants = variants;
        this.y = y;
    }

    PupilGirlAvatarPart(@NonNull String partName, @NonNull PupilAvatarPartType pupilAvatarPartType,
                        @DimenRes int y, @DrawableRes int[] variants,
                        PupilGirlAvatarPart... dependentItems) {
        this(partName, pupilAvatarPartType, y, variants);
        this.dependentItems = dependentItems;
    }

    @NonNull
    @Override
    public String getPartName() {
        return partName;
    }

    @Override
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
        return dependentItems;
    }

    @Override
    @NonNull
    public PupilAvatarPartType getPupilAvatarPartType() {
        return pupilAvatarPartType;
    }
}