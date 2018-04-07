package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.event.IEventSender
import ru.nekit.android.domain.interactor.*
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.LockScreenStartType
import ru.nekit.android.qls.domain.model.LockScreenStartType.*
import ru.nekit.android.qls.domain.providers.DependenciesProvider
import ru.nekit.android.utils.doIfOrComplete
import ru.nekit.android.utils.doIfOrNever

object LockScreenUseCases : DependenciesProvider() {

    private val lockScreenRepository
        get() = repository.getLockScreenRepository()

    fun start(startType: LockScreenStartType, body: () -> Unit) =
            useCompletableUseCase(schedulerProvider, {
                saveStartType(startType).concatWith(
                        switchOn().doIfOrComplete { startType != SETUP_WIZARD }
                )
            }, body)

    fun showLockScreen(startType: LockScreenStartType, body: () -> Unit) =
            useCompletableUseCase(schedulerProvider, {
                QuestStatisticsAndHistoryUseCases.lastHistory().build().map { historyOpt ->
                    if (startType == ON_NOTIFICATION_CLICK || startType == EXPLICIT) true
                    else
                        if (repository.getQuestSetupWizardSettingRepository().skipAfterRightAnswer)
                            if (historyOpt.isNotEmpty() &&
                                    historyOpt.nonNullData.answerType == AnswerType.RIGHT)
                                timeProvider.getCurrentTime() - historyOpt.nonNullData.timeStamp >
                                        repository.getQuestSetupWizardSettingRepository().timeForSkipAfterRightAnswer
                            else true
                        else true
                }.flatMapCompletable {
                    saveStartType(startType).doIfOrNever { it }
                }.doIfOrNever { startType != SETUP_WIZARD }
            }, body)

    private fun switchOn() = buildCompletableUseCaseFromRunnable {
        lockScreenRepository.switchOn(true)
    }

    fun switchOff(body: () -> Unit) = useCompletableUseCaseFromRunnable(schedulerProvider, {
        lockScreenRepository.switchOn(false)
        sendHideEvent(eventSender)
    }, body)

    fun hide() = useCompletableUseCaseFromRunnable(schedulerProvider, {
        SessionTimer.stop()
        sendHideEvent(eventSender)
    })

    fun isSwitchedOn(body: (Boolean) -> Unit) =
            useSingleUseCaseFromCallable(schedulerProvider, {
                lockScreenRepository.isSwitchedOn()
            }, body)

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
            buildCompletableUseCase(schedulerProvider) {
                lockScreenRepository.saveStartType(parameter)
            }

    fun getLastStartType(): Single<Optional<LockScreenStartType>> =
            buildSingleUseCase(schedulerProvider) {
                lockScreenRepository.getLastStartType()
            }

    fun updateLastStartType(): Completable =
            buildCompletableUseCase(schedulerProvider) {
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