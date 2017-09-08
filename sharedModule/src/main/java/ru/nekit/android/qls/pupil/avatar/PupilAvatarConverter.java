package ru.nekit.android.qls.pupil.avatar;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilSex;

public class PupilAvatarConverter {

    private static final String PART_AND_VARIANT_DELIMITER = ":";
    private static final String DELIMITER = ";";

    public static String toString(@NonNull IPupilAvatarPart[] parts,
                                  @NonNull int[] partVariantPositions) {
        List<String> stringList = new ArrayList<>();
        int index = 0;
        for (IPupilAvatarPart pupilAvatarPart : parts) {
            stringList.add(String.format("%s%s%s", pupilAvatarPart.getPartName(),
                    PART_AND_VARIANT_DELIMITER, partVariantPositions[index]));
            index++;
        }
        return TextUtils.join(DELIMITER, stringList);
    }

    public static List<PupilAvatarPartAndVariant> toPupilAvatarPartAndVariant(
            @NonNull Pupil pupil) {
        List<PupilAvatarPartAndVariant> result = new ArrayList<>();
        if (pupil.avatar != null) {
            String[] pupilAvatarPartStrings = TextUtils.split(pupil.avatar, DELIMITER);
            for (String pupilAvatarPartString : pupilAvatarPartStrings) {
                String[] pupilAvatarPartStringValues =
                        TextUtils.split(pupilAvatarPartString, PART_AND_VARIANT_DELIMITER);
                IPupilAvatarPart[] pupilAvatarParts = pupil.sex == PupilSex.BOY ? PupilBoyAvatarPart.values()
                        : PupilGirlAvatarPart.values();
                for (IPupilAvatarPart pupilAvatarPart : pupilAvatarParts) {
                    if (pupilAvatarPart.getPartName().equals(pupilAvatarPartStringValues[0])) {
                        result.add(new PupilAvatarPartAndVariant(pupilAvatarPart,
                                Integer.parseInt(pupilAvatarPartStringValues[1])));
                        break;
                    }
                }

            }
        }
        return result;
    }
}