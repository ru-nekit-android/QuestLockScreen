package ru.nekit.android.qls.utils;

public class TimeUtils {

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static boolean isNewMinute() {
        return getCurrentTime() % 60000 == 0;
    }

    public static boolean isNewSecond() {
        return getCurrentTime() % 1000 == 0;
    }
}
