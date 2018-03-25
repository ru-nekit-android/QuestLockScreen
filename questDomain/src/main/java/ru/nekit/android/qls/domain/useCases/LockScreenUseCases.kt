package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.*
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.LockScreenStartType
import ru.nekit.android.qls.domain.model.LockScreenStartType.ON_NOTIFICATION_CLICK
import ru.nekit.android.qls.domain.model.LockScreenStartType.SETUP_WIZARD
import ru.nekit.android.qls.domain.providers.IEventSender
import ru.nekit.android.qls.domain.providers.ITimeProvider
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.utils.toSingle

object LockScreenUseCases {

    class start(private val repository: IRepositoryHolder,
                scheduler: ISchedulerProvider? = null) :
            CompletableUseCase<LockScreenStartType>(scheduler) {
        override fun build(parameter: LockScreenStartType): Completable =
                saveStartType(repository).build(parameter).concatWith(
                        Completable.fromRunnable {
                            if (parameter != SETUP_WIZARD)
                                switchOn(repository)
                        })
    }

    class showLockScreen(private val repository: IRepositoryHolder,
                         private val timeProvider: ITimeProvider,
                         scheduler: ISchedulerProvider? = null) : SingleUseCase<Boolean, LockScreenStartType>(scheduler) {
        override fun build(parameter: LockScreenStartType): Single<Boolean> =
                //true - show
                GetLastHistoryUseCase(repository).build().map { historyOpt ->
                    if (parameter != SETUP_WIZARD) {
                        if (parameter == ON_NOTIFICATION_CLICK) {
                            true
                        } else
                            if (repository.getQuestSetupWizardSettingRepository().skipAfterRightAnswer)
                                if (historyOpt.isNotEmpty())
                                    timeProvider.getCurrentTime() - historyOpt.nonNullData.timeStamp >
                                            repository.getQuestSetupWizardSettingRepository().timeForSkipAfterRightAnswer
                                else
                                    true
                            else
                                true
                    } else
                        false
                }.flatMap {
                            if (it)
                                saveStartType(repository).build(parameter).toSingleDefault(true)
                            else
                                false.toSingle()
                        }
    }

    fun switchOn(repository: IRepositoryHolder,
                 scheduler: ISchedulerProvider? = null) = completableUseCase(scheduler) {
        repository.getLockScreenRepository().switchOn(true)
    }

    fun switchOff(repository: IRepositoryHolder,
                  eventSender: IEventSender,
                  scheduler: ISchedulerProvider? = null) = completableUseCase(scheduler) {
        repository.getLockScreenRepository().switchOn(false)
        LockScreenHelper.hide(eventSender)
    }

    fun hide(eventSender: IEventSender, scheduler: ISchedulerProvider? = null) = useCompletableUseCase(scheduler, {
        SessionTimer.stop()
        LockScreenHelper.hide(eventSender)
    })

    fun isSwitchedOn(repository: IRepositoryHolder, scheduler: ISchedulerProvider? = null) =
            singleUseCase(scheduler) {
                repository.getLockScreenRepository().isSwitchedOn()
            }

    fun startIncomingCall(repository: IRepositoryHolder, scheduler: ISchedulerProvider?, body: () -> Unit) =
            useCompletableUseCase(scheduler, {
                repository.getLockScreenRepository().incomeCallInProcess(true)
            }, body)

    fun startOutgoingCall(repository: IRepositoryHolder, scheduler: ISchedulerProvider?, body: () -> Unit) =
            useCompletableUseCase(scheduler, {
                repository.getLockScreenRepository().outgoingCallInProcess(true)
            }, body)

    fun stopIncomingCall(repository: IRepositoryHolder, scheduler: ISchedulerProvider, body: () -> Unit) =
            predicatedUseSingleUseCase(scheduler, {
                repository.getLockScreenRepository().let {
                    it.incomeCallInProcess().let { result ->
                        if (result)
                            it.incomeCallInProcess(false)
                        result
                    }
                }
            }, body)

    fun stopOutgoingCall(repository: IRepositoryHolder, scheduler: ISchedulerProvider, body: () -> Unit) =
            predicatedUseSingleUseCase(scheduler, {
                repository.getLockScreenRepository().let {
                    it.outgoingCallInProcess().let { result ->
                        if (result)
                            it.outgoingCallInProcess(false)
                        result
                    }
                }
            }, body)

    class saveStartType(private val repository: IRepositoryHolder,
                        scheduler: ISchedulerProvider? = null) :
            CompletableUseCase<LockScreenStartType>(scheduler) {

        override fun build(parameter: LockScreenStartType): Completable = Completable.fromCallable {
            repository.getLockScreenRepository().saveStartType(parameter)
        }

    }

    class updateLastStartType(private val repository: IRepositoryHolder,
                              scheduler: ISchedulerProvider? = null) :
            CompletableUseCase<LockScreenStartType>(scheduler) {

        override fun build(parameter: LockScreenStartType): Completable = getLastStartType(repository).build().flatMapCompletable { lastStartTypeOpt ->
            if (lastStartTypeOpt.isNotEmpty() && lastStartTypeOpt.data == ON_NOTIFICATION_CLICK)
                Completable.fromRunnable {
                    repository.getLockScreenRepository().saveStartType(parameter)
                }
            else
                Completable.complete()
        }
    }

    fun getLastStartType(repository: IRepositoryHolder, scheduler: ISchedulerProvider? = null) =
            singleUseCase(scheduler) {
                Optional(repository.getLockScreenRepository().getLastStartType())
            }

    object LockScreenHelper {

        fun hide(eventSender: IEventSender) {
            eventSender.send(LockScreenEvent.ACTION_HIDE)
        }
    }

    enum class LockScreenEvent : IEvent {

        ACTION_HIDE;

        override val eventName: String = "${javaClass.name}::$name"

    }
}