package ru.nekit.android.qls.lockScreen

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import ru.nekit.android.qls.domain.model.LockScreenStartType
import ru.nekit.android.qls.domain.model.LockScreenStartType.*
import ru.nekit.android.qls.domain.useCases.LockScreenUseCases
import ru.nekit.android.qls.domain.useCases.SetupWizardUseCases
import ru.nekit.android.qls.lockScreen.service.LockScreenService

object LockScreen {

    /*
    checkStatus
    on
    play
    */

    private fun startIfOn(context: Context, startType: LockScreenStartType) =
            LockScreenUseCases.isSwitchedOn {
                if (it) start(context, startType)
            }

    private fun start(context: Context, startType: LockScreenStartType) =
            LockScreenUseCases.start(startType) {
                ContextCompat.startForegroundService(context, getStartIntent(context, startType))
            }

    fun startForSetupWizard(context: Context) {
        LockScreenUseCases.isSwitchedOn { isOn ->
            if (isOn)
                start(context, LET_PLAY)
            else
                SetupWizardUseCases.setupIsComplete { isComplete ->
                    if (isComplete)
                        start(context, LET_TRY_TO_PLAY)
                    else
                        start(context, SETUP_WIZARD_IN_PROCESS)
                }
        }
    }

    private fun switchOff(context: Context, body: () -> Unit) =
            LockScreenUseCases.switchOff {
                context.stopService(Intent(context, LockScreenService::class.java))
                body()
            }

    fun isSwitchedOn(body: (Boolean) -> Unit) = LockScreenUseCases.isSwitchedOn(body)

    fun play(context: Context) = start(context, PLAY_NOW)

    fun stop(context: Context) = switchOff(context) {
        start(context, LET_TRY_TO_PLAY)
    }

    fun hide() = LockScreenUseCases.hide {}

    fun hide(body: () -> Unit) = LockScreenUseCases.hide(body)

    fun getStartIntent(context: Context, startType: LockScreenStartType): Intent =
            Intent(context, LockScreenService::class.java).apply {
                putExtra(LockScreenStartType.NAME, startType.ordinal)
            }

    fun startOnBootComplete(context: Context) {
        start(context, ON_BOOT_COMPLETE)
    }

    fun startOnDestroy(context: Context) {
        startIfOn(context, ON_DESTROY)
    }

}