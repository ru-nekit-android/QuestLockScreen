package ru.nekit.android.domain.support

import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.event.IEventListener
import ru.nekit.android.utils.IAutoDispose

interface IEventListenerSupport : IAutoDispose {

    val eventListener: IEventListener

    fun <T : IEvent> listenForEvent(clazz: Class<T>, body: (T) -> Unit) =
            listenForEvent(this, clazz, body)

    fun <T : IEvent> listenForEvent(observer: Any, clazz: Class<T>, body: (T) -> Unit) =
            eventListener.listen(observer, clazz, body)

    fun registerSubscriptions(observer: Any) =
            eventListener.register(observer)

    fun unregisterSubscriptions(observer: Any) =
            eventListener.stopListen(observer)

    fun registerSubscriptions() =
            registerSubscriptions(this)

    fun unregisterSubscriptions() =
            unregisterSubscriptions(this)

    fun stopListen(observer: Any) {
        eventListener.stopListen(observer)
    }

    override fun dispose() {
        stopListen(this)
        super.dispose()
    }

}