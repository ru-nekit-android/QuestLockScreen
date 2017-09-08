package ru.nekit.android.qls.parentControl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class BootCompletedReceiver extends WakefulBroadcastReceiver {

    public BootCompletedReceiver() {
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    public void onReceive(Context context, Intent intent) {
        startWakefulService(context, ParentControlService.getStartIntent(context));
    }
}