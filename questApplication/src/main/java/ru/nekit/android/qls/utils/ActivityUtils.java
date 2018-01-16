package ru.nekit.android.qls.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by nekit on 20.02.17.
 */

public class ActivityUtils {

    public static boolean isServiceRunning(@NonNull Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
