package ru.nekit.android.qls.domain.useCases

import ru.nekit.android.domain.interactor.singleUseCaseFromCallable
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.domain.interactor.useCompletableUseCaseFromRunnable
import ru.nekit.android.qls.domain.providers.DependenciesProvider

object SettingsUseCases : DependenciesProvider() {

    fun setQuestSeriesLength(length: Int) = useCompletableUseCaseFromRunnable(schedulerProvider) {
        repository.getQuestSetupWizardSettingRepository().setQuestSeriesLength(length)
    }

    fun getQuestSeriesLength(body: (Int) -> Unit) = questSeriesLength().use(body)

    internal fun questSeriesLength() = singleUseCaseFromCallable(schedulerProvider, {
        Math.max(LENGTH_BY_DEFAULT, repository.getQuestSetupWizardSettingRepository().getQuestSeriesLength())
    })

    private const val LENGTH_BY_DEFAULT = 1

}