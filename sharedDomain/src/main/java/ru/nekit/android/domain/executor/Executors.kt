package ru.nekit.android.domain.executor

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

interface ISchedulerProvider {

    fun computation(): Scheduler

    fun newThread(): Scheduler = Schedulers.newThread()

    fun ui(): Scheduler
}

interface IIOSchedulerProvider : ISchedulerProvider {

    fun io(): Scheduler

}

class ImmediateSchedulerProvider : IIOSchedulerProvider {

    override fun computation(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun io(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun ui(): Scheduler {
        return Schedulers.trampoline()
    }
}
