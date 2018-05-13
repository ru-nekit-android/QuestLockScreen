package ru.nekit.android.qls.lockScreen.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.nekit.android.qls.lockScreen.LockScreen

class BootCompletedReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        LockScreen.getInstance().startOnBootComplete()
    }
}