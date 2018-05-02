package ru.nekit.android.qls.lockScreen

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import ru.nekit.android.qls.QuestLockScreenApplication
import ru.nekit.android.qls.domain.model.LockScreenStartType
import ru.nekit.android.qls.domain.model.LockScreenStartType.*
import ru.nekit.android.qls.domain.useCases.LockScreenUseCases
import ru.nekit.android.qls.lockScreen.service.LockScreenService
import ru.nekit.android.utils.ActivityUtils

object LockScreen {

    /*
    checkStatus
    on
    show
    */

    private fun startIfOn(context: Context, startType: LockScreenStartType) =
            LockScreenUseCases.isSwitchedOn {
                if (it) start(context, startType)
            }

    private fun start(context: Context, startType: LockScreenStartType) =
            LockScreenUseCases.start(startType) {
                ContextCompat.startForegroundService(context, getStartIntent(context, startType))
            }

    fun startForSetupWizard(application: QuestLockScreenApplication) {
        LockScreenUseCases.isSwitchedOn {
            start(application, if (!it) {
                if (application.setupWizard.setupIsComplete()) {
                    LET_TRY_IT
                } else {
                    SETUP_WIZARD_IN_PROCESS
                }
            } else {
                LET_TRY_IT
            })
        }
    }

    fun switchOff(context: Context) =
            LockScreenUseCases.switchOff {
                context.stopService(Intent(context, LockScreenService::class.java))
            }

    fun show(context: Context) = start(context, EXPLICIT)

    fun hide() = LockScreenUseCases.hide()

    fun hide(body: () -> Unit) = LockScreenUseCases.hide(body)

    fun getStartIntent(context: Context, startType: LockScreenStartType): Intent =
            Intent(context, LockScreenService::class.java).apply {
                putExtra(LockScreenStartType.NAME, startType.ordinal)
            }

    fun isActive(context: Context): Boolean =
            ActivityUtils.isServiceRunning(context, LockScreenService::class.java)

    fun startOnBootComplete(context: Context) {
        start(context, ON_BOOT_COMPLETE)
    }

    fun startOnDestroy(context: Context) {
        startIfOn(context, ON_DESTROY)
    }

}