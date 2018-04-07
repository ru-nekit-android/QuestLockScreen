package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.CompletableUseCase
import ru.nekit.android.domain.interactor.SingleUseCase
import ru.nekit.android.domain.interactor.buildSingleUseCase
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.PhoneContact
import ru.nekit.android.qls.domain.providers.DependenciesProvider
import ru.nekit.android.qls.domain.repository.IPupilRepository
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.qls.shared.model.Pupil

object PhoneContactsUseCases : DependenciesProvider() {

    private val phoneContactRepository
        get() = repository.getPhoneContactRepository()

    fun getPhoneContacts(): Single<List<PhoneContact>> = buildSingleUseCase(schedulerProvider) {
        pupil(repository) {
            getPhoneContactsForPupil(it)
        }
    }

    private fun getPhoneContactsForPupil(pupil: Pupil): Single<List<PhoneContact>> =
            phoneContactRepository.getAll(pupil).map { it.toMutableList() }.map {
                it.addAll(0, repository.getEmergencyPhoneRepository().getPhoneContacts())
                it
            }.map { it.toList() }

}

class GetPhoneContactByIdUseCase(private val repository: IRepositoryHolder,
                                 scheduler: ISchedulerProvider? = null) : SingleUseCase<Optional<PhoneContact>, Long>(scheduler) {

    override fun build(parameter: Long): Single<Optional<PhoneContact>> =
            repository.getPupilRepository().getCurrentPupil().flatMap {
                when {
                    it.isEmpty() -> Single.error(IPupilRepository.CurrentPupilIsNotSet())
                    parameter <= PhoneContact.EMERGENCY_PHONE_NUMBER.contactId -> Single.just(Optional(repository.getEmergencyPhoneRepository().getPhoneContacts()[(Math.abs(parameter) -
                            Math.abs(PhoneContact.EMERGENCY_PHONE_NUMBER.contactId)).toInt()]))
                    else -> repository.getPhoneContactRepository().getByContactId(it.nonNullData, parameter)
                }
            }
}

class AddPhoneContactUseCase(private val repository: IRepositoryHolder,
                             scheduler: ISchedulerProvider? = null) :
        CompletableUseCase<PhoneContact>(scheduler) {

    override fun build(parameter: PhoneContact) =
            PhoneContactHelper.buildUseCase(repository) {
                repository.getPhoneContactRepository().add(it, parameter)
            }

}

class RemovePhoneContactUseCase(private val repository: IRepositoryHolder,
                                scheduler: ISchedulerProvider? = null) :
        CompletableUseCase<PhoneContact>(scheduler) {

    override fun build(parameter: PhoneContact) =
            PhoneContactHelper.buildUseCase(repository) {
                repository.getPhoneContactRepository().remove(it, parameter)
            }

}

object PhoneContactHelper {

    fun buildUseCase(repository: IRepositoryHolder, action: (Pupil) -> Completable): Completable =
            pupilAsCompletable(repository) {
                action(it)
            }
}