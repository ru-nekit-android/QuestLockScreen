package ru.nekit.android.qls.quest.providers

import ru.nekit.android.qls.window.AnswerWindow.Type.RIGHT
import ru.nekit.android.qls.window.AnswerWindow.Type.WRONG
import ru.nekit.android.qls.window.common.QuestWindowEvent
import ru.nekit.android.qls.window.common.QuestWindowEvent.*

interface IQuestWindowProvider : IEventListenerProvider {

    fun onWrongAnswerWindowOpen(body: () -> Unit) =
            listenForEvent(QuestWindowEvent::class.java) {
                if (it == OPEN && it.windowName == WRONG.name)
                    body()
            }

    fun onWrongAnswerWindowOpened(body: () -> Unit) =
            listenForEvent(QuestWindowEvent::class.java) {
                if (it == OPENED && it.windowName == WRONG.name)
                    body()
            }

    fun onWrongAnswerWindowClose(body: () -> Unit) =
            listenForEvent(QuestWindowEvent::class.java) {
                if (it == CLOSE && it.windowName == WRONG.name)
                    body()
            }

    fun onWrongAnswerWindowClosed(body: () -> Unit) =
            listenForEvent(QuestWindowEvent::class.java) {
                if (it == CLOSED && it.windowName == WRONG.name)
                    body()
            }

    fun onRightAnswerWindowOpen(body: () -> Unit) =
            listenForEvent(QuestWindowEvent::class.java) {
                if (it == OPEN && it.windowName == RIGHT.name)
                    body()
            }

    fun onRightAnswerWindowOpened(body: () -> Unit) =
            listenForEvent(QuestWindowEvent::class.java) {
                if (it == OPENED && it.windowName == RIGHT.name)
                    body()
            }

    fun onRightAnswerWindowClose(body: () -> Unit) =
            listenForEvent(QuestWindowEvent::class.java) {
                if (it == CLOSE && it.windowName == RIGHT.name)
                    body()
            }

    fun onRightAnswerWindowClosed(body: () -> Unit) =
            listenForEvent(QuestWindowEvent::class.java) {
                if (it == CLOSED && it.windowName == RIGHT.name)
                    body()
            }

}