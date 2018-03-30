package ru.nekit.android.qls.quest.providers

import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.event.IEventSender

interface IEventSenderProvider {

    val eventSender: IEventSender

    fun sendEvent(event: IEvent) = eventSender.send(event)

}