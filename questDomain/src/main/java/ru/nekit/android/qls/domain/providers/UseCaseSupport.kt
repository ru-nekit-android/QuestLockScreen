package ru.nekit.android.qls.domain.providers

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import ru.nekit.android.domain.interactor.*

interface IUseCaseSupport : IDependenciesHolder {

    var logger: ILogger

}

open class UseCaseSupport : DependenciesHolder(), IUseCaseSupport {

    override lateinit var logger: ILogger

    protected fun <T> singleUseCase(body: () -> Single<T>) =
            singleUseCase(schedulerProvider, body)

    protected fun <T> singleUseCaseFromCallable(body: () -> T) =
            singleUseCaseFromCallable(schedulerProvider, body)

    protected fun <T> buildSingleUseCase(body: () -> Single<T>) =
            buildSingleUseCase(schedulerProvider, body)

    protected fun <T> useSingleUseCase(body: () -> Single<T>, useBody: (T) -> Unit) =
            useSingleUseCase(schedulerProvider, body, useBody)

    protected fun <T> useSingleUseCase(body: () -> Single<T>) =
            useSingleUseCase(schedulerProvider, body, {})

    protected fun completableUseCase(body: () -> Completable) =
            completableUseCase(schedulerProvider, body)

    protected fun <T> buildFlowableUseCase(body: () -> Flowable<T>) =
            buildFlowableUseCase(schedulerProvider, body)

    protected fun useCompletableUseCase(body: () -> Completable, useBody: () -> Unit) =
            useCompletableUseCase(schedulerProvider, body, useBody)

    protected fun useCompletableUseCaseFromRunnable(actionBody: () -> Unit, useBody: () -> Unit) =
            useCompletableUseCaseFromRunnable(schedulerProvider, actionBody, useBody)

    protected fun useCompletableUseCaseFromRunnable(actionBody: () -> Unit) =
            useCompletableUseCaseFromRunnable(schedulerProvider, actionBody) {}

    protected fun <T> buildSingleUseCaseFromCallable(actionBody: () -> T) =
            buildSingleUseCaseFromCallable(schedulerProvider, actionBody)

    protected fun <T> useSingleUseCaseFromCallable(actionBody: () -> T, useBody: (T) -> Unit) =
            useSingleUseCaseFromCallable(schedulerProvider, actionBody, useBody)

    protected fun predicatedUseSingleUseCase(predicateBody: () -> Boolean, useBody: () -> Unit) =
            predicatedUseSingleUseCase(schedulerProvider, predicateBody, useBody)

    protected fun buildCompletableUseCase(body: () -> Completable): Completable =
            buildCompletableUseCase(schedulerProvider, body)

    protected fun <T> Observable<T>.applySchedulers(): Observable<T> =
            applySchedulers(schedulerProvider)

    protected fun <T> Flowable<T>.applySchedulers(): Flowable<T> =
            applySchedulers(schedulerProvider)

    protected fun <T> Single<T>.applySchedulers(): Single<T> =
            applySchedulers(schedulerProvider)

    protected fun Completable.applySchedulers(): Completable =
            applySchedulers(schedulerProvider)

    protected fun log(message: String) = logger.d(message)
}