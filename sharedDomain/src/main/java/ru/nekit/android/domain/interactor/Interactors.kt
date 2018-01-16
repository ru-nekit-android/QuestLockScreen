package ru.nekit.android.domain.interactor

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import ru.nekit.android.domain.executor.ISchedulerProvider

interface IRXExecutable<in T, in P> {

    fun execute(parameter: P? = null, observer: T)
    fun execute(observer: T) {
        Schedulers.trampoline()
        execute(null, observer)
    }

}

interface IDisposable {

    fun dispose()

}

interface IUseCase<T, in P> : IRXExecutable<DisposableObserver<T>, P> {

    fun buildUseCase(parameter: P? = null): Observable<T>

}

interface IFlowableUseCase<T, in P> : IRXExecutable<DisposableSubscriber<T>, P> {

    fun buildUseCase(parameter: P? = null): Flowable<T>

}

interface ISingleUseCase<T, in P> : IRXExecutable<DisposableSingleObserver<T>, P> {

    fun buildUseCase(parameter: P? = null): Single<T>

}

interface ICompletableUseCase<in P> : IRXExecutable<DisposableCompletableObserver, P> {

    fun buildUseCase(parameter: P? = null): Completable
}

abstract class RXDisposable : IDisposable {

    protected var disposables: CompositeDisposable = CompositeDisposable()

    override fun dispose() {
        disposables.clear()
    }
}

abstract class UserCase<T, in P>(private val schedulerProvider: ISchedulerProvider? = null) : RXDisposable(),
        IUseCase<T, P> {

    final override fun execute(parameter: P?, observer: DisposableObserver<T>) {
        disposables += buildUseCase(parameter)
                .applyExecutors(schedulerProvider)
                .subscribeWith(observer)
    }
}

abstract class FlowableUserCase<T, in P>(private val schedulerProvider: ISchedulerProvider? = null) : RXDisposable(),
        IFlowableUseCase<T, P> {

    final override fun execute(parameter: P?, observer: DisposableSubscriber<T>) {
        disposables +=
                buildUseCase(parameter)
                        .applyExecutors(schedulerProvider)
                        .subscribeWith(observer)
    }
}


abstract class SingleUseCase<T, in P>(private val schedulerProvider: ISchedulerProvider? = null) : RXDisposable(),
        ISingleUseCase<T, P> {

    final override fun execute(parameter: P?, observer: DisposableSingleObserver<T>) {
        disposables += buildUseCase(parameter)
                .applyExecutors(schedulerProvider)
                .subscribeWith(observer)
    }
}

abstract class CompletableUseCase<in P>(private val schedulerProvider: ISchedulerProvider? = null) : RXDisposable(),
        ICompletableUseCase<P> {

    final override fun execute(parameter: P?, observer: DisposableCompletableObserver) {
        disposables += buildUseCase(parameter)
                .applyExecutors(schedulerProvider)
                .subscribeWith(observer)
    }
}

operator fun CompositeDisposable.plus(disposable: Disposable): CompositeDisposable {
    add(disposable)
    return this
}

fun <T> Observable<T>.applyExecutors(schedulerProvider: ISchedulerProvider? = null): Observable<T> {
    safe(schedulerProvider) {
        with(it) {
            subscribeOn(computation())
            observeOn(ui())
        }
    }
    return this
}

fun <T> Flowable<T>.applyExecutors(schedulerProvider: ISchedulerProvider? = null): Flowable<T> {
    safe(schedulerProvider) {
        with(it) {
            subscribeOn(computation())
            observeOn(ui())
        }
    }
    return this
}

fun <T> Single<T>.applyExecutors(schedulerProvider: ISchedulerProvider? = null): Single<T> {
    safe(schedulerProvider) {
        with(it) {
            subscribeOn(computation())
            observeOn(ui())
        }
    }
    return this
}

fun Completable.applyExecutors(schedulerProvider: ISchedulerProvider? = null): Completable {
    safe(schedulerProvider) {
        with(it) {
            subscribeOn(computation())
            observeOn(ui())
        }
    }
    return this
}

inline fun <reified T> safe(value: T?, action: (T) -> Unit) {
    if (value != null) action(value)
}

fun <T, P> SingleUseCase<T, P>.call(parameter: P?) = call(parameter, {}, {})

fun <T, P> SingleUseCase<T, P>.call(
        parameter: P? = null,
        bodySuccess: (T) -> Unit,
        bodyError: (Throwable) -> Unit
) {
    val disposable = object : DisposableSingleObserver<T>() {
        override fun onSuccess(t: T) {
            bodySuccess(t)
            dispose()
        }

        override fun onError(e: Throwable) {
            bodyError(e)
        }
    }
    execute(parameter, disposable)
}

fun <P> CompletableUseCase<P>.call(parameter: P?): DisposableCompletableObserver = call(parameter, {}, {})

fun <P> CompletableUseCase<P>.call(
        parameter: P? = null,
        bodyComplete: () -> Unit,
        bodyError: (Throwable) -> Unit
): DisposableCompletableObserver {
    val disposable = object : DisposableCompletableObserver() {
        override fun onComplete() {
            bodyComplete()
        }

        override fun onError(e: Throwable) {
            bodyError(e)
        }
    }
    execute(parameter, disposable)
    return disposable
}

fun <T, P> UserCase<T, P>.call(parameter: P? = null): DisposableObserver<T> = call(parameter, {}, {}, {})

fun <T, P> UserCase<T, P>.call(
        parameter: P?,
        bodyNext: (T) -> Unit,
        bodyComplete: () -> Unit,
        bodyError: (Throwable) -> Unit
): DisposableObserver<T> {
    val disposable = object : DisposableObserver<T>() {
        override fun onNext(t: T) {
            bodyNext(t)
        }

        override fun onComplete() {
            bodyComplete()
        }

        override fun onError(e: Throwable) {
            bodyError(e)
        }
    }
    execute(parameter, disposable)
    return disposable
}

fun <T, P> FlowableUserCase<T, P>.call(parameter: P? = null): DisposableSubscriber<T> = call(parameter, {}, {}, {})

fun <T, P> FlowableUserCase<T, P>.call(
        parameter: P?,
        bodyNext: (T) -> Unit,
        bodyComplete: () -> Unit,
        bodyError: (Throwable) -> Unit
): DisposableSubscriber<T> {
    val disposable = object : DisposableSubscriber<T>() {
        override fun onNext(t: T) {
            bodyNext(t)
        }

        override fun onComplete() {
            bodyComplete()
        }

        override fun onError(e: Throwable) {
            bodyError(e)
        }
    }
    execute(parameter, disposable)
    return disposable
}