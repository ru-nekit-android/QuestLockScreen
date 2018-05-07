package ru.nekit.android.domain.event

import io.reactivex.Scheduler

interface IEventListener {

    fun register(observer: Any)

    fun <T : IEvent> listen(observer: Any, clazz: Class<T>, body: (T) -> Unit)

    fun <T : IEvent> listen(observer: Any, clazz: Class<T>, scheduler: Scheduler, body: (T) -> Unit)

    fun stopListen(observer: Any)

}