package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.CompletableUseCase
import ru.nekit.android.domain.interactor.ParameterlessCompletableUseCase
import ru.nekit.android.domain.interactor.SingleUseCase
import ru.nekit.android.qls.domain.model.SessionType
import ru.nekit.android.qls.domain.providers.ITimeProvider
import ru.nekit.android.qls.domain.repository.IRepositoryHolder

class SessionTypeNameProvider {

    companion object {
        fun getName(sessionType: SessionType): String {
            return String.format("session.%s", sessionType.name)
        }
    }

}

class StartSessionUseCase(private val repository: IRepositoryHolder,
                          private val timeProvider: ITimeProvider,
                          scheduler: ISchedulerProvider? = null) :
        CompletableUseCase<SessionType>(scheduler) {

    override fun build(parameter: SessionType): Completable =
            Completable.fromRunnable {
                repository.getSessionRepository().set(SessionTypeNameProvider.getName(parameter),
                        timeProvider.getCurrentTime())
            }
}


class StartAllSessionsUseCase(private val repository: IRepositoryHolder,
                              private val timeProvider: ITimeProvider,
                              scheduler: ISchedulerProvider? = null) :
        ParameterlessCompletableUseCase(scheduler) {

    override fun build(): Completable =
            Single.zip(ArrayList<Single<Boolean>>().also { list ->
                SessionType.values().forEach {
                    list += StartSessionUseCase(repository, timeProvider).build(it).toSingleDefault(true)
                }
            }, {}).toCompletable()
}


class CheckSessionValidationUseCase(private val repository: IRepositoryHolder,
                                    private val timeProvider: ITimeProvider,
                                    scheduler: ISchedulerProvider? = null) :
        SingleUseCase<Boolean, SessionType>(scheduler) {

    override fun build(parameter: SessionType): Single<Boolean> =
            Single.fromCallable {
                val sessionTime = repository.getSessionRepository().get(SessionTypeNameProvider.getName(parameter))
                sessionTime != 0L &&
                        (timeProvider.getCurrentTime() - sessionTime) <= parameter.expiredTime
            }

}