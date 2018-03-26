package ru.nekit.android.qls.domain.providers

import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.qls.domain.repository.IRepositoryHolder

interface IDependenciesProvider {

    var repository: IRepositoryHolder
    var schedulerProvider: ISchedulerProvider
    var timeProvider: ITimeProvider
    var eventSender: IEventSender
}
