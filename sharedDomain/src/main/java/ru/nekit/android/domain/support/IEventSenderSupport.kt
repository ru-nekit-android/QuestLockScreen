package ru.nekit.android.domain.support

import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.event.IEventSender

interface IEventSenderSupport {

    val eventSender: IEventSender

    fun sendEvent(event: IEvent) = eventSender.send(event)

}