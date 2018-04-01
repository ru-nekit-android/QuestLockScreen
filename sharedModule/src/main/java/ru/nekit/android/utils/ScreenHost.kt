package ru.nekit.android.utils

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.PowerManager
import android.view.WindowManager

object ScreenHost {

    fun isScreenOn(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            /*val dm = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            var screenOn = false
            for (display in dm.displays) {
                if (display.state != Display.STATE_OFF) {
                    screenOn = true
                }
            }
            screenOn*/
            pm.isInteractive
        } else {
            @Suppress("DEPRECATION")
            pm.isScreenOn
        }
    }

    fun getScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val screenPoint = Point()
        windowManager.defaultDisplay.getSize(screenPoint)
        return screenPoint
    }
}