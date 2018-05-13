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
import ru.nekit.android.utils.throttleClicks

class SettingsFragment : QuestSetupWizardFragment() {

    override var unconditionedNextAction: Boolean = true

    private lateinit var saveQuestSeriesLengthButton: Button
    //private lateinit var showBindParentButton: Button
    private lateinit var voiceRecorderButton: Button
    private lateinit var subscribesButton: Button
    private lateinit var phoneContactsButton: Button
    private lateinit var questSeriesLengthInput: EditText

    override fun onSetupStart(view: View) {
        saveQuestSeriesLengthButton = view.findViewById(R.id.btn_quest_series_length_save)
        //showBindParentButton = view.findViewById(R.id.btn_bind_parent_control)
        subscribesButton = view.findViewById(R.id.btn_subscribes)
        voiceRecorderButton = view.findViewById(R.id.btn_voice_record)
        phoneContactsButton = view.findViewById(R.id.btn_phone_contacts)
        phoneContactsButton.visibility = if (setupWizard.phoneIsAvailable()) VISIBLE else GONE
        questSeriesLengthInput = view.findViewById(R.id.input_quest_series_length)
        SetupWizardUseCases.getQuestSeriesLength { value ->
            questSeriesLengthInput.setText(value.toString())
        }
        setNextButtonText(R.string.label_play_now)
        setAltButtonText(R.string.label_stop_lock)
        autoDisposeList(
                saveQuestSeriesLengthButton.throttleClicks {
                    SetupWizardUseCases.setQuestSeriesLength(Integer.valueOf(questSeriesLengthInput.text.toString()))
                },
                /*showBindParentButton.throttleClicks {
                    showSetupWizardStep(BIND_PARENT_CONTROL)
                },*/
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
            setAltButtonVisibility(it)
        }
        updateTitle()
    }

    private fun updateTitle() {
        titleView.text = getString(R.string.title_settings)
    }

    override fun onDestroy() {
        KeyboardHost.hideKeyboard(context!!, questSeriesLengthInput, Delay.KEYBOARD.get(context!!))
        super.onDestroy()
    }

    override fun nextAction(): Single<Boolean> = Single.fromCallable {
        LockScreen.getInstance().play()
        false
    }

    @LayoutRes
    override fun getLayoutId(): Int = R.layout.sw_settings

    override fun altAction() {
        setupWizard.pause()
        setupWizard.lockScreenIsSwitchedOn {
            setAltButtonVisibility(it)
        }
    }

    companion object {

        val instance: SettingsFragment
            get() = SettingsFragment()
    }
}