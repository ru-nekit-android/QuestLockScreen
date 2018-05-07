package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.FlowableProcessor
import io.reactivex.schedulers.Timed
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.*
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.creator.*
import ru.nekit.android.qls.domain.model.*
import ru.nekit.android.qls.domain.model.QuestState.*
import ru.nekit.android.qls.domain.model.quest.CurrentTimeQuest
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.model.quest.TimeQuest
import ru.nekit.android.qls.domain.providers.IScreenProvider
import ru.nekit.android.qls.domain.providers.ITimeProvider
import ru.nekit.android.qls.domain.providers.UseCaseSupport
import ru.nekit.android.qls.domain.repository.IQuestSetupWizardSettingRepository
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.qls.shared.model.QuestType
import ru.nekit.android.utils.asSingleIf
import ru.nekit.android.utils.doIfOrComplete
import ru.nekit.android.utils.doIfOrNever
import ru.nekit.android.utils.toSingle
import java.util.concurrent.TimeUnit

private class QuestHasDelayedPlayUseCase(private val repository: IRepositoryHolder,
                                         scheduler: ISchedulerProvider? = null) :
        SingleUseCase<Boolean, QuestAndQuestionType>(scheduler) {
    override fun build(parameter: QuestAndQuestionType): Single<Boolean> =
            GetQuestTrainingProgramRuleByQuestAndQuestionType(repository).build(parameter).flatMap { rule ->
                GetCurrentQuestTrainingProgramLevelUseCase(repository).build().map { level ->
                    if (rule.nonNullData.delayedPlay == -1) {
                        if (level.delayedPlay == -1)
                            false
                        else level.delayedPlay == 1
                    } else rule.nonNullData.delayedPlay == 1
                }
            }
}

class PauseQuestUseCase(private val repository: IRepositoryHolder,
                        scheduler: ISchedulerProvider? = null) :
        ParameterlessCompletableUseCase(scheduler) {
    override fun build(): Completable = QuestHasStateUseCase(repository).build(PLAYED).flatMapCompletable {
        repository.getQuestStateRepository().replace(PLAYED, PAUSED).doOnComplete {
            SessionTimer.stop()
        }.doIfOrNever { it }
    }
}

class ResumeQuestUseCase(private val repository: IRepositoryHolder,
                         private val timeProvider: ITimeProvider,
                         scheduler: ISchedulerProvider? = null) :
        ParameterlessCompletableUseCase(scheduler) {
    override fun build(): Completable = QuestHasStateUseCase(repository).build(PAUSED).flatMapCompletable {
        repository.getQuestStateRepository().replace(PAUSED, PLAYED).concatWith(
                StartSessionTimerUseCase(repository, timeProvider).build()).doIfOrComplete { it }
    }
}

class DestroyQuestUseCase(private val repository: IRepositoryHolder,
                          scheduler: ISchedulerProvider? = null) :
        ParameterlessCompletableUseCase(scheduler) {

    override fun build(): Completable = repository.getQuestStateRepository().clear()
            .concatWith(Completable.fromRunnable {
                SessionTimer.stop()
                QuestHolder.quest = null
            })
}

class AttachQuestUseCase(private val repository: IRepositoryHolder,
                         scheduler: ISchedulerProvider? = null) :
        ParameterlessCompletableUseCase(scheduler) {
    override fun build(): Completable = repository.getQuestStateRepository().add(ATTACHED)
}

class StartAndPossiblePlayQuestUseCase(private val repository: IRepositoryHolder,
                                       private val timeProvider: ITimeProvider,
                                       private val screenProvider: IScreenProvider,
                                       scheduler: ISchedulerProvider? = null) :
        ParameterlessSingleUseCase<Boolean>(scheduler) {

    private val questStateRepository = repository.getQuestStateRepository()

    override fun build(): Single<Boolean> =
            questStateRepository.has(DELAYED_PLAY).flatMap {
                questStateRepository.add(STARTED).andThen(
                        if (!it)
                            PlayQuestUseCase(repository, timeProvider, screenProvider).build()
                        else
                            false.toSingle()
                )
            }
}

