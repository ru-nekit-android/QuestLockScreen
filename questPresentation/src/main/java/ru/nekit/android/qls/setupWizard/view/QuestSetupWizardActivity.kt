package ru.nekit.android.qls.setupWizard.view

import android.os.Bundle
import ru.nekit.android.qls.QuestLockScreenApplication
import ru.nekit.android.qls.eventBus.IEventListener
import ru.nekit.android.qls.lockScreen.LockScreenContentMediatorEvent
import ru.nekit.android.qls.quest.providers.IEventListenerProvider
import ru.nekit.android.qls.setupWizard.BaseSetupWizardActivity
import ru.nekit.android.qls.setupWizard.BaseSetupWizardStep.UNLOCK_SECRET
import ru.nekit.android.qls.setupWizard.ISetupWizardStep
import ru.nekit.android.qls.setupWizard.QuestSetupWizard
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.*

class QuestSetupWizardActivity : BaseSetupWizardActivity(), IEventListenerProvider {

    override val setupWizard: QuestSetupWizard
        get() = QuestSetupWizard.getInstance(application as QuestLockScreenApplication)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listenForEvent(LockScreenContentMediatorEvent::class.java) {
            if (it == LockScreenContentMediatorEvent.ON_INIT)
                finish()
        }
        //eventBus.handleEvents(this, LockScreenService.PUPIL_BIND_OK)
    }

    private val questApplication: QuestLockScreenApplication
        get() = context.applicationContext as QuestLockScreenApplication

    override val eventListener: IEventListener
        get() = questApplication.getEventListener()


    override fun showSetupWizardStep(step: ISetupWizardStep, vararg params: Any) {
        setupWizard.commitCurrentSetupStep(step)
        replaceFragment(
                when (step) {
                    START -> StartSetupWizardFragment.instance
                    OVERLAY_PERMISSION -> OverlayPermissionFragment.instance
                    SETUP_UNLOCK_SECRET, UNLOCK_SECRET, CHANGE_UNLOCK_SECRET ->
                        UnlockSecretFragment.getInstance(step)
                    DEVICE_ADMIN -> SetupDeviceAdminFragment.instance
                    PUPIL_NAME -> SetupPupilNameFragment.instance
                    PUPIL_SEX -> SetupPupilSexFragment.instance
                    QTP_COMPLEXITY -> SetupQTPComplexityFragment.instance
                    PUPIL_AVATAR -> SetupPupilAvatarFragment.instance
                    CALL_PHONE_AND_READ_CONTACTS_PERMISSION ->
                        CallPhoneAndReadContactsPermissionFragment.instance
                    SETUP_PHONE_CONTACTS -> PhoneContactsFragment.instance
                    BIND_PARENT_CONTROL -> BindParentFragment.instance
                    VOICE_RECORD -> VoiceRecordFragment.instance
                    else -> SettingsFragment.instance
                }
        )
    }
}