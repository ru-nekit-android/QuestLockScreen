package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.Function
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.ParameterlessCompletableUseCase
import ru.nekit.android.domain.interactor.ParameterlessSingleUseCase
import ru.nekit.android.domain.interactor.SingleUseCase
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.qls.shared.model.Pupil
import ru.nekit.android.utils.toSingle

class CreatePupilAndSetAsCurrentUseCase(
        private val repository: IRepositoryHolder,
        private val uuidProvider: IUUIDProvider,
        scheduler: ISchedulerProvider? = null) : ParameterlessSingleUseCase<Boolean>(scheduler) {

    override fun build(): Single<Boolean> {
        return GetCurrentPupilUseCase(repository).build().flatMap {
            if (it.isEmpty()) {
                val pupil = Pupil(uuidProvider.provideUuid())
                repository.getPupilRepository().create(pupil).andThen(
                        repository.getPupilRepository().setCurrentPupil(pupil).andThen(
                                repository.getPupilStatisticsRepository().create(pupil)
                        )
                )
            } else {
                false.toSingle()
            }
        }
    }
}

class UpdatePupilUseCase(private val repository: IRepositoryHolder,
                         private val scheduler: ISchedulerProvider? = null) :
        SingleUseCase<Boolean, Function<Pupil, Unit>>(scheduler) {

    override fun build(parameter: Function<Pupil, Unit>): Single<Boolean> =
            GetCurrentPupilUseCase(repository, scheduler).build().flatMap {
                if (it.isNotEmpty()) {
                    val pupil = it.nonNullData
                    repository.getPupilRepository().update(pupil.also {
                        parameter.apply(pupil)
                    }).toSingleDefault(true)
                } else {
                    false.toSingle()
                }
            }

}

class GetCurrentPupilUseCase(private val repository: IRepositoryHolder,
                             scheduler: ISchedulerProvider? = null) :
        ParameterlessSingleUseCase<Optional<Pupil>>(scheduler) {

    override fun build(): Single<Optional<Pupil>> =
            Single.just(Optional(PupilHolder.pupil)).flatMap {
                if (it.isEmpty())
                    repository.getPupilRepository().getCurrentPupil().doOnSuccess {
                        PupilHolder.pupil = it.data
                    }
                else
                    Single.just(it)
            }
}

internal fun <T> pupil(repository: IRepositoryHolder, body: (Pupil) -> Single<T>) =
        GetCurrentPupilUseCase(repository, null).build().map { it.data }.flatMap(body)

internal fun pupilAsCompletable(repository: IRepositoryHolder, body: (Pupil) -> Completable) =
        GetCurrentPupilUseCase(repository, null).build().map { it.data }.flatMapCompletable(body)

class DropCurrentPupilUseCase(private val repository: IRepositoryHolder,
                              scheduler: ISchedulerProvider? = null) :
        ParameterlessCompletableUseCase(scheduler) {

    override fun build(): Completable = repository.getPupilRepository().dropCurrentPupil()

}

private object PupilHolder {

    var pupil: Pupil? = null

}

interface IUUIDProvider {

    fun provideUuid(): String

}