object QuestUseCases : UseCaseSupport() {

    private val questStateRepository
        get() = repositoryHolder.getQuestStateRepository()

    private val questRepository
        get() = repositoryHolder.getQuestRepository()

    fun onScreenOnUseCase(body: (Boolean) -> Unit) = useSingleUseCase(schedulerProvider, {
        Single.zip(TransitionChoreographUseCases.currentTransition().build().map { it.nonNullData },
                QuestHasStateUseCase(repositoryHolder).build(DELAYED_PLAY),
                QuestHasStateUseCase(repositoryHolder).build(WAS_STOPPED),
                Function3<Transition, Boolean, Boolean, Boolean> { transition, delayed, wasStopped ->
                    transition == Transition.QUEST && (!delayed || wasStopped)
                }
        ).flatMap {
            PlayQuestUseCase(repositoryHolder, timeProvider, screenProvider).build()
                    .doIfOrNever { it }
        }
    }, body)

    fun generateQuest() = buildSingleUseCase {
        pupilFlatMap { pupil ->
            questStateRepository.clear()
                    .andThen(questRepository.hasSavedQuest(pupil))
                    .flatMap { hasSavedQuest ->
                        if (hasSavedQuest) {
                            questRepository.restoreQuest(pupil)
                                    .flatMap { restoredQuestOpt ->
                                        var restoredQuest: Quest? = restoredQuestOpt.data
                                        if (restoredQuest != null) {
                                            GetQuestTrainingProgramRuleByQuestAndQuestionType(repositoryHolder)
                                                    .build(QuestAndQuestionType(restoredQuest.questType,
                                                            restoredQuest.questionType)).map { rule ->
                                                        if (rule.isEmpty())
                                                            restoredQuest = null
                                                        Optional(restoredQuest)
                                                    }.flatMap { result ->
                                                        questStateRepository.add(WAS_RESTORED).asSingleIf(result) { result.isNotEmpty() }
                                                    }
                                        } else Single.just(Optional(null))
                                    }
                        } else Single.just(Optional(null))
                    }.flatMap { it ->
                        if (it.isEmpty()) {
                            QuestTrainingProgramUseCases.getAppropriateQuestAndQuestionType(
                                    AppropriateQuestParameter(AppropriateType.BY_RANDOM_CHANCE,
                                            1,
                                            2,
                                            true))
                                    .build()
                                    .flatMap { questAndQuestionType ->
                                        GetQuestTrainingProgramRuleByQuestAndQuestionType(repositoryHolder)
                                                .build(questAndQuestionType)
                                                .map { it.nonNullData }
                                                .map { rule ->
                                                    val questType = questAndQuestionType.questType
                                                    val questionType = questAndQuestionType.questionType
                                                    val questResourceRepository = repositoryHolder.getQuestResourceRepository()
                                                    val quest: Quest = when (questType) {

                                                        QuestType.CHOICE ->

                                                            ChoiceQuestCreator(
                                                                    rule as ChoiceQuestTrainingProgramRule,
                                                                    questResourceRepository
                                                            ).create(questionType)

                                                        QuestType.MISMATCH ->

                                                            MismatchQuestCreator(
                                                                    rule as ChoiceQuestTrainingProgramRule,
                                                                    questResourceRepository
                                                            ).create(questionType)

                                                        QuestType.COLORS ->

                                                            ColoredVisualRepresentationQuestCreator(
                                                                    rule as MemberCountQuestTrainingRule,
                                                                    questResourceRepository
                                                            ).create(questionType)

                                                        QuestType.COINS ->

                                                            CoinsQuestCreator(
                                                                    rule as MemberCountQuestTrainingRule
                                                            ).create(questionType)

                                                        QuestType.DIRECTION ->

                                                            DirectionQuestCreator().create(questionType)

                                                        QuestType.TRAFFIC_LIGHT ->

                                                            TrafficLightQuestCreator().create(questionType)

                                                        QuestType.CURRENT_SEASON ->

                                                            CurrentSeasonQuestCreator(
                                                                    questResourceRepository
                                                            ).create(questionType)

                                                        QuestType.FRUIT_ARITHMETIC ->

                                                            FruitArithmeticQuestCreator(
                                                                    rule as FruitArithmeticQuestTrainingProgramRule,
                                                                    questResourceRepository
                                                            ).create(questionType)

                                                        QuestType.TIME ->

                                                            TimeQuestCreator(
                                                                    rule as TimeQuestTrainingProgramRule
                                                            ).create(questionType).let {
                                                                TimeQuest(it)
                                                            }

                                                        QuestType.CURRENT_TIME ->

                                                            CurrentTimeQuestCreator(
                                                                    rule as TimeQuestTrainingProgramRule
                                                            ).create(questionType).let {
                                                                CurrentTimeQuest(it)
                                                            }

                                                        QuestType.SIMPLE_EXAMPLE -> TODO()
                                                        QuestType.TEXT_CAMOUFLAGE -> TODO()
                                                        QuestType.METRICS -> TODO()
                                                        QuestType.PERIMETER -> TODO()
                                                    }
                                                    quest.questType = questType
                                                    quest.questionType = questionType
                                                    quest
                                                }.flatMap { quest ->
                                                    questRepository.save(pupil, quest).toSingleDefault(quest)
                                                }
                                    }
                        } else Single.just(it.data)
                    }.flatMap { resultQuest ->
                        QuestHasDelayedPlayUseCase(repositoryHolder).build(
                                resultQuest.questType + resultQuest.questionType
                        ).flatMap { delayed ->
                            questStateRepository.add(DELAYED_PLAY).doIfOrComplete { delayed }.andThen(
                                    Single.fromCallable {
                                        resultQuest.also {
                                            QuestHolder.quest = it
                                        }
                                    }
                            )
                        }
                    }
        }
    }

}

