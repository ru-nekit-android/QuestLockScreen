package ru.nekit.android.eventBus

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.event.IEventListener
import java.util.*

class EventListener(private val rxEventBus: RxEventBus) : IEventListener {

    private val mapper: MutableSet<Any> = HashSet()

    override fun stopListen(observer: Any) {
        //it can be not registered
        if (mapper.contains(observer)) {
            rxEventBus.bus.unregister(observer)
            mapper.remove(observer)
        }

    }

    override fun <T : IEvent> listen(observer: Any,
                                     clazz: Class<T>,
                                     body: (T) -> Unit) =
            listen(observer, clazz, AndroidSchedulers.mainThread(), body)

    override fun <T : IEvent> listen(observer: Any,
                                     clazz: Class<T>,
                                     scheduler: Scheduler,
                                     body: (T) -> Unit) {
        rxEventBus.bus.obtainSubscriber(clazz) {
            body(it)
        }.withScheduler(scheduler).also {
            mapper.add(observer)
            rxEventBus.bus.registerSubscriber(observer, it)
        }
    }
}