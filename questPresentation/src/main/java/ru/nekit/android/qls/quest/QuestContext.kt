package ru.nekit.android.qls.quest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.annotation.StyleRes
import android.view.ContextThemeWrapper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.qls.QuestLockScreenApplication
import ru.nekit.android.qls.domain.model.*
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.providers.IEventSender
import ru.nekit.android.qls.domain.providers.ITimeProvider
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.qls.domain.useCases.*
import ru.nekit.android.qls.eventBus.IEventListener
import ru.nekit.android.qls.quest.QuestContextEvent.*
import ru.nekit.android.qls.quest.resources.QuestResourceRepository
import ru.nekit.android.qls.shared.model.Pupil
import ru.nekit.android.qls.utils.Delay
import ru.nekit.android.qls.utils.IAutoDispose
import ru.nekit.android.qls.utils.Vibrate
import java.util.concurrent.TimeUnit

class QuestContext constructor(
        val application: QuestLockScreenApplication,
        @StyleRes themeResourceId: Int) :
        ContextThemeWrapper(application, themeResourceId), IAutoDispose {

    override var disposable: CompositeDisposable = CompositeDisposable()
    val questResourceRepository: QuestResourceRepository = QuestResourceRepository(this)
    val eventListener: IEventListener = application.getEventListener()
    private val screenProvider = application.getScreenProvider()
    val timeProvider: ITimeProvider = application.getTimeProvider()
    val schedulerProvider: ISchedulerProvider = application.getDefaultSchedulerProvider()
    val repository: IRepositoryHolder = application
    val eventSender: IEventSender = application.getEventSender()
    val questDelayedPlayAnimationDuration: Long = application.getQuestParams().delayedPlayDelay
    val answerCallback = PublishSubject.create<Any>().toSerialized()

    private val screenOnBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            OnScreenOnUseCase(repository,
                    timeProvider,
                    screenProvider,
                    schedulerProvider
            ).use {
                notifyAboutPlayQuest()
            }
        }
    }

    init {
        application.registerReceiver(screenOnBroadcastReceiver, IntentFilter(Intent.ACTION_SCREEN_ON).also {
            it.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        })

        autoDispose {
            answerCallback.throttleFirst(1000, TimeUnit.MILLISECONDS)
                    .flatMapSingle {
                        AnswerCallbackUseCase(repository, timeProvider, schedulerProvider)
                                .build(it)
                    }.map {
                        when (it) {
                            AnswerType.RIGHT -> RIGHT_ANSWER
                            AnswerType.WRONG -> WRONG_ANSWER
                            AnswerType.EMPTY -> EMPTY_ANSWER
                            AnswerType.WRONG_INPUT_FORMAT -> WRONG_INPUT_FORMAT_ANSWER
                        }
                    }.doOnNext {
                        when (it) {
                            WRONG_ANSWER ->
                                Vibrate.make(this, Delay.VIBRATION.get(this))
                            EMPTY_ANSWER ->
                                Vibrate.make(this, Delay.VIBRATION.get(this))
                            WRONG_INPUT_FORMAT_ANSWER ->
                                Vibrate.make(this, Delay.VIBRATION.get(this))
                            else -> {
                            }
                        }
                    }.subscribe {
                        eventSender.send(it)
                    }
        }
    }

    fun pauseQuest() = PauseQuestUseCase(repository, schedulerProvider).use {
        eventSender.send(QUEST_PAUSE)
    }

    fun resumeQuest() = ResumeQuestUseCase(repository, timeProvider, schedulerProvider).use {
        eventSender.send(QUEST_RESUME)
    }

    fun attachQuest() = AttachQuestUseCase(repository, schedulerProvider).use {
        eventSender.send(QUEST_ATTACH)
    }

    fun startAndPlayQuestIfAble() = StartAndPossiblePlayQuestUseCase(repository,
            timeProvider,
            screenProvider,
            schedulerProvider).use {
        eventSender.send(QUEST_START)
        if (it)
            notifyAboutPlayQuest()
    }

    fun questSeriesLength(body: (Int) -> Unit) = GetQuestSeriesLength(repository,
            schedulerProvider).use(body)

    fun questSeriesCounterValue(body: (Int) -> Unit) =
            GetQuestSeriesCounterValueUseCase(repository, schedulerProvider).use(body)

    fun <T : Quest> listenQuest(clazz: Class<T>, body: (T) -> Unit): Disposable =
            ListenCurrentQuestUseCase(schedulerProvider).buildAsync().cast(clazz).subscribe { quest -> body(quest) }

    fun questHasState(state: QuestState, body: (Boolean) -> Unit) =
            QuestHasStateUseCase(repository, schedulerProvider).use(state, body)


    fun pupil(body: (Pupil) -> Unit) =
            GetCurrentPupilUseCase(repository).use {
                body(it.nonNullData)
            }

    fun destroy() = DestroyQuestUseCase(repository, schedulerProvider).use {
        dispose()
        application.unregisterReceiver(screenOnBroadcastReceiver)
    }

    fun forceCreateQuestTrainingProgram(body: () -> Unit) =
            ForceCreateQuestTrainingProgramUseCase(repository, schedulerProvider).use {
                body()
            }

    fun createQuestTrainingProgram(body: () -> Unit) =
            CreateQuestTrainingProgramUseCase(repository, schedulerProvider).use {
                body()
            }

    fun playQuest(body: (Boolean) -> Unit) = PlayQuestUseCase(repository, timeProvider,
            screenProvider).use {
        notifyAboutPlayQuest()
        body(it)
    }

    fun stopQuest() =
            StopQuestUseCase(repository, schedulerProvider).use {
                eventSender.send(QUEST_STOP)
            }

    fun currentLevel(body: (QuestTrainingProgramLevel) -> Unit) =
            GetCurrentQuestTrainingProgramLevelUseCase(repository, schedulerProvider).use {
                body(it)
            }

    fun pupilStatistics(body: (PupilStatistics) -> Unit): Disposable =
            GetCurrentPupilStatisticsUseCase(repository, schedulerProvider).buildAsync().subscribe(body)

    fun allPointsLevel(body: (Int) -> Unit) =
            GetBeforeCurrentQuestTrainingProgramLevelAllPointsUseCase(repository, schedulerProvider).use(body)

    fun questHistory(body: (QuestHistory?) -> Unit) =
            GetLastHistoryUseCase(repository, schedulerProvider).use { body(it.data) }

    fun questStatisticsReport(body: (QuestStatisticsReport) -> Unit) =
            GetCurrentQuestStatisticsReportUseCase(repository).use(body)

    fun questPreviousHistoryWithBestSessionTime(questHistory: QuestHistory, body: (QuestHistory?) -> Unit) =
            GetPreviousHistoryWithBestSessionTimeUseCase(repository, schedulerProvider).use(questHistory.questAndQuestionType) {
                body(if (it.isEmpty()) null else it.nonNullData)
            }

    fun listenSessionTime(body: (Long) -> Unit): Disposable = ListenSessionTimeUseCase(schedulerProvider).buildAsync().subscribe(body)

    fun listenUnlockKeyCount(body: (Int) -> Unit): Disposable = ListenRewardUseCase(repository, schedulerProvider)
            .build(Reward.UnlockKey())
            .subscribe(body)

    private fun statistics(period: StatisticPeriodType, body: (Int, Int, Int) -> Unit) =
            GetHistoryByStatisticsPeriodType(repository, timeProvider, schedulerProvider).use(period) { history ->

                body(0, 0, 0)
            }

    private fun rewardStatistics(period: StatisticPeriodType, body: (Int, Map<Reward, Int>) -> Unit) =
            GetHistoryByStatisticsPeriodType(repository, timeProvider, schedulerProvider).use(period) { history ->
                val result: HashMap<Reward, Int> = HashMap()
                var count = 0
                history.forEach { historyItem ->
                    if (historyItem.rewards.isNotEmpty()) {
                        historyItem.rewards.forEach { reward ->
                            count++
                            if (result[reward] == null)
                                result[reward] = 1
                            else
                                result[reward] = result[reward]!! + 1
                        }
                    }
                }
                body(count, result)
            }

    fun weeklyStatistics(body: (Int, Int, Int) -> Unit) = statistics(StatisticPeriodType.WEEKLY, body)

    fun weeklyRewardStatistics(body: (Int, Map<Reward, Int>) -> Unit) =
            rewardStatistics(StatisticPeriodType.WEEKLY, body)

    fun getRemainingAmountForReaching(body: (List<Pair<Reward, Int>>) -> Unit) =
            GetRemainingAmountForReaching(repository, schedulerProvider).use(body)

    private fun notifyAboutPlayQuest() =
            questHasState(QuestState.WAS_STOPPED)
            {
                if (it)
                    eventSender.send(QUEST_REPLAY)
                else
                    eventSender.send(QUEST_PLAY)
            }

}

enum class QuestContextEvent : IEvent {

    LEVEL_UP,
    RIGHT_ANSWER,
    WRONG_ANSWER,
    EMPTY_ANSWER,
    WRONG_INPUT_FORMAT_ANSWER,
    QUEST_ATTACH,
    QUEST_START,
    QUEST_PLAY,
    QUEST_REPLAY,
    QUEST_STOP,
    QUEST_PAUSE,
    QUEST_RESUME;

    override val eventName: String = "${javaClass.name}::$name"

}