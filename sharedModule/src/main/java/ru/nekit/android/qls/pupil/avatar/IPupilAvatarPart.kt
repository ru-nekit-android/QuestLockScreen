package ru.nekit.android.qls.pupil.avatar

import android.support.annotation.DimenRes
import android.support.annotation.DrawableRes

interface IPupilAvatarPart {

    @get:DimenRes
    val y: Int

    val name: String

    val dependentItems: Array<IPupilAvatarPart>?
    val pupilAvatarPartType: PupilAvatarPartType

    @get:DrawableRes
    val variants: IntArray

}
