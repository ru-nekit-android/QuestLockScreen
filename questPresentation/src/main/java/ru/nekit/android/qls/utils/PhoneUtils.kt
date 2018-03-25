package ru.nekit.android.qls.utils

import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager

object PhoneUtils {

    fun isPhoneIdle(context: Context): Boolean {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.callState == TelephonyManager.CALL_STATE_IDLE
    }

    fun pinOrPukCodeRequired(context: Context): Boolean {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.simState == TelephonyManager.SIM_STATE_PIN_REQUIRED || telephonyManager.simState == TelephonyManager.SIM_STATE_PUK_REQUIRED
    }

    fun phoneIsAvailable(context: Context): Boolean {
        val packageManager = context.packageManager
        return packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
    }
}