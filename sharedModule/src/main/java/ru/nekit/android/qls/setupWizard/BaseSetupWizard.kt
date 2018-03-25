package ru.nekit.android.qls.setupWizard

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.nekit.android.qls.shared.model.Pupil
import ru.nekit.android.qls.shared.repository.ISetupWizardSettingsRepository

abstract class BaseSetupWizard(protected val context: Context,
                               private val settingsStorage: ISetupWizardSettingsRepository) {

    private val stepStack: MutableList<ISetupWizardStep>

    init {
        stepStack = ArrayList()
    }

    var currentStep: ISetupWizardStep? = null

    internal val nextStep: Single<ISetupWizardStep>
        get() = Single.just(calculateNextStep()).flatMap { step ->
            Single.zip(needLogin(step), Single.just(step.needInternetConnection() && !internetIsConnected()),
                    BiFunction { needLogin: Boolean, needInternetConnection: Boolean ->
                        if (needLogin) {
                            stepStack.clear()
                            stepStack.add(BaseSetupWizardStep.UNLOCK_SECRET)
                        }
                        if (needInternetConnection) {
                            stepStack.add(BaseSetupWizardStep.SETUP_INTERNET_CONNECTION)
                        }
                        step
                    }).map {
                val size = stepStack.size
                if (size == 0 || size > 0 && stepStack[size - 1] !== it) {
                    stepStack.add(it)
                }
                stepStack.removeAt(0)
            }
        }

    abstract fun getName(): String

    private fun internetIsConnected(): Boolean {
        return true
    }

    protected abstract fun calculateNextStep(): ISetupWizardStep

    fun setupIsStart(): Boolean {
        return settingsStorage.setupWizardIsStart(getName())
    }

    fun setupIsComplete(): Boolean {
        return settingsStorage.setupWizardIsComplete(getName())
    }

    protected fun startSetupWizard() {
        settingsStorage.startSetupWizard(getName(), true)
    }

    protected fun completeSetupWizard() {
        settingsStorage.completeSetupWizard(getName(), true)
    }

    protected fun createPupilFromBindCode(bindCode: String): Pupil {
        val values = TextUtils.split(bindCode, ":")
        return Pupil(values[1], values[2])
    }

    abstract fun needLogin(step: ISetupWizardStep): Single<Boolean>

    private fun permissionIsGranted(permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun permissionIsGranted(permissions: Array<String>): Boolean {
        var result = true
        for (permission in permissions) {
            result = result && permissionIsGranted(permission)
            if (!result) {
                break
            }
        }
        return result
    }

    fun callPhonePermissionIsGranted(): Boolean {
        return permissionIsGranted(Manifest.permission.CALL_PHONE)
    }

    fun readContactsPermissionIsGranted(): Boolean {
        return permissionIsGranted(Manifest.permission.READ_CONTACTS)
    }

    companion object {

        val UNLOCK_SECRET_MIN_SIZE = 4
        val BIND_CODE_PATTERN = "QLS:%s:%s"
    }

}