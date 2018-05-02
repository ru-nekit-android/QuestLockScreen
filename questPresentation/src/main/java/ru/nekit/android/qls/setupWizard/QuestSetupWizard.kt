package ru.nekit.android.qls.setupWizard

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.os.Build
import android.provider.Settings
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.QuestLockScreenApplication
import ru.nekit.android.qls.data.representation.getSkuId
import ru.nekit.android.qls.data.representation.getSkuType
import ru.nekit.android.qls.deviceAdminSupport.DeviceAdminComponent
import ru.nekit.android.qls.domain.model.PhoneContact
import ru.nekit.android.qls.domain.model.SKU
import ru.nekit.android.qls.domain.model.SKUPurchase
import ru.nekit.android.qls.domain.model.SessionType
import ru.nekit.android.qls.domain.useCases.*
import ru.nekit.android.qls.lockScreen.LockScreen
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.SETTINGS
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.START
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.StepFlag.HAS_PERMISSION
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.StepFlag.SETTINGS_PARENT
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.StepFlag.SETUP_WIZARD_PARENT
import ru.nekit.android.qls.setupWizard.billing.Billing
import ru.nekit.android.qls.setupWizard.view.QuestSetupWizardActivity
import ru.nekit.android.qls.shared.model.Complexity
import ru.nekit.android.qls.shared.model.Complexity.NORMAL
import ru.nekit.android.qls.shared.model.Pupil
import ru.nekit.android.qls.shared.model.PupilSex
import ru.nekit.android.utils.PhoneUtils
import ru.nekit.android.utils.SingletonHolder
import ru.nekit.android.utils.toSingle

