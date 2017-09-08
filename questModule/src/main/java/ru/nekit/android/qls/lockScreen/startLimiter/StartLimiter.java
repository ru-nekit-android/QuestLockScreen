package ru.nekit.android.qls.lockScreen.startLimiter;

import ru.nekit.android.qls.lockScreen.LockScreen;

public class StartLimiter {

    private static final int LIMIT_LIFE_TIME_VALUE = 15000;

    public static boolean isLimit(StartLimiterStatistics startLimiterStatistics, LockScreen.LockScreenStartType startType) {
        boolean isLimit;
        int startTypeFlags = startType.getFlags();
        if ((startTypeFlags & Flags.LIMITLESS) != 0) {
            isLimit = false;
        } else {
            long lifeTime = 0;
            if ((startTypeFlags & Flags.LIMIT_BY_SCREEN_ON_LIFE_TIME) != 0) {
                lifeTime += startLimiterStatistics.getScreenOnLifeTime();
            }
            if ((startTypeFlags & Flags.LIMIT_BY_SCREEN_OFF_LIFE_TIME) != 0) {
                lifeTime += startLimiterStatistics.getScreenOffLifeTime();
            }
            if ((startTypeFlags & Flags.REPEAT_WHILE_COUNT_OF_RIGHT_ANSWERS_NOT_REACH_MAGIC_VALUE) != 0) {
                startLimiterStatistics.setRightAnswerCountIfEqualZero((int) lifeTime / LIMIT_LIFE_TIME_VALUE);
            }
            isLimit = lifeTime < LIMIT_LIFE_TIME_VALUE;
        }
        return isLimit;
    }

    public static class Flags {

        public static final int LIMITLESS = 1;
        public static final int LIMIT_BY_SCREEN_ON_LIFE_TIME = 2;
        public static final int LIMIT_BY_SCREEN_OFF_LIFE_TIME = 4;
        public static final int REPEAT_WHILE_COUNT_OF_RIGHT_ANSWERS_NOT_REACH_MAGIC_VALUE = 8;
    }
}