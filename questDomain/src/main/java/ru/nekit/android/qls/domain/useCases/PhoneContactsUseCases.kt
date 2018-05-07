package ru.nekit.android.qls.domain.useCases

import io.reactivex.Single
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.SingleUseCase
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.PhoneContact
import ru.nekit.android.qls.domain.providers.UseCaseSupport
import ru.nekit.android.qls.domain.repository.IPupilRepository
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.qls.shared.model.Pupil

object PhoneContactsUseCases : UseCaseSupport() {

    private val phoneContactRepository
        get() = repositoryHolder.getPhoneContactRepository()

    private fun getPhoneContactsForPupil(pupil: Pupil): Single<List<PhoneContact>> =
            phoneContactRepository.getAll(pupil).map { list ->
                ArrayList<PhoneContact>().also {
                    it.addAll(repositoryHolder.getEmergencyPhoneRepository().getPhoneContacts())
                    it.addAll(list)
                }
            }

    fun getPhoneContacts(): Single<List<PhoneContact>> = buildSingleUseCase {
        pupilFlatMap {
            getPhoneContactsForPupil(it)
        }
    }

    fun addPhoneContact(contact: PhoneContact) = buildCompletableUseCase {
        pupilFlatMapCompletable {
            phoneContactRepository.add(it, contact)
        }
    }

    fun removePhoneContact(contact: PhoneContact) = buildCompletableUseCase {
        pupilFlatMapCompletable {
            phoneContactRepository.remove(it, contact)
        }
    }

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


