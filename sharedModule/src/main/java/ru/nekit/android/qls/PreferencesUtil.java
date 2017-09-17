package ru.nekit.android.qls;

import android.content.Context;
import android.support.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

public class PreferencesUtil {

    private static final Object mLock = new Object();
    private static LockScreenPreferenceModule mPref = null;

    public static void init(@NonNull Context context) {
        synchronized (mLock) {
            if (mPref == null) {
                mPref = new LockScreenPreferenceModule(context);
            }
        }
    }

    public static void setString(@NonNull String key, String value) {
        mPref.put(key, value);
    }

    public static void remove(@NonNull String key) {
        mPref.remove(key);
    }

    public static String getString(@NonNull String key) {
        return mPref.getString(key, null);
    }

    public static void setInt(@NonNull String key, int value) {
        mPref.put(key, value);
    }

    public static void setLong(@NonNull String key, long value) {
        mPref.put(key, value);
    }

    public static int getInt(@NonNull String key) {
        return mPref.getInt(key, 0);
    }

    public static long getLong(@NonNull String key) {
        return mPref.getLong(key, 0);
    }

    public static int getInt(@NonNull String key, int defaultValue) {
        return mPref.getInt(key, defaultValue);
    }

    public static float getFloat(@NonNull String key, float defaultValue) {
        return mPref.getFloat(key, defaultValue);
    }

    public static void setFloat(@NonNull String key, float value) {
        mPref.put(key, value);
    }

    public static void setBoolean(@NonNull String key, boolean value) {
        mPref.put(key, value);
    }

    public static boolean getBoolean(@NonNull String key) {
        return mPref.getBoolean(key, false);
    }

    public static boolean getBoolean(@NonNull String key, boolean defaultValue) {
        return mPref.getBoolean(key, defaultValue);
    }
}

class LockScreenPreferenceModule extends TrayPreferences {

    private static final String NAME = "LOCK_SCREEN";

    LockScreenPreferenceModule(@NonNull final Context context) {
        super(context, NAME, 1);
    }
}
