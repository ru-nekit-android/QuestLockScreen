package ru.nekit.android.qls.lockScreen;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.PreferencesUtil;
import ru.nekit.android.qls.lockScreen.service.LockScreenService;
import ru.nekit.android.qls.session.Session;
import ru.nekit.android.qls.session.SessionType;
import ru.nekit.android.qls.utils.ActivityUtils;

import static ru.nekit.android.qls.lockScreen.startLimiter.StartLimiter.Flags.LIMITLESS;

public class LockScreen {

    /*
    on
    active
    show
     */

    private static final String LOCK_IS_ON = "ru.nekit.android.qls.lock_state";
    private static final String START_TYPE = "ru.nekit.android.qls.start_type";
    private static final String UNLOCK_SECRET = "ru.nekit.android.qls.unlock_secret_v2";

    //active:LockScreenService
    public static boolean isActive(@NonNull Context context) {
        return ActivityUtils.isServiceRunning(context, LockScreenService.class);
    }

    public static void activate(@NonNull Context context,
                                @NonNull LockScreenStartType startType) {
        if (startType != LockScreenStartType.SETUP_WIZARD) {
            switchOn();
        }
        PreferencesUtil.setString(START_TYPE, startType.name());
        context.startService(getActivationIntent(context, startType));
    }

    public static Intent getActivationIntent(@NonNull Context context,
                                             @NonNull LockScreenStartType startType) {
        Intent intentService = new Intent(context, LockScreenService.class);
        intentService.setFlags(startType.ordinal());
        return intentService;
    }

    public static void activateForSetupWizard(@NonNull Context context) {
        if (!isActive(context)) {
            activate(context, LockScreenStartType.SETUP_WIZARD);
        }
    }

    //on:PreferencesUtil
    public static boolean isOn() {
        return PreferencesUtil.getBoolean(LOCK_IS_ON);
    }

    public static void switchOn() {
        PreferencesUtil.setBoolean(LOCK_IS_ON, true);
    }

    public static void switchOff(@NonNull Context context) {
        PreferencesUtil.setBoolean(LOCK_IS_ON, false);
        hide(context);
        context.stopService(new Intent(context, LockScreenService.class));
    }

    public static void hide(@NonNull Context context) {
        context.sendBroadcast(new Intent(LockScreenService.ACTION_HIDE_LOCK_SCREEN_VIEW));
    }

    public static void show(@NonNull Context context) {
        activate(context, LockScreenStartType.EXPLICIT);
    }

    //support
    public static boolean tryToLogin(@NonNull Context context, @NonNull String unlockSecret) {
        boolean result = getUnlockSecret(context).equals(unlockSecret);
        if (result) {
            for (SessionType sessionType : SessionType.values()) {
                Session.startSession(context, sessionType);
            }
        }
        return result;
    }

    public static void setUnlockSecret(@NonNull Context context, @NonNull String unlockSecret) {
        PreferencesUtil.setString(UNLOCK_SECRET, unlockSecret);
        for (SessionType sessionType : SessionType.values()) {
            Session.startSession(context, sessionType);
        }
    }

    public static String getUnlockSecret(@NonNull Context context) {
        return PreferencesUtil.getString(UNLOCK_SECRET);
    }

    public static boolean isStandardKeyguardState(@NonNull Context context) {
        boolean isStandardKeyguard = false;
        KeyguardManager keyManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (null != keyManager) {
            isStandardKeyguard = keyManager.isKeyguardSecure();
        }
        return isStandardKeyguard;
    }

    public static LockScreenStartType getStartType() {
        return LockScreenStartType.valueOf(PreferencesUtil.getString(START_TYPE));
    }

    public enum LockScreenStartType {

        SETUP_WIZARD(LIMITLESS),
        SILENCE(LIMITLESS),
        EXPLICIT(LIMITLESS),
        ON_SCREEN_OFF(LIMITLESS),
        ON_BOOT_COMPLETE(LIMITLESS),
        ON_INCOME_CALL_COMPLETE(LIMITLESS),
        ON_OUTGOING_CALL_COMPLETE(LIMITLESS),
        ON_DESTROY(LIMITLESS),
        ON_NOTIFICATION_CLICK(LIMITLESS);

        public static final String NAME = "LockScreenStartType";
        private int mFlags;

        LockScreenStartType(int flags) {
            mFlags = flags;
        }

        public static LockScreenStartType fromOrdinal(int ordinal) {
            return LockScreenStartType.values()[ordinal];
        }

        public int getFlags() {
            return mFlags;
        }
    }
}