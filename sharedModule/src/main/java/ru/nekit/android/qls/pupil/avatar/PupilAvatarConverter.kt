package ru.nekit.android.qls.pupil.avatar

import android.text.TextUtils
import ru.nekit.android.qls.shared.model.Pupil
import ru.nekit.android.qls.shared.model.PupilSex
import java.util.*

class PupilAvatarConverter {

    companion object {

        private val PART_AND_VARIANT_DELIMITER = ":"
        private val DELIMITER = ";"

        fun toString(parts: Array<IPupilAvatarPart>,
                     partVariantPositions: IntArray): String {
            val stringList = ArrayList<String>()
            var index = 0
            for (pupilAvatarPart in parts) {
                stringList.add(String.format("%s%s%s", pupilAvatarPart.name,
                        PART_AND_VARIANT_DELIMITER, partVariantPositions[index]))
                index++
            }
            return TextUtils.join(DELIMITER, stringList)
        }

        fun toPupilAvatarPartAndVariant(pupil: Pupil): List<PupilAvatarPartAndVariant> {
            val result = ArrayList<PupilAvatarPartAndVariant>()
            if (pupil.avatar != null) {
                val pupilAvatarPartStrings = TextUtils.split(pupil.avatar, DELIMITER)
                for (pupilAvatarPartString in pupilAvatarPartStrings) {
                    val pupilAvatarPartStringValues = TextUtils.split(pupilAvatarPartString, PART_AND_VARIANT_DELIMITER)
                    val pupilAvatarParts = (if (pupil.sex == PupilSex.BOY)
                        PupilBoyAvatarPart.values()
                    else
                        PupilGirlAvatarPart.values()) as Array<IPupilAvatarPart>
                    for (pupilAvatarPart in pupilAvatarParts) {
                        if (pupilAvatarPart.name == pupilAvatarPartStringValues[0]) {
                            result.add(PupilAvatarPartAndVariant(pupilAvatarPart,
                                    Integer.parseInt(pupilAvatarPartStringValues[1])))
                            break
                        }
                    }

                }
            }
            return result
        }
    }
}