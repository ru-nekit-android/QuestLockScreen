package ru.nekit.android.qls.eventBus

import com.anadeainc.rxbus.CustomSubscriber
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.nekit.android.domain.event.IEvent
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
                                     body: (T) -> Unit): CustomSubscriber<T> =
            listen(observer, clazz, AndroidSchedulers.mainThread(), body)

    override fun <T : IEvent> listen(observer: Any,
                                     clazz: Class<T>,
                                     scheduler: Scheduler,
                                     body: (T) -> Unit): CustomSubscriber<T> =
            rxEventBus.bus.obtainSubscriber(clazz) {
                body(it)
            }.withScheduler(scheduler).also {
                mapper.add(observer)
                rxEventBus.bus.registerSubscriber(observer, it)
            }
}