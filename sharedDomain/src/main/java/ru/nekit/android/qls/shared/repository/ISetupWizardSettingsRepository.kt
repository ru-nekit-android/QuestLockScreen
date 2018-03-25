package ru.nekit.android.qls.shared.repository

interface ISetupWizardSettingsRepository {

    fun setupWizardIsStart(prefix: String): Boolean
    fun setupWizardIsComplete(prefix: String): Boolean
    fun completeSetupWizard(prefix: String, value: Boolean)
    fun startSetupWizard(prefix: String, value: Boolean)
    fun getQuestSeriesLength(): Int
    fun setQuestSeriesLength(value: Int)
}

interface IParentControlSettingRepository : ISetupWizardSettingsRepository