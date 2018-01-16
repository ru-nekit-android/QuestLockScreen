package ru.nekit.android.domain.qls.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.CompletableUseCase
import ru.nekit.android.domain.interactor.SingleUseCase
import ru.nekit.android.domain.qls.model.ktComplexity
import ru.nekit.android.domain.qls.model.ktPupil
import ru.nekit.android.domain.qls.model.ktPupilSex
import ru.nekit.android.domain.qls.repository.IPupilRepository

class RegisterPupil(
        private val repository: IPupilRepository,
        private val uuidProvider: UUIDProvider,
        scheduler: ISchedulerProvider? = null
) : CompletableUseCase<RegisterPupilParameter>(scheduler) {
    override fun buildUseCase(parameter: RegisterPupilParameter?): Completable {
        val pupil = ktPupil(uuidProvider.provideUuid(),
                parameter!!.name,
                parameter.sex,
                parameter.complexity,
                ""
        )
        return repository.create(pupil).andThen {
            repository.setCurrentPupil(pupil)
        }
    }
}

class UpdatePupil(private val repository: IPupilRepository,
                  scheduler: ISchedulerProvider? = null
) : CompletableUseCase<ktPupil>(scheduler) {
    override fun buildUseCase(parameter: ktPupil?): Completable =
            repository.update(parameter!!)
}

class CurrentPupil(private val repository: IPupilRepository,
                   scheduler: ISchedulerProvider? = null
) : SingleUseCase<ktPupil, Unit>(scheduler) {
    @Throws(CurrentPupilIsNotSet::class)
    override fun buildUseCase(parameter: Unit?): Single<ktPupil> =
            repository.getCurrentPupil().flatMap {
                if (it.data != null)
                    Single.error(CurrentPupilIsNotSet())
                else
                    Single.just(it.data)
            }
}

class UpdateAvatar(private val repository: IPupilRepository,
                   scheduler: ISchedulerProvider? = null
) : CompletableUseCase<String>(scheduler) {
    override fun buildUseCase(avatar: String?): Completable =
            repository.getCurrentPupil().flatMapCompletable {
                Completable.fromCallable {
                    it.data!!.avatar = avatar!!
                    repository.update(it.data!!)
                }
            }
}

class CurrentPupilIsNotSet : Throwable("Current pupil is not set")

data class RegisterPupilParameter(val name: String, val sex: ktPupilSex, val complexity: ktComplexity)

interface UUIDProvider {

    fun provideUuid(): String

}