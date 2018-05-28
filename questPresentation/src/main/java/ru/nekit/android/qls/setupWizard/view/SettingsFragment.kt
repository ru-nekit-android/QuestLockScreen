package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.LayoutRes
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.useCases.SetupWizardUseCases
import ru.nekit.android.qls.lockScreen.LockScreen
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.*
import ru.nekit.android.utils.Delay
import ru.nekit.android.utils.KeyboardHost
import ru.nekit.android.utils.ParameterlessSingletonHolder
import ru.nekit.android.utils.throttleClicks

class SettingsFragment : QuestSetupWizardFragment() {

    override var unconditionedNextAction: Boolean = true

    private lateinit var saveQuestSeriesLengthButton: Button
    private lateinit var voiceRecorderButton: Button
    private lateinit var subscribesButton: Button
    private lateinit var phoneContactsButton: Button
    private lateinit var questSeriesLengthInput: EditText

    override fun onSetupStart(view: View) {
        title = R.string.title_setup_wizard_settings
        saveQuestSeriesLengthButton = view.findViewById(R.id.btn_quest_series_length_save)
        subscribesButton = view.findViewById(R.id.btn_subscribes)
        voiceRecorderButton = view.findViewById(R.id.btn_voice_record)
        phoneContactsButton = view.findViewById(R.id.btn_phone_contacts)
        phoneContactsButton.visibility = if (setupWizard.phoneIsAvailable()) VISIBLE else GONE
        questSeriesLengthInput = view.findViewById(R.id.input_quest_series_length)
        SetupWizardUseCases.getQuestSeriesLength { value ->
            questSeriesLengthInput.setText(value.toString())
        }
        nextButtonText(R.string.label_play_now)
        altButtonText(R.string.label_pause)
        autoDisposeList(
                saveQuestSeriesLengthButton.throttleClicks {
                    SetupWizardUseCases.setQuestSeriesLength(Integer.valueOf(questSeriesLengthInput.text.toString()))
                },
                subscribesButton.throttleClicks {
                    showSetupWizardStep(SUBSCRIBES)
                },
                voiceRecorderButton.throttleClicks {
                    showSetupWizardStep(VOICE_RECORD)
                },
                phoneContactsButton.throttleClicks {
                    showSetupWizardStep(SETUP_PHONE_CONTACTS)
                }
        )
        setupWizard.lockScreenIsSwitchedOn {
            altButtonVisibility(it)
        }
    }

    override fun onStop() {
        KeyboardHost.hideKeyboard(context!!, questSeriesLengthInput, Delay.KEYBOARD.get(context!!))
        super.onStop()
    }

    override fun nextAction(): Single<Boolean> = Single.fromCallable {
        LockScreen.getInstance().play()
        false
    }

    @LayoutRes
    override fun getLayoutId() = R.layout.sw_settings

    override fun altAction() {
        setupWizard.pause()
        setupWizard.lockScreenIsSwitchedOn {
            altButtonVisibility(it)
        }
    }

    companion object : ParameterlessSingletonHolder<SettingsFragment>(::SettingsFragment)
}