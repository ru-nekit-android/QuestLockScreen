package ru.nekit.android.data.shared

import android.content.SharedPreferences
import ru.nekit.android.data.IntKeyValueStore
import ru.nekit.android.data.StringKeyValueStore
import ru.nekit.android.domain.shared.repository.ISetupWizardBaseSettingsRepository

open class SetupWizardBaseSettingsRepository(sharedPreferences: SharedPreferences) : ISetupWizardBaseSettingsRepository {

    protected val stringStore: StringKeyValueStore = StringKeyValueStore(sharedPreferences)
    protected val intStore: IntKeyValueStore = IntKeyValueStore(sharedPreferences)

    companion object {
        private val SETUP_IS_STARTED = "setup_wizard.is_started"
        private val SETUP_IS_COMPLETED = "setup_wizard.is_completed"
    }

    override fun setupWizardIsStarted(): Boolean {
        return intStore.get(SETUP_IS_STARTED) == 1
    }

    override fun setupWizardIsCompleted(prefix: String): Boolean {
        return intStore.get(SETUP_IS_COMPLETED) == 1
    }

    override fun completeSetupWizard() {
        intStore.put(SETUP_IS_COMPLETED, 1)
    }

    override fun startSetupWizard() {
        intStore.put(SETUP_IS_STARTED, 1)
    }
}