class QuestSetupWizard private constructor(val application: QuestLockScreenApplication) :
        BaseSetupWizard(application, application.getQuestSetupWizardSettingRepository()) {

    val timeProvider
        get() = application.getTimeProvider()

    private val billing: Billing
        get() = application.getBilling()

    val deviceAdminComponent: ComponentName
        get() = ComponentName(context, DeviceAdminComponent::class.java)

    val pupil: Single<Optional<Pupil>>
        get() = PupilUseCases.getCurrentPupil()

    val phoneContacts: Single<List<PhoneContact>>
        get() = PhoneContactsUseCases.getPhoneContacts()

    lateinit var activity: Activity

    private fun getPurchasedSubscription(body: (SKUPurchase?) -> Unit) =
            SKUUseCases.purchasedSubscription().use { it -> body(it.data) }

    fun initiateSKUPurchaseFlow(sku: SKU) = getPurchasedSubscription { oldSku ->
        billing.initiatePurchaseFlow(activity, sku.getSkuId(application),
                oldSku?.sku?.getSkuId(application), sku.skuType.getSkuType())
    }

    private fun overlayPermissionIsSet() =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)

    private fun allPermissionsIsGranted(): Boolean = QuestSetupWizardStep.values().all {
        it.permissionIsSet(this)
    }

    fun addPhoneContact(value: PhoneContact): Completable =
            PhoneContactsUseCases.addPhoneContact(value)

    fun removePhoneContact(value: PhoneContact): Completable =
            PhoneContactsUseCases.removePhoneContact(value)

    fun start() {
        context.startActivity(getStartIntent(true))
    }

    fun getStartIntent(newTask: Boolean): Intent =
            Intent(context, QuestSetupWizardActivity::class.java).apply {
                if (newTask) {
                    flags = FLAG_ACTIVITY_NEW_TASK
                } else {
                    flags = FLAG_ACTIVITY_CLEAR_TOP
                    flags = FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                    flags = FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK
                }
            }

    private fun unlockPasswordIsSet(): Single<Boolean> = UnlockSecretUseCases.unlockSecretIsSet()

    /*
    private fun knoxIsSupport(): Boolean {
        val packageManager = context.packageManager
        return packageManager.hasSystemFeature("com.sec.android.mdm")
    }
    */

    override fun setupIsComplete(): Boolean {
        return super.setupIsComplete() && allPermissionsIsGranted()
    }

    fun deviceAdminPermissionsIsActive(): Boolean =
            (context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager).isAdminActive(deviceAdminComponent)

    fun setPupilName(name: String): Single<Boolean> =
            updatePupilParameter { it.name = name }

    fun setPupilSex(sex: PupilSex): Single<Boolean> =
            updatePupilParameter { it.sex = sex }

    fun setPupilAvatar(avatar: String): Single<Boolean> =
            updatePupilParameter { it.avatar = avatar }

    fun setQTPComplexity(complexity: Complexity?): Single<Boolean> =
            updatePupilParameter { it.complexity = complexity ?: NORMAL }

    private fun updatePupilParameter(body: (Pupil) -> Unit): Single<Boolean> =
            PupilUseCases.updatePupil(body)

    override fun calculateNextStep(): Single<ISetupWizardStep> =
            Flowable.fromIterable(QuestSetupWizardStep.values().toList()).concatMap { step ->
                if (setupIsComplete()) {
                    val flags = step.flags
                    if (flags and HAS_PERMISSION != 0 && flags and SETUP_WIZARD_PARENT != 0)
                        step.stepIsComplete(this).map { Optional(if (it) null else step) }.toFlowable()
                    else
                        Flowable.just(Optional())

                } else
                    if (currentStep == null)
                        Flowable.just(Optional(step))
                    else
                        if (step.flags and SETUP_WIZARD_PARENT != 0)
                            step.stepIsComplete(this).map { Optional(if (it) null else step) }.toFlowable()
                        else
                            Flowable.just(Optional())
            }.filter { it.isNotEmpty() }.map { it.nonNullData }.first(SETTINGS).cast(ISetupWizardStep::class.java)

    override fun getName(): String = NAME

    override fun needLogin(step: ISetupWizardStep): Single<Boolean> =
            if (step.needLogin())
                SessionUseCases.checkSessionValidation(SessionType.SETUP_WIZARD).map { !it }
            else false.toSingle()


    fun commitCurrentSetupStep(value: ISetupWizardStep) {
        if (value == SETTINGS) {
            completeSetupWizard()
        }
        if (value != START) {
            startSetupWizard()
        }
        currentStep = value
    }

    override fun completeSetupWizard() {
        super.completeSetupWizard()
        LockScreen.startForSetupWizard(application)
    }

    fun setUnlockSecret(value: String): Completable = UnlockSecretUseCases.setUnlockSecret(value)

    fun checkUnlockSecret(password: String, body: (Boolean) -> Unit): Unit =
            UnlockSecretUseCases.checkUnlockSecret(password, body)

    fun switchOff() = LockScreen.switchOff(context)

    fun phoneIsAvailable(): Boolean {
        return PhoneUtils.phoneIsAvailable(context)
    }

    fun lockScreenIsActive(): Boolean {
        return LockScreen.isActive(context)
    }

    fun createBindCode(): Single<String> =
            pupil.flatMap {
                Single.just(if (it.isEmpty()) "" else
                    with(it.nonNullData) {
                        String.format(BIND_CODE_PATTERN, uuid, name)
                    }
                )
            }

    fun createPupilAndSetAsCurrent(): Single<Boolean> = PupilUseCases.createPupilAndSetAsCurrent()

    /*
    fun setDeviceAdminRemovable(visibility: Boolean) {
        if (knoxIsSupport()) {
            /*EnterpriseLicenseManager enterpriseLicenseManager = EnterpriseLicenseManager.getInstance(context);
            enterpriseLicenseManager.activateLicense();
            EnterpriseDeviceManager edm = new EnterpriseDeviceManager(context);
            edm.setAdminRemovable(visibility);*/
        }
    }
    */

    enum class QuestSetupWizardStep(val flags: Int) : ISetupWizardStep {

        START(SETUP_WIZARD_PARENT),
        SETUP_UNLOCK_SECRET(SETUP_WIZARD_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                return setupWizard.unlockPasswordIsSet()
            }
        },
        DEVICE_ADMIN(SETUP_WIZARD_PARENT or HAS_PERMISSION) {
            override fun permissionIsSet(setupWizard: QuestSetupWizard): Boolean {
                return setupWizard.deviceAdminPermissionsIsActive()
            }
        },
        /*SAMSUNG_ENTERPRISE(SETUP_WIZARD_PARENT | HAS_PERMISSION) {
            @Override
            public boolean stepIsComplete(@NonNull QuestSetupWizard setupWizard) {
                return false;
            }
        },*/
        OVERLAY_PERMISSION(SETUP_WIZARD_PARENT or HAS_PERMISSION) {
            override fun permissionIsSet(setupWizard: QuestSetupWizard): Boolean {
                return setupWizard.overlayPermissionIsSet()
            }
        },
        BIND_PARENT_CONTROL(SETTINGS_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                throw UnsupportedOperationException()
            }
        },
        CHANGE_UNLOCK_SECRET(SETTINGS_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                throw UnsupportedOperationException()
            }
        },
        CALL_PHONE_AND_READ_CONTACTS_PERMISSION(SETUP_WIZARD_PARENT or HAS_PERMISSION) {
            override fun permissionIsSet(setupWizard: QuestSetupWizard): Boolean {
                return if (setupWizard.phoneIsAvailable()) setupWizard.callPhonePermissionIsGranted()
                        && setupWizard.readContactsPermissionIsGranted()
                else true
            }
        },
        PUPIL_NAME(SETUP_WIZARD_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                return setupWizard.pupil.map {
                    it.isNotEmpty() && it.nonNullData.name?.isNotEmpty() != null
                }
            }
        },
        PUPIL_SEX(SETUP_WIZARD_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                return setupWizard.pupil.map {
                    it.data?.sex != null
                }
            }
        },
        QTP_COMPLEXITY(SETUP_WIZARD_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                return setupWizard.pupil.map { it.data?.complexity != null }
            }
        },
        PUPIL_AVATAR(SETUP_WIZARD_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                return setupWizard.pupil.map { it.data?.avatar != null }
            }
        },
        SETUP_PHONE_CONTACTS(SETTINGS_PARENT or SETUP_WIZARD_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> =
                    setupWizard.phoneContacts.map {
                        it.isNotEmpty() && setupWizard.phoneIsAvailable()
                    }
        },
        VOICE_RECORD(SETTINGS_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                throw UnsupportedOperationException()
            }
        },
        SUBSCRIBES(SETTINGS_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                throw UnsupportedOperationException()
            }
        },
        SETTINGS(SETTINGS_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                throw UnsupportedOperationException()
            }
        };

        override fun needLogin() = !(this == SETUP_UNLOCK_SECRET || this == START)

        override fun needInternetConnection(): Boolean {
            return this == BIND_PARENT_CONTROL
        }

        open fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> =
                (if (flags and HAS_PERMISSION != 0)
                    permissionIsSet(setupWizard)
                else
                    true).toSingle()

        open fun permissionIsSet(setupWizard: QuestSetupWizard): Boolean = true
    }

    internal object StepFlag {

        const val SETUP_WIZARD_PARENT = 1
        const val SETTINGS_PARENT = 2
        //its can be reset or unset
        const val HAS_PERMISSION = 4

    }

    companion object : SingletonHolder<QuestSetupWizard, QuestLockScreenApplication>(::QuestSetupWizard) {

        private const val NAME = "questSetupWizard"

    }
}