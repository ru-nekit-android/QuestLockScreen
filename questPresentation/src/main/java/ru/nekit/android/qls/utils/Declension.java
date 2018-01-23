package ru.nekit.android.qls.utils;

import android.support.annotation.NonNull;

public class Declension {

    public static String declineAdjectiveByNoun(@NonNull String adjectiveBase,
                                                @NonNull String nounBase,
                                                @NonNull String format,
                                                @NonNull Gender gender,
                                                boolean isPlural) {
        return String.format(format, declineAdjective(adjectiveBase, gender, isPlural),
                declineNoun(nounBase, gender, isPlural));
    }

    public static String declineNoun(@NonNull String base, @NonNull Gender gender, boolean isPlural) {
        return base + (gender == Gender.NEUTER ? "о" : (isPlural ? "и" : "у"));
    }

    private static String declineAdjective(@NonNull String base, @NonNull Gender gender, boolean isPlural) {
        String ending = "";
        if (isPlural) {

            switch (gender) {

                case MALE:

                    ending = "ые";

                    break;

                case FEMALE:

                    ending = "ые";

                    break;

                case NEUTER:

                    ending = "ые";

                    break;

            }

        } else {

            switch (gender) {

                case MALE:

                    ending = "ой";

                    break;

                case FEMALE:

                    ending = "ую";

                    break;

                case NEUTER:

                    ending = "ое";

                    break;

            }
        }
        return base + ending;
    }

    public enum Gender {
        NEUTER,
        MALE,
        FEMALE
    }

}
