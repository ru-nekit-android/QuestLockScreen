package ru.nekit.android.data.shared

import android.content.SharedPreferences
import ru.nekit.android.data.BooleanKeyValueStore
import ru.nekit.android.data.StringKeyIntValueStore
import ru.nekit.android.qls.shared.repository.ISetupWizardSettingsRepository

open class SetupWizardBaseSettingsRepository(sharedPreferences: SharedPreferences) : ISetupWizardSettingsRepository {

    private val intStore: StringKeyIntValueStore = StringKeyIntValueStore(sharedPreferences)
    private val booleanStore: BooleanKeyValueStore = BooleanKeyValueStore(sharedPreferences)

    companion object {
        val SETUP_IS_START = "setup_wizard.is_start"
        val SETUP_IS_COMPLETE = "setup_wizard.is_complete"
        val QUEST_SERIES_LENGTH = "setup_wizard.quest_series_length"

        private fun createParameter(prefix: String, name: String): String {
            return String.format("%s_%s", prefix, name)
        }
    }

    override fun setupWizardIsStart(prefix: String): Boolean =
            booleanStore.get(createParameter(prefix, SETUP_IS_START))

    override fun setupWizardIsComplete(prefix: String): Boolean =
            booleanStore.get(createParameter(prefix, SETUP_IS_COMPLETE))

    override fun completeSetupWizard(prefix: String, value: Boolean) =
            booleanStore.put(createParameter(prefix, SETUP_IS_COMPLETE), value)

    override fun startSetupWizard(prefix: String, value: Boolean) =
            booleanStore.put(createParameter(prefix, SETUP_IS_START), value)

    override fun getQuestSeriesLength(): Int = intStore.get(QUEST_SERIES_LENGTH)

    override fun setQuestSeriesLength(value: Int) =
            intStore.put(QUEST_SERIES_LENGTH, value)

}