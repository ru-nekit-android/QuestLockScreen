package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.LayoutRes
import android.view.View
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.PatternLockView.PatternViewMode.WRONG
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.setupWizard.BaseSetupWizard
import ru.nekit.android.qls.setupWizard.BaseSetupWizardStep.UNLOCK_SECRET
import ru.nekit.android.qls.setupWizard.ISetupWizardStep
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.SETUP_UNLOCK_SECRET
import ru.nekit.android.qls.utils.Delay
import ru.nekit.android.qls.utils.KeyboardHost
import ru.nekit.android.qls.utils.Vibrate

class UnlockSecretFragment : QuestSetupWizardFragment(), PatternLockViewListener {

    private lateinit var step: ISetupWizardStep
    private lateinit var patternLockView: PatternLockView
    private lateinit var pattern: List<PatternLockView.Dot>

    override fun onStarted() {
    }

    override fun onProgress(pattern: List<PatternLockView.Dot>) {
        if (step == SETUP_UNLOCK_SECRET) {
            setAltButtonVisibility(pattern.size >= BaseSetupWizard.UNLOCK_SECRET_MIN_SIZE)
        }
    }

    override fun onResume() {
        KeyboardHost.hideKeyboard(context!!, patternLockView, Delay.KEYBOARD.get(context!!))
        super.onResume()
    }

    override fun onComplete(pattern: List<PatternLockView.Dot>) {
        if (pattern.size < BaseSetupWizard.UNLOCK_SECRET_MIN_SIZE) {
            patternLockView.setViewMode(WRONG)
        } else {
            if (step == SETUP_UNLOCK_SECRET) {
                this.pattern = pattern
                update(true)
            } else if (step == UNLOCK_SECRET) {
                setupWizard.checkUnlockSecret(PatternLockUtils.patternToMD5(patternLockView, pattern)) { it ->
                    if (it) {
                        showNextSetupWizardStep()
                    } else {
                        patternLockView.setViewMode(WRONG)
                        Vibrate.make(context!!, 400)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        resetPattern()
    }

    override fun onSetupStart(view: View) {
        patternLockView = view.findViewById<View>(R.id.unlock_secret_view) as PatternLockView
        with(patternLockView) {
            addPatternLockListener(this@UnlockSecretFragment)
            dotCount = 3
            isAspectRatioEnabled = true
            dotAnimationDuration = 150
            pathEndAnimationDuration = 100
            isInStealthMode = false
            isTactileFeedbackEnabled = true
            isInputEnabled = true
        }
        if (step == SETUP_UNLOCK_SECRET) {
            setNextButtonText(R.string.label_set_unlock_secret)
            setAltButtonText(R.string.label_reset_unlock_secret)
        } else if (step == UNLOCK_SECRET) {
            setToolContainerVisibility(false)
        }
        setAltButtonVisibility(false)
        update(false)
    }

    override fun onDestroy() {
        patternLockView.removePatternLockListener(this)
        super.onDestroy()
    }

    override fun nextAction(): Single<Boolean> =
            setupWizard.setUnlockSecret(PatternLockUtils.patternToMD5(patternLockView, pattern)).toSingleDefault(true)

    @LayoutRes
    override fun getLayoutId(): Int {
        return R.layout.sw_unlock_secret
    }

    private fun update(unlockSecretIsSet: Boolean) {
        setNextButtonVisibility(unlockSecretIsSet)
    }

    override fun altAction() {
        patternLockView.clearPattern()
        resetPattern()
    }

    private fun resetPattern() {
        pattern = ArrayList()
        setAltButtonVisibility(false)
        setNextButtonVisibility(false)
    }

    companion object {

        fun getInstance(step: ISetupWizardStep): UnlockSecretFragment {
            val fragment = UnlockSecretFragment()
            fragment.step = step
            return fragment
        }
    }
}