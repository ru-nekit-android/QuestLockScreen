package ru.nekit.android.qls.setupWizard.view

import android.Manifest
import android.view.View
import android.widget.ImageButton
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.AnswerType.RIGHT
import ru.nekit.android.qls.domain.model.AnswerType.WRONG
import ru.nekit.android.qls.setupWizard.BaseSetupWizardPermissionRequestFragment
import ru.nekit.android.qls.setupWizard.QuestSetupWizard
import ru.nekit.android.qls.setupWizard.VoiceCenter


class VoiceRecordFragment : BaseSetupWizardPermissionRequestFragment(), View.OnClickListener {

    private lateinit var voiceCenter: VoiceCenter
    private lateinit var rightAnswerVoiceRecordButton: ImageButton
    private lateinit var wrongAnswerVoiceRecordButton: ImageButton
    private var activeType: AnswerType? = null

    override val permissionRequestCode: Int
        get() = 2

    override val permissionList: Array<String>
        get() = PERMISSIONS

    override fun onSetupStart(view: View) {
        setAltButtonText(R.string.label_back)
        if (!setupWizard.permissionIsGranted(PERMISSIONS)) {
            requestPermission()
        } else {
            voiceCenter = VoiceCenter()
            rightAnswerVoiceRecordButton = view.findViewById(R.id.btn_record_voice_for_right_answer) as ImageButton
            wrongAnswerVoiceRecordButton = view.findViewById(R.id.btn_record_voice_for_wrong_answer) as ImageButton
            rightAnswerVoiceRecordButton.setOnClickListener(this)
            wrongAnswerVoiceRecordButton.setOnClickListener(this)
        }
        update(false)
    }


    override fun altAction() {
        showSetupWizardStep(QuestSetupWizard.QuestSetupWizardStep.SETTINGS)
    }

    override fun onStop() {
        rightAnswerVoiceRecordButton.setOnClickListener(null)
        wrongAnswerVoiceRecordButton.setOnClickListener(null)
        voiceCenter.stopRecording()
        super.onStop()
    }

    override fun getLayoutId(): Int {
        return R.layout.sw_voice_record
    }

    override val addToBackStack: Boolean = true

    override fun onPermissionResult(grantResults: Boolean) {
        if (!grantResults) {
            showSetupWizardStep(QuestSetupWizard.QuestSetupWizardStep.SETTINGS)
        }
    }

    override fun onClick(view: View) {
        voiceCenter.stopRecording()
        if (view == rightAnswerVoiceRecordButton) {
            if (activeType == RIGHT) {
                rightAnswerVoiceRecordButton.setImageResource(BUTTON_ICONS[0])
                activeType = null
            } else {
                activeType = RIGHT
                voiceCenter.startRecording(activeType!!)
                rightAnswerVoiceRecordButton.setImageResource(BUTTON_ICONS[1])
                wrongAnswerVoiceRecordButton.setImageResource(BUTTON_ICONS[0])
            }
        } else if (view === wrongAnswerVoiceRecordButton) {
            if (activeType == WRONG) {
                wrongAnswerVoiceRecordButton.setImageResource(BUTTON_ICONS[0])
                activeType = null
            } else {
                activeType = WRONG
                voiceCenter.startRecording(activeType!!)
                wrongAnswerVoiceRecordButton.setImageResource(BUTTON_ICONS[1])
                rightAnswerVoiceRecordButton.setImageResource(BUTTON_ICONS[0])
            }
        }
    }

    companion object {

        private val PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        private val BUTTON_ICONS = intArrayOf(R.drawable.ic_voice_24dp, R.drawable.ic_stop_48dp)

        val instance: VoiceRecordFragment
            get() = VoiceRecordFragment()
    }
}