package ru.nekit.android.qls.utils;

import android.support.annotation.NonNull;

public class NameUtil {

    public static String getNameByClass(@NonNull Object classItem, @NonNull String suffix) {
        return String.format("%s.%s", classItem.getClass().getCanonicalName(), suffix);
    }

}
