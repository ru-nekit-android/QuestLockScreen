package ru.nekit.android.qls.quest.model;

public enum TrafficLightModel {

    GREEN,
    YELLOW,
    RED;

    public static TrafficLightModel fromOrdinal(int ordinal) {
        return values()[ordinal];
    }

}
