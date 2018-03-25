package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.CompletableUseCase
import ru.nekit.android.domain.interactor.ParameterlessSingleUseCase
import ru.nekit.android.domain.interactor.SingleUseCase
import ru.nekit.android.qls.domain.providers.ITimeProvider
import ru.nekit.android.qls.domain.repository.IRepositoryHolder

class SetUnlockSecretUseCase(private val repository: IRepositoryHolder,
                             private val timeProvider: ITimeProvider,
                             scheduler: ISchedulerProvider? = null) :
        CompletableUseCase<String>(scheduler) {

    override fun build(parameter: String): Completable = Completable.fromCallable {
        repository.getUnlockSecretRepository().set(parameter)
    }.concatWith(StartAllSessionsUseCase(repository, timeProvider).build())
}

class UnlockSecretIsSetUseCase(private val repository: IRepositoryHolder,
                               scheduler: ISchedulerProvider? = null) : ParameterlessSingleUseCase<Boolean>(scheduler) {

    override fun build(): Single<Boolean> = Single.fromCallable {
        !repository.getUnlockSecretRepository().get().equals("")
    }

}

class CheckUnlockSecretUseCase(private val repository: IRepositoryHolder,
                               private val timeProvider: ITimeProvider,
                               scheduler: ISchedulerProvider? = null) :
        SingleUseCase<Boolean, String>(scheduler) {

    override fun build(parameter: String): Single<Boolean> =
            Single.fromCallable {
                repository.getUnlockSecretRepository().get() == parameter
            }.flatMap { result ->
                        StartAllSessionsUseCase(repository, timeProvider).build().toSingleDefault(result)
                    }
}