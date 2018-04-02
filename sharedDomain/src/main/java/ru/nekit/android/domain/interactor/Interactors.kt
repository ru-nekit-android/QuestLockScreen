package ru.nekit.android.domain.interactor

import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subscribers.DisposableSubscriber
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.utils.plus

interface IParameterlessRXExecutable<in T> {

    fun execute(observer: T)

}

interface IRXExecutable<in T, in P> {

    fun execute(parameter: P, observer: T)

}

interface IDisposable {

    fun dispose()

}

interface IUseCase<T, in P> : IRXExecutable<DisposableObserver<T>, P> {

    fun build(parameter: P): Observable<T>

    fun buildAsync(parameter: P): Observable<T>

}

interface IFlowableUseCase<T, in P> : IRXExecutable<DisposableSubscriber<T>, P> {

    fun build(parameter: P): Flowable<T>

    fun buildAsync(parameter: P): Flowable<T>

}

interface ISingleUseCase<T, in P> : IRXExecutable<DisposableSingleObserver<T>, P> {

    fun build(parameter: P): Single<T>

    fun buildAsync(parameter: P): Single<T>

}

interface IParameterlssSingleUseCase<T> : IParameterlessRXExecutable<DisposableSingleObserver<T>> {

    fun build(): Single<T>

    fun buildAsync(): Single<T>

}

interface IParameterlessFlowableUseCase<T> : IParameterlessRXExecutable<DisposableSubscriber<T>> {

    fun build(): Flowable<T>

    fun buildAsync(): Flowable<T>

}

interface ICompletableUseCase<in P> : IRXExecutable<DisposableCompletableObserver, P> {

    fun build(parameter: P): Completable

    fun buildAsync(parameter: P): Completable
}

interface IParameterlessCompletableUseCase : IParameterlessRXExecutable<DisposableCompletableObserver> {

    fun build(): Completable

    fun buildAsync(): Completable
}

abstract class RXDisposable : IDisposable {

    protected var disposables: CompositeDisposable = CompositeDisposable()

    override fun dispose() {
        disposables.clear()
    }
}

abstract class UserCase<T, in P>(private val schedulerProvider: ISchedulerProvider? = null) : RXDisposable(),
        IUseCase<T, P> {

    final override fun execute(parameter: P, observer: DisposableObserver<T>) {
        disposables += buildAsync(parameter).subscribeWith(observer)
    }

    final override fun buildAsync(parameter: P): Observable<T> = build(parameter).compose(applySchedulersObservable<T>(schedulerProvider))
}

abstract class FlowableUseCase<T, in P>(private val schedulerProvider: ISchedulerProvider? = null) : RXDisposable(),
        IFlowableUseCase<T, P> {

    final override fun execute(parameter: P, observer: DisposableSubscriber<T>) {
        disposables += buildAsync(parameter).subscribeWith(observer)
    }

    final override fun buildAsync(parameter: P): Flowable<T> = build(parameter).compose(applySchedulersFlowable<T>(schedulerProvider))
}

abstract class SingleUseCase<T, in P>(private val schedulerProvider: ISchedulerProvider? = null) : RXDisposable(),
        ISingleUseCase<T, P> {

    final override fun execute(parameter: P, observer: DisposableSingleObserver<T>) {
        disposables += buildAsync(parameter)
                .subscribeWith(observer)
    }

    final override fun buildAsync(parameter: P): Single<T> = build(parameter).compose(applySchedulers<T>(schedulerProvider))
}

abstract class ParameterlessSingleUseCase<T>(private val schedulerProvider: ISchedulerProvider? = null) : RXDisposable(),
        IParameterlssSingleUseCase<T> {

    final override fun execute(observer: DisposableSingleObserver<T>) {
        disposables += buildAsync().subscribeWith(observer)
    }

    final override fun buildAsync(): Single<T> = build().compose(applySchedulers<T>(schedulerProvider))
}

abstract class ParameterlessFlowableUseCase<T>(private val schedulerProvider: ISchedulerProvider? = null) : RXDisposable(),
        IParameterlessFlowableUseCase<T> {

    final override fun execute(observer: DisposableSubscriber<T>) {
        disposables += buildAsync().subscribeWith(observer)
    }

    final override fun buildAsync(): Flowable<T> = build().compose(applySchedulersFlowable<T>(schedulerProvider))
}

abstract class CompletableUseCase<in P>(private val schedulerProvider: ISchedulerProvider? = null) : RXDisposable(),
        ICompletableUseCase<P> {

    final override fun execute(parameter: P, observer: DisposableCompletableObserver) {
        disposables += buildAsync(parameter).subscribeWith(observer)
    }

    final override fun buildAsync(parameter: P): Completable = build(parameter).compose(applySchedulers(schedulerProvider))
}

