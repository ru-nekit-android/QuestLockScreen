package ru.nekit.android.data.shared

import android.content.SharedPreferences
import ru.nekit.android.data.StringKeyBooleanValueStore
import ru.nekit.android.data.StringKeyIntValueStore
import ru.nekit.android.data.StringKeyLongValueStore
import ru.nekit.android.data.StringKeyStringValueStore
import ru.nekit.android.qls.shared.repository.ISetupWizardSettingsRepository

abstract class SetupWizardBaseSettingsRepository(sharedPreferences: SharedPreferences) : ISetupWizardSettingsRepository {

    protected val longStore: StringKeyLongValueStore = StringKeyLongValueStore(sharedPreferences)
    private val intStore: StringKeyIntValueStore = StringKeyIntValueStore(sharedPreferences)
    protected val stringStore: StringKeyStringValueStore = StringKeyStringValueStore(sharedPreferences)
    protected val booleanStore: StringKeyBooleanValueStore = StringKeyBooleanValueStore(sharedPreferences)

    companion object {
        const val SETUP_IS_START = "setup_wizard.is_start"
        const val SETUP_IS_COMPLETE = "setup_wizard.is_complete"
        const val QUEST_SERIES_LENGTH = "setup_wizard.quest_series_length"
        const val LENGTH_BY_DEFAULT: Int = 1
    }

    private fun createParameter(name: String): String {
        return "${this.name}_$name"
    }

    override fun setupWizardIsStart(): Boolean =
            booleanStore.get(createParameter(SETUP_IS_START))

    override fun setupWizardIsComplete(): Boolean =
            booleanStore.get(createParameter(SETUP_IS_COMPLETE))

    override fun completeSetupWizard(value: Boolean) =
            booleanStore.set(createParameter(SETUP_IS_COMPLETE), value)

    override fun startSetupWizard(value: Boolean) =
            booleanStore.set(createParameter(SETUP_IS_START), value)

    override fun getQuestSeriesLength(): Int = Math.max(LENGTH_BY_DEFAULT, intStore.get(QUEST_SERIES_LENGTH))

    override fun setQuestSeriesLength(value: Int) =
            intStore.set(QUEST_SERIES_LENGTH, value)


}