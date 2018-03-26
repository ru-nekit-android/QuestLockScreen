package ru.nekit.android.qls.lockScreen

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import io.reactivex.Completable
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.qls.domain.model.LockScreenStartType
import ru.nekit.android.qls.domain.useCases.LockScreenUseCases
import ru.nekit.android.qls.lockScreen.service.LockScreenService
import ru.nekit.android.qls.utils.ActivityUtils

object LockScreen {

    /*
    on
    active
    show
     */

    fun activeIfOn(context: Context, startType: LockScreenStartType) =
            LockScreenUseCases.isSwitchedOn().use {
                if (it) active(context, startType)
            }

    private fun active(context: Context, startType: LockScreenStartType) {
        return LockScreenUseCases.start(startType) {
            ContextCompat.startForegroundService(context, getStartIntent(context, startType))
        }
    }

    /*
    fun startForSetupWizard(context: Context) {
        if (!isActive(context)) {
            active(context, LockScreenStartType.SETUP_WIZARD)
        }
    }

    fun switchOn(context: Context): Completable =
            LockScreenUseCases.SwitchOn(
                    getApplication(context),
                    getApplication(context).getDefaultSchedulerProvider()
            ).build()
    */

    fun switchOff(context: Context): Completable =
            LockScreenUseCases.switchOff().doOnComplete {
                context.stopService(Intent(context, LockScreenService::class.java))
            }

    fun show(context: Context) = active(context, LockScreenStartType.EXPLICIT)

    fun hide() = LockScreenUseCases.hide()

    fun getStartIntent(context: Context, startType: LockScreenStartType): Intent =
            Intent(context, LockScreenService::class.java).apply {
                putExtra(LockScreenStartType.NAME, startType.ordinal)
            }

    fun isActive(context: Context): Boolean =
            ActivityUtils.isServiceRunning(context, LockScreenService::class.java)

}