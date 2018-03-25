package ru.nekit.android.qls.lockScreen.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.nekit.android.qls.domain.model.LockScreenStartType
import ru.nekit.android.qls.lockScreen.LockScreen

class BootCompletedReceiver : BroadcastReceiver() {

    private var wasReceived: Boolean = false

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        if (!wasReceived) {
            wasReceived = true
            LockScreen.activeIfOn(context, LockScreenStartType.ON_BOOT_COMPLETE)
        }
    }
}