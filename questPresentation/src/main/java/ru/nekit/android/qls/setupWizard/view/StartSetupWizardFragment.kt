package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.LayoutRes
import android.view.View

import ru.nekit.android.qls.R

class StartSetupWizardFragment : QuestSetupWizardFragment() {

    @LayoutRes
    override fun getLayoutId(): Int {
        return R.layout.sw_setup_start
    }

    override fun onSetupStart(view: View) {
        setupWizard.setupIsStart {
            setNextButtonText(if (it)
                R.string.label_setup_wizard_continue
            else
                R.string.label_start_setup_wizard)
        }
    }

    companion object {

        val instance: StartSetupWizardFragment
            get() = StartSetupWizardFragment()
    }
}
