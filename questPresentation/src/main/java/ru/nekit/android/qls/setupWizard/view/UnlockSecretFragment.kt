package ru.nekit.android.qls.setupWizard.view

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RelativeLayout
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.PatternLockView.PatternViewMode.WRONG
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.setupWizard.BaseSetupWizard
import ru.nekit.android.qls.setupWizard.BaseSetupWizardStep
import ru.nekit.android.qls.setupWizard.BaseSetupWizardStep.UNLOCK_SECRET
import ru.nekit.android.qls.setupWizard.ISetupWizardStep
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.SET_UNLOCK_SECRET
import ru.nekit.android.utils.Delay
import ru.nekit.android.utils.KeyboardHost
import ru.nekit.android.utils.ParameterlessSingletonHolder
import ru.nekit.android.utils.Vibrate

class UnlockSecretFragment : QuestSetupWizardFragment(), PatternLockViewListener {

    var step: ISetupWizardStep? = null
    private lateinit var patternLockView: PatternLockView
    private lateinit var pattern: List<PatternLockView.Dot>

    override fun onStarted() {
    }

    override fun onProgress(pattern: List<PatternLockView.Dot>) {
        if (step == SET_UNLOCK_SECRET) {
            update(pattern.size >= BaseSetupWizard.UNLOCK_SECRET_MIN_SIZE, false)
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
            if (step == SET_UNLOCK_SECRET) {
                this.pattern = pattern
                update(true, true)
            } else if (step == UNLOCK_SECRET) {
                setupWizard.checkUnlockSecret(PatternLockUtils.patternToMD5(patternLockView, pattern)) { it ->
                    if (it) {
                        patternLockView.clearPattern()
                        goNext()
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
        patternLockView = view.findViewById<View>(R.id.unlock_view) as PatternLockView
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
        if (step == SET_UNLOCK_SECRET) {
            title = R.string.title_setup_unlock_secret
            nextButtonText(R.string.label_set_unlock_secret)
            altButtonText(R.string.label_reset_unlock_secret)
            descriptionView.visibility = VISIBLE
        } else if (step == UNLOCK_SECRET) {
            title = R.string.title_enter_unlock_secret
            toolContainerVisibility(false)
            descriptionView.visibility = GONE
            (patternLockView.layoutParams as RelativeLayout.LayoutParams).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                patternLockView.layoutParams = this
            }
        }
        update(false, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        step?.apply {
            outState.putString(CURRENT_STEP, name)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            step = BaseSetupWizardStep.valueOf(savedInstanceState.getString(CURRENT_STEP))
        }
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onDestroy() {
        patternLockView.removePatternLockListener(this)
        super.onDestroy()
    }

    override fun nextAction(): Single<Boolean> =
            setupWizard.setUnlockSecret(PatternLockUtils.patternToMD5(patternLockView, pattern)).toSingleDefault(true)

    @LayoutRes
    override fun getLayoutId() = R.layout.sw_unlock_secret

    private fun update(unlockSecretCanBeReset: Boolean, unlockSecretIsSet: Boolean) {
        toolContainerVisibility(unlockSecretCanBeReset || unlockSecretIsSet)
        altButtonVisibility(unlockSecretCanBeReset)
        nextButtonVisibility(unlockSecretIsSet)
    }

    override fun altAction() {
        patternLockView.clearPattern()
        resetPattern()
    }

    private fun resetPattern() {
        pattern = ArrayList()
        update(false, false)
    }

    companion object : ParameterlessSingletonHolder<UnlockSecretFragment>(::UnlockSecretFragment) {

        const val CURRENT_STEP = "current_step"

    }
}