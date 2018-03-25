package ru.nekit.android.qls.lockScreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import ru.nekit.android.qls.lockScreen.LockScreen

class AlarmClockReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        LockScreen.hide(context)
    }
}
