package ru.nekit.android.qls.quest.model;

/**
 * Created by nekit on 23.03.17.
 */

public enum TrafficLightModel {

    GREEN,
    YELLOW,
    RED;

    public static TrafficLightModel fromOrdinal(int ordinal) {
        return values()[ordinal];
    }

}
