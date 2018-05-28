package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.LayoutRes
import android.view.View
import ru.nekit.android.qls.R
import ru.nekit.android.qls.R.string.label_setup_wizard_continue
import ru.nekit.android.qls.R.string.label_setup_wizard_start
import ru.nekit.android.utils.ParameterlessSingletonHolder

class StartFragment : QuestSetupWizardFragment() {

    @LayoutRes
    override fun getLayoutId() = R.layout.sw_start

    override fun onSetupStart(view: View) {
        title = R.string.title_setup_wizard_start
        setupWizard.setupIsStart {
            nextButtonText(if (it)
                label_setup_wizard_continue
            else
                label_setup_wizard_start)
        }
    }

    companion object : ParameterlessSingletonHolder<StartFragment>(::StartFragment)
}