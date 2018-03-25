package ru.nekit.android.qls.pupil.avatar

import android.support.annotation.DimenRes
import android.support.annotation.DrawableRes
import ru.nekit.android.shared.R

enum class PupilBoyAvatarPart(override var pupilAvatarPartType: PupilAvatarPartType,
                              @get:DimenRes
                              override var y: Int,
                              @get:DrawableRes
                              override var variants: IntArray) : IPupilAvatarPart {

    BODY(PupilAvatarPartType.BODY,
            0,
            intArrayOf(R.drawable.man_body)),
    BOOTS(PupilAvatarPartType.BODY,
            R.dimen.boy_boots_top,
            intArrayOf(R.drawable.man_boots1,
                    R.drawable.man_boots2,
                    R.drawable.man_boots3)),
    PANTS(PupilAvatarPartType.BODY,
            R.dimen.boy_pants_top,
            intArrayOf(R.drawable.man_pants_1,
                    R.drawable.man_pants_2,
                    R.drawable.man_pants_3)),
    T_SHIRT(PupilAvatarPartType.BODY,
            R.dimen.boy_tshirt_top,
            intArrayOf(R.drawable.man_tshirt_1,
                    R.drawable.man_tshirt_2,
                    R.drawable.man_tshirt_3)),
    MOUTH(PupilAvatarPartType.FACE,
            R.dimen.boy_mouth_top,
            intArrayOf(R.drawable.man_mouth1,
                    R.drawable.man_mouth2,
                    R.drawable.man_mouth3,
                    R.drawable.man_mouth4)),
    NOSE(PupilAvatarPartType.FACE,
            R.dimen.boy_nose_top,
            intArrayOf(R.drawable.man_nose1,
                    R.drawable.man_nose2,
                    R.drawable.man_nose3)),
    EYE(PupilAvatarPartType.FACE,
            R.dimen.boy_eye_top,
            intArrayOf(R.drawable.man_eye1,
                    R.drawable.man_eye2,
                    R.drawable.man_eye3,
                    R.drawable.man_eye4)),
    EYEBROW(PupilAvatarPartType.FACE,
            R.dimen.boy_eyebrow_top,
            intArrayOf(R.drawable.man_eyebrow1,
                    R.drawable.man_eyebrow2)),
    HAIR(PupilAvatarPartType.FACE,
            R.dimen.boy_hair_top,
            intArrayOf(R.drawable.man_hair_1,
                    R.drawable.man_hair_2,
                    R.drawable.man_hair_3,
                    R.drawable.man_hair_4));

    override val dependentItems: Array<IPupilAvatarPart>? = null

}