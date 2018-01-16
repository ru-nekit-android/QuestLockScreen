package ru.nekit.android.qls.session;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.PreferencesUtil;
import ru.nekit.android.qls.utils.TimeUtils;

public class Session {

    private static final String NAME = "session";

    private static String getFullSessionName(@NonNull String name) {
        return String.format("%s.%s", NAME, name);
    }

    public static void startSession(@NonNull SessionType sessionType) {
        PreferencesUtil.setLong(getFullSessionName(sessionType.getName()),
                TimeUtils.getCurrentTime());
    }

    public static boolean isValid(@NonNull SessionType sessionType) {
        long sessionTime = PreferencesUtil.getLong(getFullSessionName(sessionType.getName()));
        return sessionTime != 0
                && (TimeUtils.getCurrentTime() - sessionTime) <= sessionType.getExpiredTime();
    }

    public static void reset(@NonNull SessionType sessionType) {
        PreferencesUtil.setLong(getFullSessionName(sessionType.getName()), 0);
    }
}
