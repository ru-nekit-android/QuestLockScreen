package ru.nekit.android.qls.eventBus

import com.anadeainc.rxbus.CustomSubscriber
import io.reactivex.Scheduler
import ru.nekit.android.domain.event.IEvent

interface IEventListener {

    fun <T : IEvent> listen(observer: Any, clazz: Class<T>, body: (T) -> Unit): CustomSubscriber<T>

    fun <T : IEvent> listen(observer: Any, clazz: Class<T>, scheduler: Scheduler, body: (T) -> Unit): CustomSubscriber<T>

    fun stopListen(observer: Any)

}