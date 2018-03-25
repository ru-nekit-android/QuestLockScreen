package ru.nekit.android.qls.quest.providers

import ru.nekit.android.domain.event.KeyboardHideAction
import ru.nekit.android.qls.QuestLockScreenApplication
import ru.nekit.android.qls.domain.model.*
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.providers.IEventSender
import ru.nekit.android.qls.eventBus.IEventListener
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.shared.model.Pupil

interface IQuestContextProvider : IQuestWindowProvider, IEventSenderProvider {

    var questContext: QuestContext

    val application: QuestLockScreenApplication
        get() = questContext.application

    override val eventListener: IEventListener
        get() = questContext.eventListener

    override val eventSender: IEventSender
        get() = questContext.eventSender

    fun listenQuest(body: (Quest) -> Unit) =
            listenQuest(Quest::class.java, body)

    fun <T : Quest> listenQuest(clazz: Class<T>, body: (T) -> Unit) =
            autoDispose {
                questContext.listenQuest(clazz, body)
            }

    fun listenPupilStatistics(body: (PupilStatistics) -> Unit) =
            autoDispose {
                questContext.pupilStatistics(body)
            }

    fun listenSessionTime(body: (Long) -> Unit) = autoDispose {
        questContext.listenSessionTime(body)
    }

    fun allPointsLevel(body: (Int) -> Unit) =
            questContext.allPointsLevel(body)

    fun pupil(body: (Pupil) -> Unit) = questContext.pupil(body)

    fun currentLevel(body: (QuestTrainingProgramLevel) -> Unit) =
            questContext.currentLevel(body)

    fun questHasState(state: QuestState, body: (Boolean) -> Unit) =
            questContext.questHasState(state, body)

    fun questHistory(body: (QuestHistory?) -> Unit) = questContext.questHistory(body)

    fun questStatisticsReport(body: (QuestStatisticsReport) -> Unit) =
            questContext.questStatisticsReport(body)

    fun questPreviousHistoryWithBestSessionTime(questHistory: QuestHistory, body: (QuestHistory?) -> Unit) =
            questContext.questPreviousHistoryWithBestSessionTime(questHistory, body)

    fun listenUnlockKeyCount(body: (Int) -> Unit) = autoDispose {
        questContext.listenUnlockKeyCount(body)
    }

    fun hideKeyboard(body: () -> Unit) = sendEvent(KeyboardHideAction(body))

}