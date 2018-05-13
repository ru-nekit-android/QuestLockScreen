package ru.nekit.android.qls.lockScreen

import android.content.Intent
import android.support.v4.content.ContextCompat
import ru.nekit.android.qls.dependences.ContextDependenciesHolder
import ru.nekit.android.qls.domain.model.LockScreenStartType
import ru.nekit.android.qls.domain.model.LockScreenStartType.*
import ru.nekit.android.qls.domain.useCases.LockScreenUseCases
import ru.nekit.android.qls.domain.useCases.SetupWizardUseCases
import ru.nekit.android.qls.lockScreen.service.LockScreenService
import ru.nekit.android.utils.ParameterlessSingletonHolder

class LockScreen private constructor() : ContextDependenciesHolder() {

    /*
    checkStatus
    on
    play
    */

    private fun startIfOn(startType: LockScreenStartType) =
            LockScreenUseCases.isSwitchedOn {
                if (it) start(startType)
            }

    private fun start(startType: LockScreenStartType) =
            LockScreenUseCases.start(startType) {
                ContextCompat.startForegroundService(context, getStartIntent(startType))
            }

    fun startForSetupWizard() {
        LockScreenUseCases.isSwitchedOn { isOn ->
            if (isOn)
                start(LET_PLAY)
            else
                SetupWizardUseCases.setupIsComplete { isComplete ->
                    if (isComplete)
                        start(LET_TRY_TO_PLAY)
                    else
                        start(SETUP_WIZARD_IN_PROCESS)
                }
        }
    }

    private fun switchOff(body: () -> Unit) =
            LockScreenUseCases.switchOff {
                context.stopService(Intent(context, LockScreenService::class.java))
                body()
            }

    fun isSwitchedOn(body: (Boolean) -> Unit) = LockScreenUseCases.isSwitchedOn(body)

    fun play() = start(PLAY_NOW)

    fun pause() = switchOff {
        start(LET_TRY_TO_PLAY)
    }

    fun hide() = LockScreenUseCases.hide {}

    fun hide(body: () -> Unit) = LockScreenUseCases.hide(body)

    fun getStartIntent(startType: LockScreenStartType): Intent =
            Intent(context, LockScreenService::class.java).apply {
                putExtra(LockScreenStartType.NAME, startType.ordinal)
            }

    fun startOnBootComplete() {
        start(ON_BOOT_COMPLETE)
    }

    fun startOnDestroy() {
        startIfOn(ON_DESTROY)
    }

    companion object : ParameterlessSingletonHolder<LockScreen>(::LockScreen)

}