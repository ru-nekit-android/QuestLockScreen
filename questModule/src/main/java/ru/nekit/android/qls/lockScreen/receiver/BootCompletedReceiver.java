package ru.nekit.android.qls.lockScreen.receiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import ru.nekit.android.qls.PreferencesUtil;
import ru.nekit.android.qls.lockScreen.LockScreen;
import ru.nekit.android.qls.utils.PhoneManager;
import ru.nekit.android.qls.utils.TimeUtils;

import static ru.nekit.android.qls.lockScreen.LockScreen.LockScreenStartType.ON_BOOT_COMPLETE;

public class BootCompletedReceiver extends WakefulBroadcastReceiver {

    public BootCompletedReceiver() {
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        PreferencesUtil.init(context);
        if (!PhoneManager.pinOrPukCodeRequired(context)) {
            startWakefulService(context, LockScreen.getActivationIntent(context, ON_BOOT_COMPLETE));
        } else {
            AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarms != null) {
                Intent intentToFire = new Intent(context, getClass());
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
                        0, intentToFire, 0);
                alarms.set(AlarmManager.RTC_WAKEUP,
                        TimeUtils.getCurrentTime() + 10000, alarmIntent);
            }
        }
    }
}