package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.ParameterlessCompletableUseCase
import ru.nekit.android.domain.interactor.ParameterlessSingleUseCase
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.QuestHistory
import ru.nekit.android.qls.domain.model.Transition
import ru.nekit.android.qls.domain.model.Transition.*
import ru.nekit.android.qls.domain.model.Transition.Type.CURRENT_TRANSITION
import ru.nekit.android.qls.domain.model.Transition.Type.PREVIOUS_TRANSITION
import ru.nekit.android.qls.domain.providers.IEventSender
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.qls.domain.repository.ITransitionChoreographRepository
import ru.nekit.android.qls.domain.useCases.TransitionChoreographEvent.TRANSITION_CHANGED

class GoStartTransitionUseCase(private val repository: IRepositoryHolder,
                               private val eventSender: IEventSender,
                               scheduler: ISchedulerProvider? = null) : ParameterlessCompletableUseCase(scheduler) {
    override fun build(): Completable =
            GetQuestSeriesLength(repository).build().flatMapCompletable { seriesLength ->
                GetLastHistoryUseCase(repository).build().flatMapCompletable { historyOpt ->
                    Completable.fromRunnable {
                        repository.getTransitionChoreographRepository().let {
                            it.questSeriesCounter.startValue = seriesLength
                            it.advertCounter.startValue = it.advertStartValue
                            it.advertCounter.reset()
                            var startTransition: Transition?
                            do {
                                startTransition = generateNextTransition(historyOpt.data, repository)
                                if (startTransition == null)
                                    reset(repository)
                            } while (startTransition == null)
                            it.setTransition(CURRENT_TRANSITION, null)
                            setCurrentTransition(it, startTransition)
                            notifyAboutTransitionChange(it, eventSender)
                        }
                    }
                }
            }
}

class DestroyTransitionUseCase(private val repository: IRepositoryHolder,
                               scheduler: ISchedulerProvider? = null) : ParameterlessCompletableUseCase(scheduler) {
    override fun build(): Completable =
            Completable.fromRunnable {
                repository.getTransitionChoreographRepository().let {
                    val currentTransition = getCurrentTransition(it)
                    val lastTransition = Transition.values()[Transition.values().size - 1]
                    if (currentTransition == null || currentTransition == lastTransition) {
                        reset(repository)
                    }
                    it.introductionWasShown(false)
                    it.advertWasShown(false)
                }
            }
}

class CommitNextTransitionUseCase(private val repository: IRepositoryHolder,
                                  private val eventSender: IEventSender,
                                  scheduler: ISchedulerProvider? = null) : ParameterlessCompletableUseCase(scheduler) {
    override fun build(): Completable = GetLastHistoryUseCase(repository).build().flatMapCompletable { historyOpt ->
        Completable.fromRunnable {
            repository.getTransitionChoreographRepository().apply {
                setCurrentTransition(this,
                        generateNextTransition(historyOpt.data, repository))
                notifyAboutTransitionChange(this, eventSender)
            }
        }
    }
}

class GenerateNextTransitionUseCase(private val repository: IRepositoryHolder,
                                    scheduler: ISchedulerProvider? = null) : ParameterlessCompletableUseCase(scheduler) {
    override fun build(): Completable = GetLastHistoryUseCase(repository).build().flatMapCompletable { historyOpt ->
        Completable.fromRunnable {
            repository.getTransitionChoreographRepository().apply {
                questSeriesCounter.countDown()
                if (advertIsPresented) {
                    advertCounter.countDown()
                }
                setCurrentTransition(this,
                        generateNextTransition(historyOpt.data, repository))
            }
        }
    }
}

class GetQuestSeriesCounterValueUseCase(private val repository: IRepositoryHolder,
                                        scheduler: ISchedulerProvider? = null) : ParameterlessSingleUseCase<Int>(scheduler) {
    override fun build(): Single<Int> = Single.just(repository.getTransitionChoreographRepository().questSeriesCounter.value)
}

class CommitCurrentTransitionUseCase(private val repository: IRepositoryHolder, private val eventSender: IEventSender,
                                     scheduler: ISchedulerProvider? = null) : ParameterlessCompletableUseCase(scheduler) {
    override fun build(): Completable = Completable.fromRunnable {
        repository.getTransitionChoreographRepository().let {
            it.advertWasShown(false)
            notifyAboutTransitionChange(it, eventSender)
        }
    }
}

class GetCurrentTransitionUseCase(private val repository: IRepositoryHolder,
                                  scheduler: ISchedulerProvider? = null) : ParameterlessSingleUseCase<Optional<Transition>>(scheduler) {
    override fun build(): Single<Optional<Transition>> = Single.fromCallable {
        Optional(getCurrentTransition(repository.getTransitionChoreographRepository()))
    }
}

class GetPreviousTransitionUseCase(private val repository: IRepositoryHolder,
                                   scheduler: ISchedulerProvider? = null) : ParameterlessSingleUseCase<Optional<Transition>>(scheduler) {
    override fun build(): Single<Optional<Transition>> = Single.fromCallable {
        Optional(getPreviousTransition(repository.getTransitionChoreographRepository()))
    }
}

private fun generateNextTransition(lastHistory: QuestHistory?, repository: IRepositoryHolder): Transition? =
        repository.getTransitionChoreographRepository().let {
            var transition: Transition? = null
            if (it.introductionIsPresented) {
                if (!it.introductionWasShown()) {
                    it.introductionWasShown(true)
                    transition = INTRODUCTION
                }
            }
            if (transition == null)
                transition = levelUpGoIfCan(lastHistory) ?: advertGoIfCan(it)
            if (transition == null) {
                transition = if (!it.questSeriesCounter.zeroWasReached()) QUEST else null
            }
            transition
        }

private fun advertGoIfCan(repository: ITransitionChoreographRepository) =
        if (repository.advertIsPresented && repository.advertCounter.zeroWasReached()
                && !repository.advertWasShown()) {
            repository.advertCounter.reset()
            repository.advertWasShown(true)
            ADVERT
        } else null

private fun levelUpGoIfCan(lastHistory: QuestHistory?) =
        if (lastHistory != null && lastHistory.levelUp) LEVEL_UP else null

private fun notifyAboutTransitionChange(repository: ITransitionChoreographRepository,
                                        eventSender: IEventSender) {
    TRANSITION_CHANGED.currentTransition = getCurrentTransition(repository)
    TRANSITION_CHANGED.previousTransition = getPreviousTransition(repository)
    eventSender.send(TRANSITION_CHANGED)
}

private fun getCurrentTransition(repository: ITransitionChoreographRepository): Transition? =
        repository.getTransition(CURRENT_TRANSITION)

private fun getPreviousTransition(repository: ITransitionChoreographRepository): Transition? =
        repository.getTransition(PREVIOUS_TRANSITION)

private fun reset(repository: IRepositoryHolder) {
    repository.getTransitionChoreographRepository().apply {
        introductionWasShown(false)
        advertWasShown(false)
        questSeriesCounter.reset()
        setTransition(PREVIOUS_TRANSITION, null)
        setTransition(CURRENT_TRANSITION, null)
    }
}

private fun setCurrentTransition(repository: ITransitionChoreographRepository,
                                 transition: Transition?) {
    repository.setTransition(PREVIOUS_TRANSITION, getCurrentTransition(repository))
    repository.setTransition(CURRENT_TRANSITION, transition)
}

enum class TransitionChoreographEvent : IEvent {

    TRANSITION_CHANGED;

    var currentTransition: Transition? = null
        internal set
    var previousTransition: Transition? = null
        internal set

    override val eventName: String = "${javaClass.name}::$name"

}