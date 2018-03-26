package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.*
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.LockScreenStartType
import ru.nekit.android.qls.domain.model.LockScreenStartType.*
import ru.nekit.android.qls.domain.providers.IDependenciesProvider
import ru.nekit.android.qls.domain.providers.IEventSender
import ru.nekit.android.qls.domain.providers.ITimeProvider
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.utils.doIfOrComplete

object LockScreenUseCases : IDependenciesProvider {

    override lateinit var repository: IRepositoryHolder
    override lateinit var schedulerProvider: ISchedulerProvider
    override lateinit var timeProvider: ITimeProvider
    override lateinit var eventSender: IEventSender

    private val lockScreenRepository
        get() = repository.getLockScreenRepository()

    fun start(parameter: LockScreenStartType, useBody: () -> Unit) =
            useCompletableUseCase(parameter, schedulerProvider, {
                saveStartType(parameter).concatWith(
                        Completable.fromRunnable {
                            if (parameter != SETUP_WIZARD)
                                switchOn()
                        })
            }, useBody)

    fun showLockScreen(parameter: LockScreenStartType,
                       useBody: () -> Unit) =
            useCompletableUseCase(parameter, schedulerProvider, {
                if (parameter != SETUP_WIZARD)
                    GetLastHistoryUseCase(repository).build().map { historyOpt ->
                        if (parameter == ON_NOTIFICATION_CLICK || parameter == EXPLICIT) true
                        else
                            if (repository.getQuestSetupWizardSettingRepository().skipAfterRightAnswer)
                                if (historyOpt.isNotEmpty())
                                    timeProvider.getCurrentTime() - historyOpt.nonNullData.timeStamp >
                                            repository.getQuestSetupWizardSettingRepository().timeForSkipAfterRightAnswer
                                else true
                            else true
                    }.flatMapCompletable { show ->
                                if (show)
                                    saveStartType(parameter)
                                else
                                    Completable.never()
                            }
                else
                    Completable.never()
            }, useBody)

    private fun switchOn() = completableUseCaseFromRunnable(schedulerProvider) {
        lockScreenRepository.switchOn(true)
    }

    fun switchOff() = buildCompletableUseCaseFromRunnable(schedulerProvider) {
        lockScreenRepository.switchOn(false)
        sendHideEvent(eventSender)
    }

    fun hide() = useCompletableUseCaseFromRunnable(schedulerProvider, {
        SessionTimer.stop()
        sendHideEvent(eventSender)
    })

    fun isSwitchedOn() =
            singleUseCaseFromCallable(schedulerProvider) {
                lockScreenRepository.isSwitchedOn()
            }

    fun startIncomingCall(body: () -> Unit) =
            useCompletableUseCaseFromRunnable(schedulerProvider, {
                lockScreenRepository.incomeCallInProcess(true)
            }, body)

    fun startOutgoingCall(body: () -> Unit) =
            useCompletableUseCaseFromRunnable(schedulerProvider, {
                lockScreenRepository.outgoingCallInProcess(true)
            }, body)

    fun stopIncomingCall(body: () -> Unit) =
            predicatedUseSingleUseCase(schedulerProvider, {
                lockScreenRepository.let {
                    it.incomeCallInProcess().let { result ->
                        if (result)
                            it.incomeCallInProcess(false)
                        result
                    }
                }
            }, body)

    fun stopOutgoingCall(body: () -> Unit) =
            predicatedUseSingleUseCase(schedulerProvider, {
                lockScreenRepository.let {
                    it.outgoingCallInProcess().let { result ->
                        if (result)
                            it.outgoingCallInProcess(false)
                        result
                    }
                }
            }, body)

    private fun saveStartType(parameter: LockScreenStartType): Completable =
            buildEmptyCompletableUseCase(schedulerProvider) {
                lockScreenRepository.saveStartType(parameter)
            }

    fun getLastStartType(): Single<Optional<LockScreenStartType>> =
            buildEmptySingleUseCase(schedulerProvider) {
                lockScreenRepository.getLastStartType()
            }

    fun updateLastStartType(): Completable =
            buildEmptyCompletableUseCase(schedulerProvider) {
                getLastStartType().flatMapCompletable { lastStartTypeOpt ->
                    lockScreenRepository.saveStartType(EXPLICIT).doIfOrComplete {
                        lastStartTypeOpt.data == ON_NOTIFICATION_CLICK
                    }
                }
            }


    private fun sendHideEvent(eventSender: IEventSender) {
        eventSender.send(LockScreenHideEvent)
    }

    object LockScreenHideEvent : IEvent {

        override val eventName: String = javaClass.name

    }
}