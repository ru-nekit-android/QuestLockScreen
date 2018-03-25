package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.CompletableUseCase
import ru.nekit.android.domain.interactor.ParameterlessSingleUseCase
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.utils.toSingle

class GetQuestSeriesLength(private val repository: IRepositoryHolder,
                           scheduler: ISchedulerProvider? = null) : ParameterlessSingleUseCase<Int>(scheduler) {

    override fun build(): Single<Int> =
            Math.max(LENGTH_BY_DEFAULT, repository.getQuestSetupWizardSettingRepository().getQuestSeriesLength()).toSingle()

    companion object {
        const val LENGTH_BY_DEFAULT = 1
    }

}

class SetQuestSeriesLength(private val repository: IRepositoryHolder,
                           scheduler: ISchedulerProvider? = null) : CompletableUseCase<Int>(scheduler) {
    override fun build(parameter: Int): Completable = Completable.fromRunnable {
        repository.getQuestSetupWizardSettingRepository().setQuestSeriesLength(parameter)
    }

}