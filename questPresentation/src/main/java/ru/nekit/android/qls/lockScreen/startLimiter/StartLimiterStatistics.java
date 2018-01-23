package ru.nekit.android.qls.lockScreen.startLimiter;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.PreferencesUtil;
import ru.nekit.android.qls.utils.TimeUtils;

public class StartLimiterStatistics {

    private static final String SCREEN_OFF_TIME = "screen_off_time";
    private static final String SCREEN_OFF_LIFE_TIME = "screen_off_life_time";
    private static final String SCREEN_ON_TIME = "screen_on_time";
    private static final String SCREEN_ON_LIFE_TIME = "screen_on_life_time";
    private static final String RIGHT_ANSWER_COUNT = "right_answer_count";

    public StartLimiterStatistics(@NonNull Context context) {
        PreferencesUtil.init(context);
    }

    public void updateScreenOnLifeTime() {
        long currentTime = TimeUtils.getCurrentTime();
        long onTime = getScreenOnTime();
        long onLifeTime = getScreenOnLifeTime();
        if (onTime == 0) {
            onLifeTime = 0;
        } else {
            onLifeTime += currentTime - onTime;
        }
        setScreenOnLifeTime(onLifeTime);
        setScreenOffTime(currentTime);
    }

    public void updateScreenOffLifeTime() {
        long currentTime = TimeUtils.getCurrentTime();
        long offTime = getScreenOffTime();
        long offLifeTime = getScreenOffLifeTime();
        if (offTime == 0) {
            offLifeTime = 0;
        } else {
            offLifeTime += currentTime - offTime;
        }
        setScreenOffLifeTime(offLifeTime);
        setScreenOnTime(currentTime);
    }

    long getScreenOnLifeTime() {
        return PreferencesUtil.getLong(SCREEN_ON_LIFE_TIME);
    }

    private void setScreenOnLifeTime(long value) {
        PreferencesUtil.setLong(SCREEN_ON_LIFE_TIME, value);
    }

    long getScreenOffLifeTime() {
        return PreferencesUtil.getLong(SCREEN_OFF_LIFE_TIME);
    }

    private void setScreenOffLifeTime(long value) {
        PreferencesUtil.setLong(SCREEN_OFF_LIFE_TIME, value);
    }

    private long getScreenOnTime() {
        return PreferencesUtil.getLong(SCREEN_ON_TIME);
    }

    private void setScreenOnTime(long value) {
        PreferencesUtil.setLong(SCREEN_ON_TIME, value);
    }

    private long getScreenOffTime() {
        return PreferencesUtil.getLong(SCREEN_OFF_TIME);
    }

    private void setScreenOffTime(long value) {
        PreferencesUtil.setLong(SCREEN_OFF_TIME, value);
    }

    private int getRightAnswerCount() {
        return PreferencesUtil.getInt(RIGHT_ANSWER_COUNT);
    }

    private void setRightAnswerCount(int value) {
        PreferencesUtil.setInt(RIGHT_ANSWER_COUNT, value);
    }

    void setRightAnswerCountIfEqualZero(int value) {
        if (getRightAnswerCount() == 0) {
            setRightAnswerCount(value);
        }
    }

    public void updateRightAnswerCount() {
        if (getRightAnswerCount() == 0) {
            setScreenOnLifeTime(0);
            setScreenOnTime(0);
            setScreenOffTime(0);
            setScreenOffLifeTime(0);
        } else {
            setRightAnswerCount(getRightAnswerCount() - 1);
        }
    }
}