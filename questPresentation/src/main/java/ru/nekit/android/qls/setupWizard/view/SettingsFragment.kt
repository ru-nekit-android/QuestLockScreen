package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.LayoutRes
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.useCases.GetQuestSeriesLength
import ru.nekit.android.qls.domain.useCases.SetQuestSeriesLength
import ru.nekit.android.qls.lockScreen.LockScreen
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.*
import ru.nekit.android.qls.utils.Delay
import ru.nekit.android.qls.utils.KeyboardHost


class SettingsFragment : QuestSetupWizardFragment(), View.OnClickListener {

    override var unconditionedNextAction: Boolean = false

    private lateinit var saveQuestSeriesLengthButton: Button
    private lateinit var showBindParentButton: Button
    private lateinit var voiceRecorderButton: Button
    private lateinit var allowContactsButton: Button
    private lateinit var questSeriesLengthInput: EditText

    override fun onSetupStart(view: View) {
        saveQuestSeriesLengthButton = view.findViewById(R.id.btn_quest_series_length_save)
        showBindParentButton = view.findViewById(R.id.btn_bind_parent_control)
        voiceRecorderButton = view.findViewById(R.id.btn_voice_record)
        allowContactsButton = view.findViewById(R.id.btn_allow_contacts)
        allowContactsButton.visibility = if (setupWizard.phoneIsAvailable()) VISIBLE else GONE
        questSeriesLengthInput = view.findViewById(R.id.input_quest_series_length)
        autoDispose {
            GetQuestSeriesLength(questApplication, questApplication.getDefaultSchedulerProvider()).build().subscribe { value ->
                questSeriesLengthInput.setText(value.toString())
            }
        }

        setNextButtonText(R.string.label_try_now)
        setAltButtonText(R.string.label_stop_lock)
        saveQuestSeriesLengthButton.setOnClickListener(this)
        showBindParentButton.setOnClickListener(this)
        voiceRecorderButton.setOnClickListener(this)
        allowContactsButton.setOnClickListener(this)
        setAltButtonVisibility(setupWizard.lockScreenIsActive())

    }

    override fun onDestroy() {
        KeyboardHost.hideKeyboard(context!!, questSeriesLengthInput, Delay.KEYBOARD.get(context!!))
        saveQuestSeriesLengthButton.setOnClickListener(null)
        showBindParentButton.setOnClickListener(null)
        voiceRecorderButton.setOnClickListener(null)
        allowContactsButton.setOnClickListener(null)
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

    override fun onClick(view: View) {
        when (view) {
            showBindParentButton -> showSetupWizardStep(BIND_PARENT_CONTROL)
            allowContactsButton -> showSetupWizardStep(SETUP_PHONE_CONTACTS)
            saveQuestSeriesLengthButton ->
                SetQuestSeriesLength(questApplication, questApplication.getDefaultSchedulerProvider()).build(Integer.valueOf(questSeriesLengthInput.text.toString())).subscribe()
            voiceRecorderButton -> showSetupWizardStep(VOICE_RECORD)
        }
    }

    companion object {

        val instance: SettingsFragment
            get() = SettingsFragment()
    }
}