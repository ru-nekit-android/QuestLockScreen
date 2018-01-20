package ru.nekit.android.qls

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import ru.nekit.android.qls.lockScreen.LockScreen

class ktQuestLockScreenApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PreferencesUtil.init(this)
        LockScreen.activateForSetupWizard(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}