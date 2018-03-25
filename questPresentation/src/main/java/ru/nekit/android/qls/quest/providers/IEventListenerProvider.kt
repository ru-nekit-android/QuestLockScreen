package ru.nekit.android.qls.quest.providers

import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.qls.eventBus.IEventListener
import ru.nekit.android.qls.utils.IAutoDispose

interface IEventListenerProvider : IAutoDispose {

    val eventListener: IEventListener

    fun <T : IEvent> listenForEvent(clazz: Class<T>, body: (T) -> Unit) =
            eventListener.listen(this, clazz, body)

    override fun dispose() {
        eventListener.stopListen(this)
        super.dispose()
    }

}