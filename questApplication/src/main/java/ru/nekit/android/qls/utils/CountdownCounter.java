package ru.nekit.android.qls.utils;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.PreferencesUtil;

public class CountdownCounter {

    @NonNull
    private final String mName;

    public CountdownCounter(@NonNull String name, int startValue) {
        mName = name;
        if (getStartValue() != startValue) {
            setStartValue(startValue);
            reset();
        }
    }

    private static String COUNTDOWN_COUNTER_VALUE(@NonNull String name) {
        return String.format("countdown_counter.value.%s", name);
    }

    private static String COUNTDOWN_COUNTER_START_VALUE(@NonNull String name) {
        return String.format("countdown_counter.start_value.%s", name);
    }

    private int getStartValue() {
        return PreferencesUtil.getInt(COUNTDOWN_COUNTER_START_VALUE(mName));
    }

    private void setStartValue(int value) {
        PreferencesUtil.setInt(COUNTDOWN_COUNTER_START_VALUE(mName), value);
    }

    public void reset() {
        setCounterValue(getStartValue());
    }

    private void setCounterValue(int value) {
        PreferencesUtil.setInt(COUNTDOWN_COUNTER_VALUE(mName), value);
    }

    public boolean zeroIsReached() {
        int counterValue = PreferencesUtil.getInt(COUNTDOWN_COUNTER_VALUE(mName), getStartValue());
        return counterValue == 0;
    }

    public int getValue() {
        return PreferencesUtil.getInt(COUNTDOWN_COUNTER_VALUE(mName), getStartValue());
    }

    public void setValue(int value) {
        PreferencesUtil.getInt(COUNTDOWN_COUNTER_VALUE(mName), value);
    }

    public void countDown() {
        count(-1);
    }

    public void countDownUp() {
        count(1);
    }

    private void count(int value) {
        int counterValue = PreferencesUtil.getInt(COUNTDOWN_COUNTER_VALUE(mName), getStartValue());
        counterValue += value;
        counterValue = Math.max(0, counterValue);
        PreferencesUtil.setInt(COUNTDOWN_COUNTER_VALUE(mName), counterValue);

    }


}