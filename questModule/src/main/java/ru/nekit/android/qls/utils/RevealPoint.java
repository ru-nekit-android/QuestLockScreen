package ru.nekit.android.qls.utils;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;

public class RevealPoint extends Point {

    public static final String POSITION_TOP_LEFT = "POSITION_TOP_LEFT";
    public static final String POSITION_TOP_CENTER = "POSITION_TOP_CENTER";
    public static final String POSITION_TOP_RIGHT = "POSITION_TOP_RIGHT";

    public static final String POSITION_MIDDLE_LEFT = "POSITION_MIDDLE_LEFT";
    public static final String POSITION_MIDDLE_CENTER = "POSITION_MIDDLE_CENTER";
    public static final String POSITION_MIDDLE_RIGHT = "POSITION_MIDDLE_RIGHT";

    public static final String POSITION_BOTTOM_LEFT = "POSITION_BOTTOM_LEFT";
    public static final String POSITION_BOTTOM_CENTER = "POSITION_BOTTOM_CENTER";
    public static final String POSITION_BOTTOM_RIGHT = "POSITION_BOTTOM_RIGHT";

    public float radius;

    public static RevealPoint getRevealPoint(@NonNull Context context, String position) {
        RevealPoint revealPoint = new RevealPoint();
        Point screenSize = ScreenHost.getScreenSize(context);
        switch (position) {

            case POSITION_TOP_LEFT:

                revealPoint.x = 0;
                revealPoint.y = 0;
                revealPoint.radius = (float) Math.hypot(screenSize.x, screenSize.y);

                break;

            case POSITION_TOP_CENTER:

                revealPoint.x = screenSize.x / 2;
                revealPoint.y = 0;
                revealPoint.radius = screenSize.y;

                break;

            case POSITION_TOP_RIGHT:

                revealPoint.x = screenSize.x;
                revealPoint.y = 0;
                revealPoint.radius = (float) Math.hypot(screenSize.x, screenSize.y);

                break;

            case POSITION_MIDDLE_LEFT:

                revealPoint.x = 0;
                revealPoint.y = screenSize.y / 2;
                revealPoint.radius = (float) Math.hypot(screenSize.x, revealPoint.y);

                break;

            case POSITION_MIDDLE_CENTER:

                revealPoint.x = screenSize.x / 2;
                revealPoint.y = screenSize.y / 2;
                revealPoint.radius = (float) Math.hypot(revealPoint.x, revealPoint.y);

                break;

            case POSITION_MIDDLE_RIGHT:

                revealPoint.x = screenSize.x;
                revealPoint.y = screenSize.y / 2;
                revealPoint.radius = (float) Math.hypot(revealPoint.x, revealPoint.y);

                break;

            case POSITION_BOTTOM_LEFT:

                revealPoint.x = 0;
                revealPoint.y = screenSize.y;
                revealPoint.radius = (float) Math.hypot(revealPoint.x, revealPoint.y);

                break;

            case POSITION_BOTTOM_CENTER:

                revealPoint.x = screenSize.x / 2;
                revealPoint.y = screenSize.y;
                revealPoint.radius = screenSize.y;

                break;

            case POSITION_BOTTOM_RIGHT:

                revealPoint.x = screenSize.x;
                revealPoint.y = screenSize.y;
                revealPoint.radius = (float) Math.hypot(screenSize.x, revealPoint.y);

                break;

        }
        return revealPoint;
    }
}
