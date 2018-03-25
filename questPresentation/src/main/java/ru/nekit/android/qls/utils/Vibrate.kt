package ru.nekit.android.qls.utils

import android.content.Context
import android.os.Vibrator

object Vibrate {

    fun make(context: Context, time: Long) {
        (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(time)
    }

}
