package ru.nekit.android.qls.shared.repository

interface ISetupWizardSettingsRepository {

    fun setupWizardIsStart(): Boolean
    fun setupWizardIsComplete(): Boolean
    fun completeSetupWizard(value: Boolean)
    fun startSetupWizard(value: Boolean)
    fun getQuestSeriesLength(): Int
    fun setQuestSeriesLength(value: Int)

    val name: String

}

interface IParentControlSettingRepository : ISetupWizardSettingsRepository