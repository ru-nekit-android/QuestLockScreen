package ru.nekit.android.domain.shared.repository

interface ISetupWizardBaseSettingsRepository {

    fun setupWizardIsStarted(): Boolean
    fun setupWizardIsCompleted(prefix: String): Boolean
    fun completeSetupWizard()
    fun startSetupWizard()
}

interface IParentControlSettingRepository : ISetupWizardBaseSettingsRepository