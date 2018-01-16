package ru.nekit.android.qls

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import ru.nekit.android.domain.qls.repository.ICurrentPupilRepository
import ru.nekit.android.domain.qls.repository.IPupilRepository
import ru.nekit.android.qls.data.entity.MyObjectBox
import ru.nekit.android.qls.data.repository.CurrentPupilRepository
import ru.nekit.android.qls.data.repository.PupilRepository
import ru.nekit.android.qls.lockScreen.LockScreen


class ktQuestLockScreenApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PreferencesUtil.init(this)
        LockScreen.activateForSetupWizard(this)

        val boxStore = MyObjectBox.builder().androidContext(this).build()
        val currentPupilRepository: ICurrentPupilRepository = CurrentPupilRepository(getSharedPreferences("my", MODE_PRIVATE))
        val pupilRepository: IPupilRepository = PupilRepository(boxStore, currentPupilRepository)

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}