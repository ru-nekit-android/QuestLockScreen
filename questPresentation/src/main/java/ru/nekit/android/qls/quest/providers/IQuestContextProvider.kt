package ru.nekit.android.qls.quest.providers

import android.support.annotation.StringRes
import android.view.View
import ru.nekit.android.domain.event.IEventListener
import ru.nekit.android.domain.event.IEventSender
import ru.nekit.android.domain.event.KeyboardAction
import ru.nekit.android.qls.QuestLockScreenApplication
import ru.nekit.android.qls.domain.model.*
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.QuestContextEvent
import ru.nekit.android.qls.shared.model.Pupil
import ru.nekit.android.qls.window.common.QuestWindowEvent
import ru.nekit.android.utils.Delay
import ru.nekit.android.utils.Vibrate
import ru.nekit.android.utils.throttleClicks

interface IQuestContextProvider : IEventListenerProvider, IEventSenderProvider {

    var questContext: QuestContext

    val application: QuestLockScreenApplication
        get() = questContext.application

    override val eventListener: IEventListener
        get() = questContext.eventListener

    override val eventSender: IEventSender
        get() = questContext.eventSender

    fun listenQuest(body: (Quest) -> Unit) =
            listenQuest(Quest::class.java, body)

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

    fun listenPupilStatistics(body: (PupilStatistics) -> Unit) =
            autoDispose {
                questContext.pupilStatistics(body)
            }

    fun listenSessionTime(body: (Long, Long) -> Unit) = autoDispose {
        questContext.listenSessionTime(body)
    }

    fun listenSessionTimeUpdate(body: () -> Unit) = autoDispose {
        questContext.listenSessionTime { _, _ -> body() }
    }

    fun allPointsLevel(body: (Int) -> Unit) =
            questContext.allPointsLevel(body)

    fun pupil(body: (Pupil) -> Unit) = questContext.pupil(body)

    fun currentLevel(body: (QuestTrainingProgramLevel) -> Unit) =
            questContext.currentLevel(body)

    fun questHasState(state: QuestState, body: (Boolean) -> Unit) =
            questContext.questHasState(state, body)

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

    fun getString(@StringRes stringResId: Int): String = questContext.getString(stringResId)

    fun View.responsiveClick(body: () -> Unit) = autoDispose {
        throttleClicks {
            Vibrate.make(questContext, Delay.CLICK.get(questContext))
            body()
        }
    }

    fun statistics(body: (List<Statistics>) -> Unit) = questContext.statistics(body)
}