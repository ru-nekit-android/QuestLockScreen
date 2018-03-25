package ru.nekit.android.qls.pupil.avatar


import android.support.annotation.DimenRes
import android.support.annotation.DrawableRes

import ru.nekit.android.shared.R

enum class PupilGirlAvatarPart(override val pupilAvatarPartType: PupilAvatarPartType,
                               @get:DimenRes
                               override var y: Int,
                               @get:DrawableRes
                               override var variants: IntArray,
                               override var dependentItems: Array<IPupilAvatarPart>? = null) : IPupilAvatarPart {

    HAIR_BACKGROUND(PupilAvatarPartType.FACE,
            0,
            intArrayOf(R.drawable.girl_hair_background1,
                    R.drawable.girl_hair_background2,
                    R.drawable.girl_hair_background3,
                    R.drawable.girl_hair_background4)),

    BODY(PupilAvatarPartType.BODY,
            R.dimen.girl_body_top,
            intArrayOf(R.drawable.girl_body1, R.drawable.girl_body2)),

    BOOTS(PupilAvatarPartType.BODY,
            R.dimen.girl_boots_top,
            intArrayOf(R.drawable.girl_boots1,
                    R.drawable.girl_boots2,
                    R.drawable.girl_boots3)),

    DRESS(PupilAvatarPartType.BODY,
            R.dimen.girl_dress_top,
            intArrayOf(R.drawable.girl_dress1,
                    R.drawable.girl_dress2,
                    R.drawable.girl_dress3,
                    R.drawable.girl_dress4)),

    EYEBROW(PupilAvatarPartType.FACE,
            R.dimen.girl_eyebrow_top,
            intArrayOf(R.drawable.girl_eyebrow1,
                    R.drawable.girl_eyebrow2,
                    R.drawable.girl_eyebrow3,
                    R.drawable.girl_eyebrow4)),

    HAIR(PupilAvatarPartType.FACE,
            R.dimen.girl_hair_top,
            intArrayOf(R.drawable.girl_hair1,
                    R.drawable.girl_hair2,
                    R.drawable.girl_hair3,
                    R.drawable.girl_hair4),
            arrayOf(HAIR_BACKGROUND)),

    MOUTH(PupilAvatarPartType.FACE,
            R.dimen.girl_mouth_top,
            intArrayOf(R.drawable.girl_mouth1,
                    R.drawable.girl_mouth2,
                    R.drawable.girl_mouth3,
                    R.drawable.girl_mouth4)),

    NOSE(PupilAvatarPartType.FACE,
            R.dimen.girl_nose_top,
            intArrayOf(R.drawable.girl_nose1,
                    R.drawable.girl_nose2,
                    R.drawable.girl_nose3,
                    R.drawable.girl_nose4)),

    EYE(PupilAvatarPartType.FACE,
            R.dimen.girl_eye_top,
            intArrayOf(R.drawable.girl_eye1,
                    R.drawable.girl_eye2,
                    R.drawable.girl_eye3,
                    R.drawable.girl_eye4));

}