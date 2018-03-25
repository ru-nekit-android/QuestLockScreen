package ru.nekit.android.qls.utils

import android.app.ActivityManager
import android.content.Context

object ActivityUtils {

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE).any { serviceClass.name == it.service.className }
    }
}
