package ru.nekit.android.qls.pupil.avatar

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.view.ViewGroup
import ru.nekit.android.qls.shared.model.Pupil

object PupilAvatarViewBuilder {

    fun build(context: Context, pupil: Pupil, pupilAvatarContainer: ViewGroup) {
        val pupilAvatarPartAndVariants = PupilAvatarConverter.toPupilAvatarPartAndVariant(pupil)
        for (pupilAvatarPartAndVariant in pupilAvatarPartAndVariants) {
            val avatarPartImageRepresentation = AppCompatImageView(context)
            avatarPartImageRepresentation.setImageResource(
                    pupilAvatarPartAndVariant.pupilAvatarPart.variants[pupilAvatarPartAndVariant.variant])
            pupilAvatarContainer.addView(avatarPartImageRepresentation)
        }
    }
}
