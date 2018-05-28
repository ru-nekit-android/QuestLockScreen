package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.ParameterlessCompletableUseCase
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.providers.IUUIDProvider
import ru.nekit.android.qls.domain.providers.UseCaseSupport
import ru.nekit.android.qls.domain.repository.IPupilRepository
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.qls.shared.model.Complexity
import ru.nekit.android.qls.shared.model.Pupil
import ru.nekit.android.qls.shared.model.PupilSex
import ru.nekit.android.utils.toSingle

object PupilUseCases : UseCaseSupport() {

    lateinit var uuidProvider: IUUIDProvider

    private val pupilRepository: IPupilRepository
        get() = repositoryHolder.getPupilRepository()

    private val questSetupWizardRepository
        get() = repositoryHolder.getSettingsRepository()

    fun createPupil(pupilName: String, pupilSex: PupilSex) = buildSingleUseCase {
        getCurrentPupil().flatMap {
            if (it.isEmpty())
                Pupil(uuidProvider.generateUuid(),
                        pupilName,
                        pupilSex,
                        if (!questSetupWizardRepository.useQTPComplexity)
                            Complexity.NORMAL else null
                ).let { pupil ->
                    pupilRepository.create(pupil)
                            .andThen(pupilRepository.setCurrentPupil(pupil))
                            .andThen(repositoryHolder.getPupilStatisticsRepository().create(pupil))
                }
            else
                false.toSingle()
        }
    }

    private fun currentPupilUseCase() = singleUseCase {
        Single.just(Optional(PupilHolder.pupil)).flatMap {
            if (it.isEmpty())
                pupilRepository.getCurrentPupil().doOnSuccess {
                    PupilHolder.pupil = it.data
                }
            else
                Single.just(it)
        }
    }

    fun useCurrentPupil(body: (Pupil) -> Unit) = currentPupilUseCase().use {
        body(it.nonNullData)
    }

    fun getCurrentPupil() = currentPupilUseCase().buildAsync()

    fun updatePupil(body: (Pupil) -> Unit) = buildSingleUseCase {
        getCurrentPupil().flatMap {
            if (it.isNotEmpty()) {
                val pupil = it.nonNullData
                pupilRepository.update(pupil.also {
                    body(pupil)
                }).toSingleDefault(true)
            } else {
                false.toSingle()
            }
        }
    }
}

internal fun <T> pupilFlatMap(body: (Pupil) -> Single<T>) =
        PupilUseCases.getCurrentPupil().map { it.data }.flatMap(body)

internal fun pupilFlatMapCompletable(body: (Pupil) -> Completable) =
        PupilUseCases.getCurrentPupil().map { it.data }.flatMapCompletable(body)

class DropCurrentPupilUseCase(private val repository: IRepositoryHolder,
                              scheduler: ISchedulerProvider? = null) :
        ParameterlessCompletableUseCase(scheduler) {

    override fun build(): Completable = repository.getPupilRepository().dropCurrentPupil()

}

private object PupilHolder {

    var pupil: Pupil? = null

}