package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.LayoutRes
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.useCases.SettingsUseCases
import ru.nekit.android.qls.lockScreen.LockScreen
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.*
import ru.nekit.android.utils.Delay
import ru.nekit.android.utils.KeyboardHost
import ru.nekit.android.utils.throttleClicks


class SettingsFragment : QuestSetupWizardFragment() {

    override var unconditionedNextAction: Boolean = false

    private lateinit var saveQuestSeriesLengthButton: Button
    private lateinit var showBindParentButton: Button
    private lateinit var voiceRecorderButton: Button
    private lateinit var phoneContactsButton: Button
    private lateinit var questSeriesLengthInput: EditText

    override fun onSetupStart(view: View) {
        saveQuestSeriesLengthButton = view.findViewById(R.id.btn_quest_series_length_save)
        showBindParentButton = view.findViewById(R.id.btn_bind_parent_control)
        voiceRecorderButton = view.findViewById(R.id.btn_voice_record)
        phoneContactsButton = view.findViewById(R.id.btn_phone_contacts)
        phoneContactsButton.visibility = if (setupWizard.phoneIsAvailable()) VISIBLE else GONE
        questSeriesLengthInput = view.findViewById(R.id.input_quest_series_length)
        SettingsUseCases.getQuestSeriesLength { value ->
            questSeriesLengthInput.setText(value.toString())
        }
        setNextButtonText(R.string.label_try_now)
        setAltButtonText(R.string.label_stop_lock)
        autoDisposeList {
            listOf(
                    saveQuestSeriesLengthButton.throttleClicks {
                        SettingsUseCases.setQuestSeriesLength(Integer.valueOf(questSeriesLengthInput.text.toString()))
                    },
                    showBindParentButton.throttleClicks {
                        showSetupWizardStep(BIND_PARENT_CONTROL)
                    },
                    voiceRecorderButton.throttleClicks {
                        showSetupWizardStep(VOICE_RECORD)
                    },
                    phoneContactsButton.throttleClicks {
                        showSetupWizardStep(SETUP_PHONE_CONTACTS)
                    }
            )
        }
        setAltButtonVisibility(setupWizard.lockScreenIsActive())
    }

    override fun onDestroy() {
        KeyboardHost.hideKeyboard(context!!, questSeriesLengthInput, Delay.KEYBOARD.get(context!!))
        super.onDestroy()
    }

    override fun nextAction(): Single<Boolean> = Single.fromCallable {
        LockScreen.show(context!!)
        false
    }

    @LayoutRes
    override fun getLayoutId(): Int = R.layout.sw_settings

    override fun altAction() {
        setupWizard.switchOff()
        setAltButtonVisibility(setupWizard.lockScreenIsActive())
    }

    companion object {

        val instance: SettingsFragment
            get() = SettingsFragment()
    }
}