abstract class ParameterlessCompletableUseCase(private val schedulerProvider: ISchedulerProvider? = null) : RXDisposable(),
        IParameterlessCompletableUseCase {

    final override fun execute(observer: DisposableCompletableObserver) {
        disposables += buildAsync().subscribeWith(observer)
    }

    final override fun buildAsync(): Completable = build().compose(applySchedulers(schedulerProvider))
}

fun <T> Observable<T>.applySchedulers(schedulerProvider: ISchedulerProvider): Observable<T> =
        schedulerProvider.let { subscribeOn(it.computation()).observeOn(it.ui()) }

fun <T> Flowable<T>.applySchedulers(schedulerProvider: ISchedulerProvider): Flowable<T> =
        schedulerProvider.let { subscribeOn(it.computation()).observeOn(it.ui()) }


fun <T> Single<T>.applySchedulers(schedulerProvider: ISchedulerProvider): Single<T> =
        schedulerProvider.let { subscribeOn(it.computation()).observeOn(it.ui()) }

fun Completable.applySchedulers(schedulerProvider: ISchedulerProvider): Completable =
        schedulerProvider.let { subscribeOn(it.computation()).observeOn(it.ui()) }

fun applySchedulers(schedulerProvider: ISchedulerProvider?): CompletableTransformer =
        CompletableTransformer { upstream ->
            schedulerProvider?.let { upstream.applySchedulers(it) } ?: upstream
        }

fun <T> applySchedulers(schedulerProvider: ISchedulerProvider?): SingleTransformer<T, T> =
        SingleTransformer { upstream ->
            schedulerProvider?.let { upstream.applySchedulers(it) } ?: upstream
        }

fun <T> applySchedulersFlowable(schedulerProvider: ISchedulerProvider?): FlowableTransformer<T, T> =
        FlowableTransformer { upstream ->
            schedulerProvider?.let { upstream.applySchedulers(it) } ?: upstream
        }

fun <T> applySchedulersObservable(schedulerProvider: ISchedulerProvider?): ObservableTransformer<T, T> =
        ObservableTransformer { upstream ->
            schedulerProvider?.let { upstream.applySchedulers(it) } ?: upstream
        }

fun <T> ParameterlessSingleUseCase<T>.use() = use({}, {})

fun <T> ParameterlessSingleUseCase<T>.use(bodySuccess: (T) -> Unit) = use(bodySuccess, {})

fun <T> ParameterlessSingleUseCase<T>.use(
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
            e.printStackTrace()
            dispose()
        }
    }
    execute(disposable)
}

fun completableUseCase(schedulerProvider: ISchedulerProvider?,
                       body: () -> Completable) =
        object : ParameterlessCompletableUseCase(schedulerProvider) {
            override fun build(): Completable = body()
        }

fun useCompletableUseCase(schedulerProvider: ISchedulerProvider?,
                          body: () -> Completable) =
        completableUseCase(schedulerProvider, body).use()

fun useCompletableUseCase(schedulerProvider: ISchedulerProvider?,
                          body: () -> Completable, useBody: () -> Unit) =
        completableUseCase(schedulerProvider, body).use(useBody)

fun <T> singleUseCase(schedulerProvider: ISchedulerProvider? = null,
                      body: () -> Single<T>) =
        object : ParameterlessSingleUseCase<T>(schedulerProvider) {
            override fun build(): Single<T> = body()
        }

fun <T> buildSingleUseCase(schedulerProvider: ISchedulerProvider? = null, body: () -> Single<T>) =
        singleUseCase(schedulerProvider, body).let {
            if (schedulerProvider == null)
                it.build()
            else
                it.buildAsync()
        }

fun <T> useSingleUseCase(schedulerProvider: ISchedulerProvider?,
                              body: () -> Single<T>, useBody: (T) -> Unit) =
        singleUseCase(schedulerProvider, body).use(useBody)

fun buildCompletableUseCase(schedulerProvider: ISchedulerProvider?, body: () -> Completable): Completable =
        completableUseCase(null, body).let {
            if (schedulerProvider == null)
                it.build()
            else
                it.buildAsync()
        }

fun completableUseCaseFromRunnable(schedulerProvider: ISchedulerProvider?,
                                   body: () -> Unit) =
        object : ParameterlessCompletableUseCase(schedulerProvider) {
            override fun build(): Completable = Completable.fromRunnable(body)
        }

fun useCompletableUseCaseFromRunnable(schedulerProvider: ISchedulerProvider?,
                                      actionBody: () -> Unit,
                                      useBody: () -> Unit) =
        completableUseCaseFromRunnable(schedulerProvider, actionBody).use(useBody)

fun useCompletableUseCaseFromRunnable(schedulerProvider: ISchedulerProvider?,
                                      actionBody: () -> Unit) =
        completableUseCaseFromRunnable(schedulerProvider, actionBody).use()

