package ru.nekit.android.qls.lockScreen

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import ru.nekit.android.qls.domain.model.LockScreenStartType
import ru.nekit.android.qls.domain.model.LockScreenStartType.EXPLICIT
import ru.nekit.android.qls.domain.useCases.LockScreenUseCases
import ru.nekit.android.qls.lockScreen.service.LockScreenService
import ru.nekit.android.utils.ActivityUtils

object LockScreen {

    /*
    on
    start
    show
     */

    fun startIfOn(context: Context, startType: LockScreenStartType) =
            LockScreenUseCases.isSwitchedOn {
                if (it) start(context, startType)
            }

    private fun start(context: Context, startType: LockScreenStartType) =
            LockScreenUseCases.start(startType) {
                ContextCompat.startForegroundService(context, getStartIntent(context, startType))
            }

    /*
    fun startForSetupWizard(context: Context) {
        if (!isActive(context)) {
            start(context, LockScreenStartType.SETUP_WIZARD)
        }
    }

    fun switchOn(context: Context): Completable =
            LockScreenUseCases.SwitchOn(
                    getApplication(context),
                    getApplication(context).getDefaultSchedulerProvider()
            ).build()
    */

    fun switchOff(context: Context) =
            LockScreenUseCases.switchOff {
                context.stopService(Intent(context, LockScreenService::class.java))
            }

    //on -> start -> show
    fun show(context: Context) = start(context, EXPLICIT)

    fun hide() = LockScreenUseCases.hide()

    fun getStartIntent(context: Context, startType: LockScreenStartType): Intent =
            Intent(context, LockScreenService::class.java).apply {
                putExtra(LockScreenStartType.NAME, startType.ordinal)
            }

    fun isActive(context: Context): Boolean =
            ActivityUtils.isServiceRunning(context, LockScreenService::class.java)

}