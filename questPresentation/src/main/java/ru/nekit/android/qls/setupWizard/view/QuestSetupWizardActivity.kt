package ru.nekit.android.qls.setupWizard.view

import com.anadeainc.rxbus.Subscribe
import ru.nekit.android.qls.QuestLockScreenApplication
import ru.nekit.android.qls.domain.useCases.TransitionChoreographEvent
import ru.nekit.android.qls.setupWizard.BaseSetupWizardActivity
import ru.nekit.android.qls.setupWizard.BaseSetupWizardStep.UNLOCK_SECRET
import ru.nekit.android.qls.setupWizard.ISetupWizardStep
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.*

class QuestSetupWizardActivity : BaseSetupWizardActivity() {

    private val questApplication
        get() = application as QuestLockScreenApplication
    override val setupWizard
        get() = questApplication.setupWizard
    override val eventListener
        get() = questApplication.getEventListener()

    @Subscribe
    fun listen(event: TransitionChoreographEvent) {
        if (event == TransitionChoreographEvent.ON_INIT)
            finish()
    }

    override fun showSetupWizardStep(step: ISetupWizardStep, vararg params: Any) {
        setupWizard.commitCurrentSetupStep(step)
        replaceFragment(
                when (step) {
                    START -> StartSetupWizardFragment.getInstance()
                    OVERLAY_PERMISSION -> OverlayPermissionFragment.getInstance()
                    SETUP_UNLOCK_SECRET, UNLOCK_SECRET, CHANGE_UNLOCK_SECRET ->
                        UnlockSecretFragment.getInstance().also {
                            it.setStep(step)
                        }
                    DEVICE_ADMIN -> SetupDeviceAdminFragment.instance
                    PUPIL_NAME_AND_SEX -> SetupPupilNameAndSexFragment.instance
                    //PUPIL_SEX -> SetupPupilSexFragment.instance
                    QTP_COMPLEXITY -> SetupQTPComplexityFragment.instance
                    PUPIL_AVATAR -> SetupPupilAvatarFragment.instance
                    CALL_PHONE_AND_READ_CONTACTS_PERMISSION ->
                        CallPhoneAndReadContactsPermissionFragment.instance
                    SETUP_PHONE_CONTACTS -> PhoneContactsFragment.instance
                    BIND_PARENT_CONTROL -> BindParentFragment.instance
                    VOICE_RECORD -> VoiceRecordFragment.instance
                    SUBSCRIBES -> SubscriptionsFragment.instance
                    SETTINGS -> SettingsFragment.instance
                    else -> TODO()
                }
        )
    }
}