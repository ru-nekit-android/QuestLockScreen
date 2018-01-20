package ru.nekit.android.qls.lockScreen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.nekit.android.qls.lockScreen.LockScreen;

public class AlarmClockReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LockScreen.hide(context);
    }
}
