package ru.nekit.android.utils

import android.content.Context
import android.os.Vibrator

//TODO: resolve a deprecation for vibrate function
object Vibrate {

    fun make(context: Context, time: Long) {
        (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(time)
    }

}