class PlayQuestUseCase(private val repository: IRepositoryHolder,
                       private val timeProvider: ITimeProvider,
                       private val screenProvider: IScreenProvider,
                       scheduler: ISchedulerProvider? = null) :
        ParameterlessSingleUseCase<Boolean>(scheduler) {

    private val questStateRepository = repository.getQuestStateRepository()

    override fun build(): Single<Boolean> =
            Single.zip(questStateRepository.has(PLAYED),
                    questStateRepository.has(DELAYED_PLAY),
                    questStateRepository.has(WAS_STOPPED),
                    Function3<Boolean, Boolean, Boolean, List<Boolean>> { questIsPlayed,
                                                                          questIsDelayedPlay,
                                                                          questIsWasStopped ->
                        val screenIsOn = screenProvider.screenIsOn()
                        listOf(screenIsOn, questIsPlayed, questIsDelayedPlay, questIsWasStopped)
                    }).flatMap { questStateRepository.replace(STARTED, PLAYED).asSingleIf(it) { it[0] } }
                    .doOnSuccess {
                        val screenIsOn = it[0]
                        val questIsPlayed = it[1]
                        val delayedPlay = it[2]
                        val wasStopped = it[3]
                        if (screenIsOn) {
                            if (delayedPlay && !wasStopped) {
                                StartSessionTimerWithDelayUseCase(repository,
                                        timeProvider,
                                        null).use()
                            }
                            if (!questIsPlayed || wasStopped) {
                                StartSessionTimerUseCase(repository, timeProvider).use()
                            }
                        }
                    }.map { it[0] && !it[1] }
}

private class StartSessionTimerWithDelayUseCase(
        private val repository: IRepositoryHolder,
        private val timeProvider: ITimeProvider,
        scheduler: ISchedulerProvider?) :
        ParameterlessCompletableUseCase(scheduler) {

    override fun build(): Completable = Completable.timer(
            repository.getQuestSetupWizardSettingRepository().delayedPlayDelay, TimeUnit.MILLISECONDS).andThen {
        SessionTimer.start(repository.getQuestSetupWizardSettingRepository(), timeProvider)
    }

}

