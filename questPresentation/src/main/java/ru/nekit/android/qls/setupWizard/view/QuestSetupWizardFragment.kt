package ru.nekit.android.qls.setupWizard.view

import ru.nekit.android.qls.QuestLockScreenApplication
import ru.nekit.android.qls.setupWizard.BaseSetupWizardFragment
import ru.nekit.android.qls.setupWizard.QuestSetupWizard

abstract class QuestSetupWizardFragment : BaseSetupWizardFragment() {
    override val setupWizard: QuestSetupWizard
        get() = super.setupWizard as QuestSetupWizard

    val questApplication: QuestLockScreenApplication
        get() = context!!.applicationContext as QuestLockScreenApplication
}
