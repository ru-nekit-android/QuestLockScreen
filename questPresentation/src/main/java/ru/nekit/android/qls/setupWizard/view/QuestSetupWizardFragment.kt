package ru.nekit.android.qls.setupWizard.view

import android.widget.TextView
import ru.nekit.android.qls.R
import ru.nekit.android.qls.setupWizard.BaseSetupWizardFragment
import ru.nekit.android.qls.setupWizard.QuestSetupWizard

abstract class QuestSetupWizardFragment : BaseSetupWizardFragment() {

    override val setupWizard: QuestSetupWizard
        get() = super.setupWizard as QuestSetupWizard

    val titleView
        get() = view?.findViewById(R.id.tv_title) as TextView

    val descriptionView
        get() = view?.findViewById(R.id.tv_description) as TextView

}
