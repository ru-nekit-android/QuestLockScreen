package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import ru.nekit.android.domain.interactor.buildCompletableUseCaseFromRunnable
import ru.nekit.android.qls.domain.model.SessionType
import ru.nekit.android.qls.domain.providers.UseCaseSupport
import ru.nekit.android.qls.domain.repository.ISessionRepository

object SessionUseCases : UseCaseSupport() {

    private val sessionRepository: ISessionRepository
        get() = repository.getSessionRepository()

    fun checkSessionValidation(sessionType: SessionType) = buildSingleUseCaseFromCallable {
        val sessionTime = sessionRepository.get(getName(sessionType))
        sessionTime != 0L &&
                (timeProvider.getCurrentTime() - sessionTime) <= sessionType.expiredTime
    }

    private fun startSession(session: SessionType) = buildCompletableUseCaseFromRunnable {
        sessionRepository.set(getName(session), timeProvider.getCurrentTime())
    }

    fun startAllSessions() = buildCompletableUseCase {
        Completable.concat(
                SessionType.values().map {
                    startSession(it)
                }
        )
    }

    fun getName(sessionType: SessionType) = String.format("session.%s", sessionType.name)

}