package ru.nekit.android.qls

import android.util.Log
import com.google.firebase.crash.FirebaseCrash
import ru.nekit.android.qls.domain.providers.ILogger

class Logger : ILogger {

    override fun d(message: String) {
        Log.d(TAG, message)
        FirebaseCrash.log(message)
    }

    override fun e(message: String) {
        Log.e(TAG, message)
    }

    override fun e(throwable: Throwable) {
        Log.e(TAG, throwable.message)
        FirebaseCrash.report(throwable)
    }

    companion object {
        const val TAG: String = "ru.nekit.android.qls"
    }
}