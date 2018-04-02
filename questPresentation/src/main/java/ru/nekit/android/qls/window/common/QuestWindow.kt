package ru.nekit.android.qls.window.common

import android.support.annotation.StyleRes
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.event.IEventSender
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.window.common.QuestWindowEvent.*
import ru.nekit.android.window.Window
import ru.nekit.android.window.WindowContentViewHolder
import ru.nekit.android.window.WindowListener

open class QuestWindow(questContext: QuestContext,
                       name: String,
                       content: WindowContentViewHolder?,
                       @StyleRes styleResId: Int) : Window(questContext, name, object : WindowListener {

    override fun onWindowOpen(window: Window) {
        sendEvent(questContext.eventSender, OPEN, window.name)
    }

    override fun onWindowOpened(window: Window) {
        sendEvent(questContext.eventSender, OPENED, window.name)
    }

    override fun onWindowClose(window: Window) {
        sendEvent(questContext.eventSender, CLOSE, window.name)
    }

    override fun onWindowClosed(window: Window) {
        sendEvent(questContext.eventSender, CLOSED, window.name)
    }
}, content, styleResId) {

    companion object {
        private fun sendEvent(eventSender: IEventSender, event: QuestWindowEvent, windowName: String) {
            event.windowName = windowName
            eventSender.send(event)
        }
    }
}

enum class QuestWindowEvent : IEvent {

    OPEN,
    OPENED,
    CLOSE,
    CLOSED;

    lateinit var windowName: String

    override val eventName: String = "${javaClass.name}::$name"

}