package ru.nekit.android.domain.event

interface IEventSender {

    fun send(event: IEvent)

}
