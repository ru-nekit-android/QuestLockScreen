package ru.nekit.android.qls.quest.types;

/**
 * Created by nekit on 23.03.17.
 */

public enum TrafficLightType {

    GREEN,
    YELLOW,
    RED;

    public static TrafficLightType fromOrdinal(int ordinal) {
        return values()[ordinal];
    }

}
