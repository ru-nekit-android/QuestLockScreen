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
                    START -> StartFragment.getInstance()
                    OVERLAY_PERMISSION -> OverlayPermissionFragment.getInstance()
                    SET_UNLOCK_SECRET, UNLOCK_SECRET, CHANGE_UNLOCK_SECRET ->
                        UnlockSecretFragment.getInstance().also {
                            it.step = step
                        }
                    DEVICE_ADMIN -> DeviceAdminFragment.getInstance()
                    PUPIL_NAME_AND_SEX -> PupilNameAndSexFragment.getInstance()
                    QTP_COMPLEXITY -> QTPComplexityFragment.getInstance()
                    PUPIL_AVATAR -> PupilAvatarFragment.getInstance()
                    CALL_PHONE_AND_READ_CONTACTS_PERMISSION ->
                        CallPhoneAndReadContactsPermissionFragment.getInstance()
                    SETUP_PHONE_CONTACTS -> PhoneContactsFragment.getInstance()
                    VOICE_RECORD -> VoiceRecordFragment.getInstance()
                    SUBSCRIBES -> SubscriptionsFragment.getInstance()
                    SETTINGS -> SettingsFragment.getInstance()
                //PUPIL_SEX -> SetupPupilSexFragment.instance
                //BIND_PARENT_CONTROL -> BindParentFragment.instance
                    else -> TODO()
                }
        )
    }
}