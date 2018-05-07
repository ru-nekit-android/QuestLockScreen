package ru.nekit.android.qls.domain.useCases

import io.reactivex.Single
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.qls.domain.providers.IPhoneProvider
import ru.nekit.android.qls.domain.providers.UseCaseSupport
import ru.nekit.android.qls.domain.repository.IQuestSetupWizardSettingRepository

object SetupWizardUseCases : UseCaseSupport() {

    lateinit var phoneProvider: IPhoneProvider

    private val questSetupWizardSettingsRepository
        get() = repositoryHolder.getQuestSetupWizardSettingRepository()

    fun setupIsComplete(body: (Boolean) -> Unit) = setupIsComplete().use(body)

    fun setupIsComplete() = singleUseCaseFromCallable {
        questSetupWizardSettingsRepository.setupWizardIsComplete()
    }

    fun setupIsStart(body: (Boolean) -> Unit) = useSingleUseCaseFromCallable({
        questSetupWizardSettingsRepository.setupWizardIsStart()
    }, body)

    fun startSetupWizard() = useCompletableUseCaseFromRunnable {
        questSetupWizardSettingsRepository.startSetupWizard(true)
    }

    fun completeSetupWizard() = useCompletableUseCaseFromRunnable {
        questSetupWizardSettingsRepository.completeSetupWizard(true)
    }

    fun callPhonePermissionIsGranted(): Single<Boolean> = buildSingleUseCaseFromCallable {
        phoneProvider.callPhonePermissionIsGranted()
    }

    private val questSetupWizardSettingRepository: IQuestSetupWizardSettingRepository
        get() = repositoryHolder.getQuestSetupWizardSettingRepository()

    fun setQuestSeriesLength(lengthValue: Int) = useCompletableUseCaseFromRunnable {
        questSetupWizardSettingRepository.setQuestSeriesLength(lengthValue)
        repositoryHolder.getTransitionChoreographRepository().questSeriesCounter.apply {
            startValue = lengthValue
            //reset only if value of counter is greater or equal than new length value
            if (value >= lengthValue)
                reset()
        }
    }

    fun getQuestSeriesLength(body: (Int) -> Unit) = questSeriesLength().use(body)

    internal fun questSeriesLength() = singleUseCaseFromCallable {
        questSetupWizardSettingRepository.getQuestSeriesLength()
    }

    fun showHelpOnUnlockKeyConsume(body: (Boolean) -> Unit) = useSingleUseCaseFromCallable({
        questSetupWizardSettingRepository.showUnlockKeyHelpOnConsume
    }, body)

    fun setShowHelpOnUnlockKeyConsume(value: Boolean) = useCompletableUseCaseFromRunnable {
        questSetupWizardSettingRepository.showUnlockKeyHelpOnConsume = value
    }

}