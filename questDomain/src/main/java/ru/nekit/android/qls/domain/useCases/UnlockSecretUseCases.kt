package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.interactor.buildCompletableUseCaseFromRunnable
import ru.nekit.android.qls.domain.providers.UseCaseSupport

object UnlockSecretUseCases : UseCaseSupport() {

    private val unlockRepository
        get() = repositoryHolder.getUnlockSecretRepository()

    fun unlockSecretIsSet() = buildSingleUseCaseFromCallable {
        !unlockRepository.get().equals("")
    }

    fun setUnlockSecret(value: String): Completable = buildCompletableUseCaseFromRunnable {
        unlockRepository.set(value)
    }.concatWith(SessionUseCases.startAllSessions())

    fun checkUnlockSecret(value: String, body: (Boolean) -> Unit) = useSingleUseCase({
        Single.fromCallable {
            unlockRepository.get() == value
        }.flatMap { result ->
            if (result)
                SessionUseCases.startAllSessions().toSingleDefault(result)
            else
                Single.just(result)
        }
    }, body)
}


