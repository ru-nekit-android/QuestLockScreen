package ru.nekit.android.eventBus

import com.anadeainc.rxbus.Bus
import ru.nekit.android.utils.SingletonHolder

class RxEventBus private constructor(val bus: Bus) {

    companion object : SingletonHolder<RxEventBus, Bus>(::RxEventBus)

    fun post(event: Any) = bus.post(event)

}