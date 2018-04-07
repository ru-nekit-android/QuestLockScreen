package ru.nekit.android.qls.domain.useCases

import ru.nekit.android.domain.interactor.singleUseCaseFromCallable
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.domain.interactor.useCompletableUseCaseFromRunnable
import ru.nekit.android.domain.interactor.useSingleUseCaseFromCallable
import ru.nekit.android.qls.domain.providers.DependenciesProvider
import ru.nekit.android.qls.domain.repository.IQuestSetupWizardSettingRepository

object SettingsUseCases : DependenciesProvider() {

    private val questSetupWizardSettingRepository: IQuestSetupWizardSettingRepository
        get() = repository.getQuestSetupWizardSettingRepository()

    fun setQuestSeriesLength(length: Int) = useCompletableUseCaseFromRunnable(schedulerProvider) {
        questSetupWizardSettingRepository.setQuestSeriesLength(length)
        repository.getTransitionChoreographRepository().questSeriesCounter.apply {
            startValue = length
            reset()
        }
    }

    fun getQuestSeriesLength(body: (Int) -> Unit) = questSeriesLength().use(body)

    internal fun questSeriesLength() = singleUseCaseFromCallable(schedulerProvider, {
        questSetupWizardSettingRepository.getQuestSeriesLength()
    })

    fun showUnlockKeyHelpOnConsume(body: (Boolean) -> Unit) = useSingleUseCaseFromCallable(schedulerProvider, {
        questSetupWizardSettingRepository.showUnlockKeyHelpOnConsume
    }, body)

    fun setShowUnlockKeyHelpOnConsume(value: Boolean) = useCompletableUseCaseFromRunnable(schedulerProvider, {
        questSetupWizardSettingRepository.showUnlockKeyHelpOnConsume = value
    })

}