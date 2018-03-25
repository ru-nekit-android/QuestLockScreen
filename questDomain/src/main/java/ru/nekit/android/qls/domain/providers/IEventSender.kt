package ru.nekit.android.qls.domain.providers

import ru.nekit.android.domain.event.IEvent

interface IEventSender {

    fun send(event: IEvent)

}
