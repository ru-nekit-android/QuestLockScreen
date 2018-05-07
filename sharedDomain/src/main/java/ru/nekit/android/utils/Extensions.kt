package ru.nekit.android.utils

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

operator fun CompositeDisposable.plus(disposable: Disposable): CompositeDisposable {
    add(disposable)
    return this
}

fun Completable.doIfOrComplete(predicate: () -> Boolean): Completable = if (predicate()) this else Completable.complete()
fun Completable.asBooleanSingleIf(predicate: () -> Boolean): Single<Boolean> = predicate().let {
    if (it)
        toSingleDefault(it)
    else
        Single.just(it)
}

fun <T> Completable.asSingleIf(result: T, predicate: () -> Boolean): Single<T> = predicate().let {
    if (it)
        toSingleDefault(result)
    else
        Single.just(result)
}

fun Completable.doIfOrNever(predicate: () -> Boolean): Completable = if (predicate()) this else Completable.never()
fun <T> Single<T>.doIfOrNever(predicate: () -> Boolean): Single<T> = if (predicate()) this else Single.never()

fun Boolean.toSingle(): Single<Boolean> = Single.just(this)
fun Int.toSingle(): Single<Int> = Single.just(this)