private class StartSessionTimerUseCase(private val repository: IRepositoryHolder,
                                       private val timeProvider: ITimeProvider,
                                       scheduler: ISchedulerProvider? = null) :
        ParameterlessCompletableUseCase(scheduler) {

    override fun build(): Completable = Completable.fromRunnable {
        SessionTimer.start(repository.getQuestSetupWizardSettingRepository(), timeProvider)
    }
}

class StopQuestUseCase(private val repository: IRepositoryHolder,
                       scheduler: ISchedulerProvider? = null) :
        ParameterlessCompletableUseCase(scheduler) {

    private val questStateRepository = repository.getQuestStateRepository()

    override fun build(): Completable = QuestHasStateUseCase(repository)
            .build(PLAYED)
            .flatMapCompletable { result ->
                questStateRepository.add(WAS_STOPPED).doOnComplete {
                    SessionTimer.stop()
                }.doIfOrNever { result }
            }
}

class ListenSessionTimeUseCase(private val repository: IRepositoryHolder,
                               scheduler: ISchedulerProvider? = null) :
        ParameterlessFlowableUseCase<Pair<Long, Long>>(scheduler) {

    override fun build(): Flowable<Pair<Long, Long>> = SessionTimer.publisher.map {
        it to repository.getQuestSetupWizardSettingRepository().maxGameSessionTime
    }

}

class ListenCurrentQuestUseCase(scheduler: ISchedulerProvider? = null) :
        ParameterlessFlowableUseCase<Quest>(scheduler) {

    override fun build(): Flowable<Quest> = QuestHolder.publisher.filter { it -> it.isNotEmpty() }.map { it.nonNullData }

}

class GetCurrentQuestUseCase(scheduler: ISchedulerProvider? = null) :
        ParameterlessSingleUseCase<Quest>(scheduler) {

    override fun build(): Single<Quest> =
            Single.just(Optional(QuestHolder.quest))
                    .flatMap {
                        if (it.isEmpty()) Single.never() else Single.just(it.nonNullData)
                    }

}

class QuestHasStateUseCase(private val repository: IRepositoryHolder,
                           scheduler: ISchedulerProvider? = null) :
        SingleUseCase<Boolean, QuestState>(scheduler) {

    override fun build(parameter: QuestState): Single<Boolean> = repository.getQuestStateRepository().has(parameter)

}

internal object QuestHolder {

    var quest: Quest? = null
        set(value) {
            field = value
            publisher.onNext(Optional(value))
        }

    val publisher: FlowableProcessor<Optional<Quest>> = BehaviorProcessor.create<Optional<Quest>>().toSerialized()

}

object SessionLimiter {

    internal fun timeLimiter(repository: IQuestSetupWizardSettingRepository, time: Long) =
            Math.min(repository.maxGameSessionTime, time)

}

internal object SessionTimer {

    private const val TIME_RESOLUTION = 1000L

    fun start(repository: IQuestSetupWizardSettingRepository, timeProvider: ITimeProvider, scheduler: ISchedulerProvider? = null) {
        stop()
        disposable = timer.compose(applySchedulersFlowable(scheduler)).map { it.value() * TIME_RESOLUTION }.subscribe {
            publisher.onNext(SessionLimiter.timeLimiter(repository, it))
        }
        startSessionTime = timeProvider.getCurrentTime()
    }

    private var startSessionTime: Long = 0

    fun getTime(repository: IQuestSetupWizardSettingRepository, currentTime: Long) = SessionLimiter.timeLimiter(repository, currentTime - startSessionTime)

    fun stop() {
        disposable?.dispose()
        publisher.onNext(0)
    }

    private val timer: Flowable<Timed<Long>> = Flowable.interval(TIME_RESOLUTION, TimeUnit.MILLISECONDS).timeInterval().onBackpressureDrop()
    private var disposable: Disposable? = null

    val publisher: FlowableProcessor<Long> = BehaviorProcessor.create<Long>().toSerialized()

}