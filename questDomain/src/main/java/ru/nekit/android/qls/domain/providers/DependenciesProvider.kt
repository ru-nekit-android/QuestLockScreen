package ru.nekit.android.qls.domain.providers

import ru.nekit.android.domain.event.IEventListener
import ru.nekit.android.domain.event.IEventSender
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.qls.domain.repository.IRepositoryHolder

interface IDependenciesProvider {
    var repository: IRepositoryHolder
    var schedulerProvider: ISchedulerProvider
    var timeProvider: ITimeProvider
    var eventSender: IEventSender
    var eventListener: IEventListener
    var screenProvider: IScreenProvider
}

open class DependenciesProvider : IDependenciesProvider {

    override lateinit var repository: IRepositoryHolder
    override lateinit var schedulerProvider: ISchedulerProvider
    override lateinit var timeProvider: ITimeProvider
    override lateinit var eventSender: IEventSender
    override lateinit var eventListener: IEventListener
    override lateinit var screenProvider: IScreenProvider
}
