package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.interactor.buildCompletableUseCaseFromRunnable
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.LockScreenStartType
import ru.nekit.android.qls.domain.model.LockScreenStartType.*
import ru.nekit.android.qls.domain.providers.UseCaseSupport
import ru.nekit.android.utils.doIfOrComplete
import ru.nekit.android.utils.doIfOrNever

object LockScreenUseCases : UseCaseSupport() {

    private val lockScreenRepository
        get() = repositoryHolder.getLockScreenRepository()

    private val questSetupWizardSettingRepository
        get() = repositoryHolder.getQuestSetupWizardSettingRepository()

    fun start(startType: LockScreenStartType, body: () -> Unit) =
            useCompletableUseCase({
                saveStartType(startType)
            }) {
                AccessUseCases.checkAccess()
                body()
            }

    fun tryToShowLockScreen(startType: LockScreenStartType, body: () -> Unit) =
            useCompletableUseCase({
                QuestStatisticsAndHistoryUseCases.lastHistory().build().map { historyOpt ->
                    if (lockScreenRepository.switchOn && startType != SETUP_WIZARD_IN_PROCESS) {
                        if (startType == ON_NOTIFICATION_CLICK || startType == PLAY_NOW) true
                        else
                            if (questSetupWizardSettingRepository.skipAfterRightAnswer)
                                if (historyOpt.isNotEmpty() &&
                                        historyOpt.nonNullData.answerType == AnswerType.RIGHT)
                                    timeProvider.getCurrentTime() - historyOpt.nonNullData.timeStamp >
                                            questSetupWizardSettingRepository.timeoutToSkipAfterRightAnswer
                                else true
                            else true
                    } else
                        false
                }.flatMapCompletable {
                    saveStartType(startType).doIfOrNever { it }
                }.doOnSubscribe {
                    QuestTrainingProgramUseCases.createRemoteQuestTrainingProgram().use()
                }
            }, body)

    fun switchOnIfNeed(startType: LockScreenStartType, body: (Boolean) -> Unit) = useSingleUseCaseFromCallable({
        val isOn = startType == ON_NOTIFICATION_CLICK || startType == PLAY_NOW
        if (isOn)
            lockScreenRepository.switchOn = isOn
        lockScreenRepository.switchOn
    }, body)

    fun switchOff(body: () -> Unit) = useCompletableUseCaseFromRunnable({
        lockScreenRepository.switchOn = false
        sendHideEvent()
    }, body)

    fun hide(body: () -> Unit) {
        useCompletableUseCaseFromRunnable {
            SessionTimer.stop()
            sendHideEvent()
            body()
        }
    }

    fun isSwitchedOn(body: (Boolean) -> Unit) =
            useSingleUseCaseFromCallable({
                lockScreenRepository.switchOn
            }, body)

    fun startIncomingCall(body: () -> Unit) =
            useCompletableUseCaseFromRunnable({
                lockScreenRepository.incomeCallInProcess = screenProvider.screenIsOn()
            }, body)

    fun startOutgoingCall(body: () -> Unit) = useCompletableUseCase({
        SetupWizardUseCases.callPhonePermissionIsGranted().flatMapCompletable {
            if (it)
                buildCompletableUseCaseFromRunnable {
                    lockScreenRepository.outgoingCallInProcess = true
                }
            else
                Completable.never()
        }
    }, body)

    fun stopIncomingCall(body: () -> Unit) =
            predicatedUseSingleUseCase({
                lockScreenRepository.let {
                    it.incomeCallInProcess.let { result ->
                        if (result)
                            it.incomeCallInProcess = false
                        result
                    }
                }
            }, body)

    fun firstStartTimestamp(): Single<Optional<Long>> = buildSingleUseCase {
        lockScreenRepository.firstStartTypeTimestamp(ON_NOTIFICATION_CLICK, PLAY_NOW)
    }

    fun stopOutgoingCall(body: () -> Unit) =
            predicatedUseSingleUseCase({
                lockScreenRepository.let {
                    it.outgoingCallInProcess.let { result ->
                        if (result)
                            it.outgoingCallInProcess = false
                        result
                    }
                }
            }, body)

    private fun saveStartType(parameter: LockScreenStartType): Completable =
            buildCompletableUseCase {
                lockScreenRepository.saveStartType(parameter, timeProvider.getCurrentTime())
            }

    fun getLastStartType(): Single<Optional<LockScreenStartType>> =
            buildSingleUseCase {
                lockScreenRepository.lastStartType
            }

    fun updateLastStartType(): Completable =
            buildCompletableUseCase {
                getLastStartType().flatMapCompletable { lastStartTypeOpt ->
                    lockScreenRepository.replaceLastStartTypeWith(PLAY_NOW).doIfOrComplete {
                        lastStartTypeOpt.data == ON_NOTIFICATION_CLICK
                    }
                }
            }

    private fun sendHideEvent() {
        eventSender.send(LockScreenHideEvent)
    }

    object LockScreenHideEvent : IEvent {

        override val eventName: String = javaClass.name

    }
}