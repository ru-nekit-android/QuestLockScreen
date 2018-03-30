package ru.nekit.android.qls.setupWizard.view

import android.Manifest
import android.support.design.widget.FloatingActionButton
import android.view.View

import ru.nekit.android.qls.R
import ru.nekit.android.qls.setupWizard.BaseSetupWizardPermissionRequestFragment
import ru.nekit.android.qls.setupWizard.QuestSetupWizard
import ru.nekit.android.qls.setupWizard.VoiceCenter
import ru.nekit.android.qls.setupWizard.VoiceCenter.Type

import ru.nekit.android.qls.setupWizard.VoiceCenter.Type.RIGHT
import ru.nekit.android.qls.setupWizard.VoiceCenter.Type.WRONG

class VoiceRecordFragment : BaseSetupWizardPermissionRequestFragment(), View.OnClickListener {

    private lateinit var mVoiceCenter: VoiceCenter
    private lateinit var mRightAnswerVoiceRecordButton: FloatingActionButton
    private lateinit var mWrongAnswerVoiceRecordButton: FloatingActionButton
    private var mActiveType: Type? = null

    override val permissionRequestCode: Int
        get() = 2

    override val permissionList: Array<String>
        get() = PERMISSIONS

    override fun onSetupStart(view: View) {
        setAltButtonText(R.string.label_back)
        if (!setupWizard.permissionIsGranted(PERMISSIONS)) {
            requestPermission()
        } else {
            mVoiceCenter = VoiceCenter()
            mRightAnswerVoiceRecordButton = view.findViewById<View>(R.id.btn_record_voice_for_right_answer) as FloatingActionButton
            mWrongAnswerVoiceRecordButton = view.findViewById<View>(R.id.btn_record_voice_for_wrong_answer) as FloatingActionButton
            mRightAnswerVoiceRecordButton.setOnClickListener(this)
            mWrongAnswerVoiceRecordButton.setOnClickListener(this)
        }
        update(false)
    }


    override fun altAction() {
        showSetupWizardStep(QuestSetupWizard.QuestSetupWizardStep.SETTINGS)
    }

    override fun onStop() {
        mRightAnswerVoiceRecordButton.setOnClickListener(null)
        mWrongAnswerVoiceRecordButton.setOnClickListener(null)
        mVoiceCenter.stopRecording()
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
        mVoiceCenter.stopRecording()
        if (view == mRightAnswerVoiceRecordButton) {
            if (mActiveType == RIGHT) {
                mRightAnswerVoiceRecordButton.setImageResource(BUTTON_ICONS[0])
                mActiveType = null
            } else {
                mActiveType = RIGHT
                mVoiceCenter.startRecording(mActiveType!!)
                mRightAnswerVoiceRecordButton.setImageResource(BUTTON_ICONS[1])
                mWrongAnswerVoiceRecordButton.setImageResource(BUTTON_ICONS[0])
            }
        } else if (view === mWrongAnswerVoiceRecordButton) {
            if (mActiveType == WRONG) {
                mWrongAnswerVoiceRecordButton.setImageResource(BUTTON_ICONS[0])
                mActiveType = null
            } else {
                mActiveType = WRONG
                mVoiceCenter.startRecording(mActiveType!!)
                mWrongAnswerVoiceRecordButton.setImageResource(BUTTON_ICONS[1])
                mRightAnswerVoiceRecordButton.setImageResource(BUTTON_ICONS[0])
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