package ru.nekit.android.data.shared

import android.content.SharedPreferences
import ru.nekit.android.data.BooleanKeyValueStore
import ru.nekit.android.data.StringKeyIntValueStore
import ru.nekit.android.qls.shared.repository.ISetupWizardSettingsRepository

open class SetupWizardBaseSettingsRepository(sharedPreferences: SharedPreferences) : ISetupWizardSettingsRepository {

    protected val intStore: StringKeyIntValueStore = StringKeyIntValueStore(sharedPreferences)
    protected val booleanStore: BooleanKeyValueStore = BooleanKeyValueStore(sharedPreferences)

    companion object {
        const val SETUP_IS_START = "setup_wizard.is_start"
        const val SETUP_IS_COMPLETE = "setup_wizard.is_complete"
        const val QUEST_SERIES_LENGTH = "setup_wizard.quest_series_length"
        const val LENGTH_BY_DEFAULT: Int = 1

        private fun createParameter(prefix: String, name: String): String {
            return "${prefix}_$name"
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

    override fun getQuestSeriesLength(): Int = Math.max(LENGTH_BY_DEFAULT, intStore.get(QUEST_SERIES_LENGTH))

    override fun setQuestSeriesLength(value: Int) =
            intStore.put(QUEST_SERIES_LENGTH, value)


}
