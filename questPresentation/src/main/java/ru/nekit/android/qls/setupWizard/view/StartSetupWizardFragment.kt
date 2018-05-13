package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.LayoutRes
import android.view.View
import ru.nekit.android.qls.R
import ru.nekit.android.qls.R.string.label_setup_wizard_continue
import ru.nekit.android.qls.R.string.label_start_setup_wizard
import ru.nekit.android.utils.ParameterlessSingletonHolder

class StartSetupWizardFragment : QuestSetupWizardFragment() {

    @LayoutRes
    override fun getLayoutId() = R.layout.sw_setup_start

    override fun onSetupStart(view: View) {
        setupWizard.setupIsStart {
            setNextButtonText(if (it)
                label_setup_wizard_continue
            else
                label_start_setup_wizard)
        }
    }

    companion object : ParameterlessSingletonHolder<StartSetupWizardFragment>(::StartSetupWizardFragment)
}
