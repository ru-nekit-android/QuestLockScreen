package ru.nekit.android.qls.quest.providers

import android.support.annotation.StringRes
import ru.nekit.android.domain.event.IEventListener
import ru.nekit.android.domain.event.IEventSender
import ru.nekit.android.domain.event.KeyboardAction
import ru.nekit.android.domain.support.IEventListenerSupport
import ru.nekit.android.domain.support.IEventSenderSupport
import ru.nekit.android.qls.dependences.DependenciesProvider
import ru.nekit.android.qls.domain.model.*
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.QuestContextEvent
import ru.nekit.android.qls.shared.model.Pupil
import ru.nekit.android.qls.window.common.QuestWindowEvent

interface IQuestContextSupport : IEventListenerSupport, IEventSenderSupport {

    var questContext: QuestContext

    val dependenciesProvider: DependenciesProvider
        get() = questContext.dependenciesProvider

    override val eventListener: IEventListener
        get() = questContext.eventListener

    override val eventSender: IEventSender
        get() = questContext.eventSender

    fun quest(body: (Quest) -> Unit) =
            quest(Quest::class.java, body)

    fun <T : Quest> listenQuest(clazz: Class<T>, body: (T) -> Unit) =
            autoDispose {
                questContext.listenQuest(clazz, body)
            }

    fun <T : Quest> quest(clazz: Class<T>, body: (T) -> Unit) =
            autoDispose {
                questContext.quest(clazz, body)
            }

    fun allPointsLevel(body: (Int) -> Unit) =
            questContext.allPointsLevel(body)

    fun pupil(body: (Pupil) -> Unit) = questContext.pupil(body)

    fun currentLevel(body: (QuestTrainingProgramLevel) -> Unit) =
            questContext.currentLevel(body)

    fun questHasState(state: QuestState, body: (Boolean) -> Unit) =
            questContext.questHasState(state, body)

    fun questIsDelayed(body: (Boolean) -> Unit) = questHasState(QuestState.DELAYED_PLAY, body)

    fun questHasStates(vararg state: QuestState, body: (List<Boolean>) -> Unit) =
            questContext.questHasStates(*state) { body(it) }

    fun questHistory(body: (QuestHistory?) -> Unit) = questContext.questHistory(body)

    fun questStatisticsReport(body: (QuestStatisticsReport) -> Unit) =
            questContext.questStatisticsReport(body)

    fun questPreviousHistoryWithBestSessionTime(questHistory: QuestHistory, body: (QuestHistory?) -> Unit) =
            questContext.questPreviousHistoryWithBestSessionTime(questHistory, body)

    fun listenUnlockKeyCount(body: (Int) -> Unit) = autoDispose {
        questContext.listenUnlockKeyCount(body)
    }

    fun getUnlockKeyCount(body: (Int) -> Unit) = questContext.unlockKeyCount(body)

    fun hideKeyboard(body: () -> Unit) = sendEvent(KeyboardAction(KeyboardAction.Action.HIDE, body))

    fun hideKeyboard() = hideKeyboard {}

    fun showKeyboard() = sendEvent(KeyboardAction(KeyboardAction.Action.SHOW, {}))

    fun listenForQuestEvent(body: (event: QuestContextEvent) -> Unit) =
            listenForEvent(QuestContextEvent::class.java, body)

    fun listenForWindowEvent(body: (event: QuestWindowEvent) -> Unit) =
            listenForEvent(QuestWindowEvent::class.java, body)

    fun listenQuest(body: (Quest) -> Unit) =
            listenQuest(Quest::class.java, body)

    fun listenPupilStatistics(body: (PupilStatistics) -> Unit) =
            autoDispose {
                questContext.listenPupilStatistics(body)
            }

    fun listenSessionTime(body: (Long, Long) -> Unit) = autoDispose {
        questContext.listenSessionTime(body)
    }

    fun listenSessionTimeUpdate(body: () -> Unit) = listenSessionTime { _, _ -> body() }

    fun getString(@StringRes stringResId: Int): String = questContext.getString(stringResId)

    fun statistics(body: (List<Statistics>) -> Unit) = questContext.statistics(body)

}