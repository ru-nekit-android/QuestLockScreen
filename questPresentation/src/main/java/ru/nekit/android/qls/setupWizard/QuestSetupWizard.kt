package ru.nekit.android.qls.setupWizard

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.os.Build
import android.provider.Settings
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.QuestLockScreenApplication
import ru.nekit.android.qls.deviceAdminSupport.DeviceAdminComponent
import ru.nekit.android.qls.domain.model.PhoneContact
import ru.nekit.android.qls.domain.model.SessionType
import ru.nekit.android.qls.domain.useCases.*
import ru.nekit.android.qls.lockScreen.LockScreen
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.SETTINGS
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.START
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.StepFlag.CAN_BE_RESET_AFTER_SET
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.StepFlag.SETTINGS_PARENT
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.StepFlag.SETUP_WIZARD_PARENT
import ru.nekit.android.qls.setupWizard.view.QuestSetupWizardActivity
import ru.nekit.android.qls.shared.model.Complexity
import ru.nekit.android.qls.shared.model.Pupil
import ru.nekit.android.qls.shared.model.PupilSex
import ru.nekit.android.qls.utils.PhoneUtils
import ru.nekit.android.utils.SingletonHolder
import ru.nekit.android.utils.toSingle

class QuestSetupWizard private constructor(private val application: QuestLockScreenApplication) :
        BaseSetupWizard(application, application.getQuestSetupWizardSettingRepository()) {

    val deviceAdminComponent: ComponentName
        get() = ComponentName(context, DeviceAdminComponent::class.java)

    //pupil can be null
    val pupil: Single<Optional<Pupil>>
        get() = GetCurrentPupilUseCase(application,
                application.getDefaultSchedulerProvider()).build()

    fun phoneContacts(body: (MutableList<PhoneContact>) -> Unit) =
            phoneContacts.subscribe { it ->
                body(it)
            }

    val phoneContacts: Single<MutableList<PhoneContact>>
        get() = GetPhoneContactsUseCase(application, application.getDefaultSchedulerProvider()).build()

    private fun overlayPermissionIsSet(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)
    }

    fun allPermissionsIsGranted(): Boolean {
        var permissionIsGranted = true
        if (PhoneUtils.phoneIsAvailable(context)) {
            permissionIsGranted = callPhonePermissionIsGranted() && readContactsPermissionIsGranted()
        }
        return permissionIsGranted && overlayPermissionIsSet()
    }

    fun addPhoneContact(value: PhoneContact): Completable =
            AddPhoneContactUseCase(application, application.getDefaultSchedulerProvider()).build(value)


    fun removePhoneContact(value: PhoneContact): Completable =
            RemovePhoneContactUseCase(application, application.getDefaultSchedulerProvider()).build(value)


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

    private fun unlockPasswordIsSet(): Single<Boolean> =
            UnlockSecretIsSetUseCase(application, application.getDefaultSchedulerProvider()).build()

    /*
    private fun knoxIsSupport(): Boolean {
        val packageManager = context.packageManager
        return packageManager.hasSystemFeature("com.sec.android.mdm")
    }
    */

    fun deviceAdminIsActive(): Boolean {
        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        return devicePolicyManager.isAdminActive(deviceAdminComponent)
    }

    fun setPupilName(name: String): Single<Boolean> =
            updatePupilParameter(Function<Pupil, Unit> { value -> value.name = name })

    fun setPupilSex(sex: PupilSex): Single<Boolean> =
            updatePupilParameter(Function { value -> value.sex = sex })

    fun setPupilAvatar(avatar: String): Single<Boolean> =
            updatePupilParameter(Function { value -> value.avatar = avatar })

    fun setQTPComplexity(complexity: Complexity?): Single<Boolean> =
            updatePupilParameter(Function { value -> value.complexity = complexity ?: Complexity.NORMAL })

    private fun updatePupilParameter(body: Function<Pupil, Unit>): Single<Boolean> =
            UpdatePupilUseCase(application,
                    application.getDefaultSchedulerProvider()
            ).build(body)


    override fun calculateNextStep(): QuestSetupWizardStep {
        /*Flowable.fromIterable(QuestSetupWizardStep.values().toMutableList()).flatMap {

            it -> Single.just(it).filter{
                 val flags = it.flags
                 flags with CAN_BE_RESET_AFTER_SET != 0 && flags with SETUP_WIZARD_PARENT != 0
              }.zipWith(it.stepIsComplete(this).toMaybe(), BiFunction{ a:ISetupWizardStep, b:Boolean -> if(!b) a}).toFlowable()
        }.firstElement()*/
        val steps = QuestSetupWizardStep.values()
        if (setupIsComplete()) {
            for (step in steps) {
                val flags = step.flags
                if (flags and CAN_BE_RESET_AFTER_SET != 0 && flags and SETUP_WIZARD_PARENT != 0) {
                    if (!step.stepIsComplete(this).blockingGet()) {
                        return step
                    }
                }
            }
        } else {
            for (step in steps) {
                if (currentStep == null) {
                    return step
                } else {
                    if (step.flags and SETUP_WIZARD_PARENT != 0) {
                        if (!step.stepIsComplete(this).blockingGet()) {
                            return step
                        }
                    }
                }
            }
        }
        return SETTINGS
    }

    override fun getName(): String {
        return NAME
    }

    override fun needLogin(step: ISetupWizardStep): Single<Boolean> {
        return CheckSessionValidationUseCase(application,
                application.getTimeProvider(),
                application.getDefaultSchedulerProvider()).build(SessionType.SETUP_WIZARD).zipWith(step.needLogin().toSingle(),
                BiFunction { a: Boolean, b: Boolean -> !a && b })
    }

    fun commitCurrentSetupStep(value: ISetupWizardStep) {
        if (value == SETTINGS) {
            completeSetupWizard()
        }
        if (value != START) {
            startSetupWizard()
        }
        currentStep = value
    }

    fun setUnlockSecret(value: String): Completable =
            SetUnlockSecretUseCase(application,
                    application.getTimeProvider(),
                    application.getDefaultSchedulerProvider()).build(value)

    fun checkUnlockSecret(password: String, body: (Boolean) -> Unit) = CheckUnlockSecretUseCase(application,
            application.getTimeProvider(),
            application.getDefaultSchedulerProvider()).use(password) {
        body(it)
    }


    fun switchOff() {
        LockScreen.switchOff(context)
    }

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

    fun createPupilAndSetAsCurrent(): Single<Boolean> {
        return CreatePupilAndSetAsCurrentUseCase(application,
                application.getUuidProvider(),
                application.getDefaultSchedulerProvider()).build()
    }

    /*
    fun setDeviceAdminRemovable(value: Boolean) {
        if (knoxIsSupport()) {
            /*EnterpriseLicenseManager enterpriseLicenseManager = EnterpriseLicenseManager.getInstance(context);
            enterpriseLicenseManager.activateLicense();
            EnterpriseDeviceManager edm = new EnterpriseDeviceManager(context);
            edm.setAdminRemovable(value);*/
        }
    }
    */

    enum class QuestSetupWizardStep(val flags: Int) : ISetupWizardStep {

        START(SETUP_WIZARD_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                return true.toSingle()
            }
        },
        SETUP_UNLOCK_SECRET(SETUP_WIZARD_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                return setupWizard.unlockPasswordIsSet()
            }
        },
        DEVICE_ADMIN(SETUP_WIZARD_PARENT or CAN_BE_RESET_AFTER_SET) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                return setupWizard.deviceAdminIsActive().toSingle()
            }
        },
        /*SAMSUNG_ENTERPRISE(SETUP_WIZARD_PARENT | CAN_BE_RESET_AFTER_SET) {
            @Override
            public boolean stepIsComplete(@NonNull QuestSetupWizard setupWizard) {
                return false;
            }
        },*/
        OVERLAY_PERMISSION(SETUP_WIZARD_PARENT or CAN_BE_RESET_AFTER_SET) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                return setupWizard.overlayPermissionIsSet().toSingle()
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
        CALL_PHONE_AND_READ_CONTACTS_PERMISSION(SETUP_WIZARD_PARENT or CAN_BE_RESET_AFTER_SET) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                return (setupWizard.phoneIsAvailable() && setupWizard.callPhonePermissionIsGranted()).toSingle()
            }
        },
        PUPIL_NAME(SETUP_WIZARD_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                return setupWizard.pupil.map { !it.isEmpty() && it.data?.name?.isNotEmpty() != null }
            }
        },
        PUPIL_SEX(SETUP_WIZARD_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                return setupWizard.pupil.map { it.data?.sex != null }
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
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                return Single.zip(setupWizard.phoneContacts,
                        setupWizard.phoneIsAvailable().toSingle(),
                        BiFunction { a: List<PhoneContact>,
                                     b: Boolean ->
                            a.isNotEmpty() && b
                        }
                )
            }
        },
        VOICE_RECORD(SETTINGS_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                throw UnsupportedOperationException()
            }
        },
        SETTINGS(SETTINGS_PARENT) {
            override fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean> {
                throw UnsupportedOperationException()
            }
        };

        override fun needLogin(): Boolean {
            return !(this == SETUP_UNLOCK_SECRET || this == START)
        }

        override fun needInternetConnection(): Boolean {
            return this == BIND_PARENT_CONTROL
        }

        abstract fun stepIsComplete(setupWizard: QuestSetupWizard): Single<Boolean>
    }

    internal object StepFlag {

        val SETUP_WIZARD_PARENT = 1
        val SETTINGS_PARENT = 2
        val CAN_BE_RESET_AFTER_SET = 4

    }

    companion object : SingletonHolder<QuestSetupWizard, QuestLockScreenApplication>(::QuestSetupWizard) {

        private val NAME = "questSetupWizard"

    }
}