fun buildCompletableUseCaseFromRunnable(schedulerProvider: ISchedulerProvider? = null, actionBody: () -> Unit) =
        completableUseCaseFromRunnable(null, actionBody).let {
            if (schedulerProvider == null)
                it.build()
            else
                it.buildAsync()
        }

fun predicatedUseSingleUseCase(schedulerProvider: ISchedulerProvider?,
                               predicateBody: () -> Boolean,
                               useBody: () -> Unit) =
        singleUseCaseFromCallable(schedulerProvider, predicateBody).use { if (it) useBody() }

fun <T> singleUseCaseFromCallable(schedulerProvider: ISchedulerProvider?, actionBody: () -> T) =
        object : ParameterlessSingleUseCase<T>(schedulerProvider) {
            override fun build(): Single<T> = Single.fromCallable(actionBody)
        }

fun <T> buildSingleUseCaseFromCallable(schedulerProvider: ISchedulerProvider?,
                                       actionBody: () -> T) =
        singleUseCaseFromCallable(schedulerProvider, actionBody).let {
            if (schedulerProvider == null)
                it.build()
            else
                it.buildAsync()
        }

fun <T> useSingleUseCaseFromCallable(schedulerProvider: ISchedulerProvider?,
                                     actionBody: () -> T,
                                     useBody: (T) -> Unit) =
        singleUseCaseFromCallable(schedulerProvider, actionBody).use(useBody)

fun <T, P> SingleUseCase<T, P>.use(parameter: P, bodySuccess: (T) -> Unit) = use(parameter, bodySuccess, {})

fun <T, P> SingleUseCase<T, P>.use(
        parameter: P,
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
            e.printStackTrace()
            dispose()
        }
    }
    execute(parameter, disposable)
}

fun ParameterlessCompletableUseCase.use() = use({}, {})

fun ParameterlessCompletableUseCase.use(body: () -> Unit) = use(body, {})

fun ParameterlessCompletableUseCase.use(
        bodyComplete: () -> Unit,
        bodyError: (Throwable) -> Unit
) {
    val disposable = object : DisposableCompletableObserver() {
        override fun onComplete() {
            bodyComplete()
            dispose()
        }

        override fun onError(e: Throwable) {
            bodyError(e)
            e.printStackTrace()
            dispose()
        }
    }
    execute(disposable)
}

fun <P> CompletableUseCase<P>.use(parameter: P) = use(parameter, {})

fun <P> CompletableUseCase<P>.use(parameter: P, bodyComplete: () -> Unit) = use(parameter, bodyComplete, {})

fun <P> CompletableUseCase<P>.use(
        parameter: P,
        bodyComplete: () -> Unit,
        bodyError: (Throwable) -> Unit
) {
    val disposable = object : DisposableCompletableObserver() {
        override fun onComplete() {
            bodyComplete()
            dispose()
        }

        override fun onError(e: Throwable) {
            bodyError(e)
            dispose()
        }
    }
    execute(parameter, disposable)
}

fun <T, P> UserCase<T, P>.use(parameter: P, bodyNext: (T) -> Unit) = use(parameter, bodyNext, {}, {})

fun <T, P> UserCase<T, P>.use(
        parameter: P,
        bodyNext: (T) -> Unit,
        bodyComplete: () -> Unit,
        bodyError: (Throwable) -> Unit
) {
    val disposable = object : DisposableObserver<T>() {
        override fun onNext(t: T) {
            bodyNext(t)
        }

        override fun onComplete() {
            bodyComplete()
            dispose()
        }

        override fun onError(e: Throwable) {
            bodyError(e)
            dispose()
        }
    }
    execute(parameter, disposable)
}

fun <T, P> FlowableUseCase<T, P>.use(parameter: P, bodyNext: (T) -> Unit) = use(parameter, bodyNext, {}, {})

fun <T, P> FlowableUseCase<T, P>.use(
        parameter: P,
        bodyNext: (T) -> Unit,
        bodyComplete: () -> Unit,
        bodyError: (Throwable) -> Unit
) {
    val disposable = object : DisposableSubscriber<T>() {
        override fun onNext(t: T) {
            bodyNext(t)
        }

        override fun onComplete() {
            bodyComplete()
            dispose()
        }

        override fun onError(e: Throwable) {
            bodyError(e)
            dispose()
        }
    }
    execute(parameter, disposable)
}

fun <T> ParameterlessFlowableUseCase<T>.use(bodyNext: (T) -> Unit) = use(bodyNext, {}, {})

fun <T> ParameterlessFlowableUseCase<T>.use(
        bodyNext: (T) -> Unit,
        bodyComplete: () -> Unit,
        bodyError: (Throwable) -> Unit
) {
    val disposable = object : DisposableSubscriber<T>() {
        override fun onNext(t: T) {
            bodyNext(t)
        }

        override fun onComplete() {
            bodyComplete()
            dispose()
        }

        override fun onError(e: Throwable) {
            bodyError(e)
            dispose()
        }
    }
    execute(disposable)
}
