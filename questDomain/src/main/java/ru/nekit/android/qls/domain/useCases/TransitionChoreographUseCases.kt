package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.interactor.*
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.QuestHistory
import ru.nekit.android.qls.domain.model.Transition
import ru.nekit.android.qls.domain.model.Transition.*
import ru.nekit.android.qls.domain.model.Transition.Type.CURRENT_TRANSITION
import ru.nekit.android.qls.domain.model.Transition.Type.PREVIOUS_TRANSITION
import ru.nekit.android.qls.domain.providers.DependenciesProvider
import ru.nekit.android.qls.domain.repository.ITransitionChoreographRepository
import ru.nekit.android.qls.domain.useCases.TransitionChoreographEvent.TRANSITION_CHANGED

object TransitionChoreographUseCases : DependenciesProvider() {

    private val transitionRepository: ITransitionChoreographRepository
        get() = repository.getTransitionChoreographRepository()

    private fun getCurrentTransition(): Transition? =
            transitionRepository.getTransition(CURRENT_TRANSITION)

    private fun getPreviousTransition(): Transition? =
            transitionRepository.getTransition(PREVIOUS_TRANSITION)

    private fun generateNextTransition(lastHistory: QuestHistory?): Transition? =
            transitionRepository.let {
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

    private fun notifyAboutTransitionChange() {
        TRANSITION_CHANGED.currentTransition = getCurrentTransition()
        TRANSITION_CHANGED.previousTransition = getPreviousTransition()
        eventSender.send(TRANSITION_CHANGED)
    }

    private fun setCurrentTransition(transition: Transition?) {
        transitionRepository.setTransition(PREVIOUS_TRANSITION, getCurrentTransition())
        transitionRepository.setTransition(CURRENT_TRANSITION, transition)
    }

    fun getPreviousTransition(body: (Transition?) -> Unit) = useSingleUseCaseFromCallable(schedulerProvider, {
        Optional(getPreviousTransition())
    }) {
        body(it.data)
    }

    fun getQuestSeriesCounterValue(body: (Int) -> Unit) = useSingleUseCaseFromCallable(schedulerProvider, {
        transitionRepository.questSeriesCounter.value
    }, body)

    fun goOnCurrentTransition() = useCompletableUseCaseFromRunnable(schedulerProvider, {
        transitionRepository.let {
            it.advertWasShown(false)
            notifyAboutTransitionChange()
        }
    })

    internal fun generateNextTransition() = buildCompletableUseCase(null, {
        QuestStatisticsAndHistoryUseCases.lastHistory().build().flatMapCompletable { historyOpt ->
            Completable.fromRunnable {
                transitionRepository.apply {
                    questSeriesCounter.countDown()
                    if (advertIsPresented)
                        advertCounter.countDown()
                    setCurrentTransition(generateNextTransition(historyOpt.data))
                }
            }
        }
    })

    fun goOnNextTransition() = useCompletableUseCase(schedulerProvider, {
        QuestStatisticsAndHistoryUseCases.lastHistory().build().flatMapCompletable { historyOpt ->
            Completable.fromRunnable {
                transitionRepository.apply {
                    setCurrentTransition(generateNextTransition(historyOpt.data))
                    notifyAboutTransitionChange()
                }
            }
        }
    })

    fun getCurrentTransition(body: (Transition?) -> Unit) = currentTransition().use { body(it.data) }

    fun currentTransition() = singleUseCaseFromCallable(schedulerProvider, {
        Optional(getCurrentTransition())
    })

    fun getNextTransition(body: (Transition?) -> Unit) = useSingleUseCase(schedulerProvider, {
        QuestStatisticsAndHistoryUseCases.lastHistory().build().flatMap { historyOpt ->
            Single.fromCallable {
                Optional(generateNextTransition(historyOpt.data))
            }
        }
    }) { body(it.data) }

    fun goStartTransition(body: () -> Unit) = useCompletableUseCase(schedulerProvider, {
        SettingsUseCases.questSeriesLength().build().flatMapCompletable { seriesLength ->
            QuestStatisticsAndHistoryUseCases.lastHistory().build().flatMapCompletable { historyOpt ->
                Completable.fromRunnable {
                    transitionRepository.let {
                        it.questSeriesCounter.startValue = seriesLength
                        it.advertCounter.startValue = it.advertStartValue
                        it.advertCounter.reset()
                        var startTransition: Transition?
                        do {
                            startTransition = generateNextTransition(historyOpt.data)
                            if (startTransition == null)
                                reset()
                        } while (startTransition == null)
                        it.setTransition(CURRENT_TRANSITION, null)
                        setCurrentTransition(startTransition)
                        notifyAboutTransitionChange()
                    }
                }
            }
        }
    }, body)

    fun destroyTransition() = useCompletableUseCase(schedulerProvider, {
        Completable.fromRunnable {
            transitionRepository.let {
                val currentTransition = getCurrentTransition()
                val lastTransition = Transition.values()[Transition.values().size - 1]
                if (currentTransition == null || currentTransition == lastTransition) {
                    reset()
                }
                it.introductionWasShown(false)
                it.advertWasShown(false)
            }
        }
    })


    private fun reset() {
        transitionRepository.apply {
            introductionWasShown(false)
            advertWasShown(false)
            if (questSeriesCounter.zeroWasReached())
                questSeriesCounter.reset()
            setTransition(PREVIOUS_TRANSITION, null)
            setTransition(CURRENT_TRANSITION, null)
        }
    }

}

enum class TransitionChoreographEvent : IEvent {

    TRANSITION_CHANGED;

    var currentTransition: Transition? = null
        internal set
    var previousTransition: Transition? = null
        internal set

    override val eventName: String = "${javaClass.name}::$name"

}