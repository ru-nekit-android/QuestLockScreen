package ru.nekit.android.qls.utils;

import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;

public class Vibrate {

    public static void make(@NonNull Context context, long time) {
        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(time);
    }

}
