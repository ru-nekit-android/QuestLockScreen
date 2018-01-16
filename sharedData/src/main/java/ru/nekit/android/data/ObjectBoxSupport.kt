package ru.nekit.android.data

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.Property
import io.objectbox.query.Query
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Single
import io.reactivex.SingleSource

abstract class ObjectBoxSupport<Entity>(val boxStore: BoxStore) {

    abstract protected fun createBox(): Box<Entity>

    inline fun <reified T> initBox(): Box<T> = boxStore.boxFor()

    protected fun boxCompletableUsing(body: (Box<Entity>) -> CompletableSource) =
            boxCompletableUsing(createBox(), body)

    protected fun boxCompletableUsingFromCallable(body: (Box<Entity>) -> Unit) =
            boxCompletableUsingFromCallable(createBox(), body)

    protected fun <T> boxSingleUsing(body: (Box<Entity>) -> Single<T>) =
            boxSingleUsing(createBox(), body)

    protected fun <T> boxSingleUsingWithCallable(body: (Box<Entity>) -> T) =
            boxSingleUsingWithCallable(createBox(), body)

    protected fun Box<Entity>.queryBy(property: Property, value: String): Query<Entity> =
            query().equal(property, value).build()

    private fun <T> boxCompletableUsing(box: Box<T>, body: (Box<T>) -> CompletableSource): Completable =
            Completable.using({ box },
                    { body(it) },
                    { it.closeThreadResources() })

    private fun <T> boxCompletableUsingFromCallable(box: Box<T>, body: (Box<T>) -> Unit): Completable =
            boxCompletableUsing(box, { Completable.fromCallable { body(box) } })

    private fun <T, R> boxSingleUsing(box: Box<T>, body: (Box<T>) -> SingleSource<R>): Single<R> =
            Single.using({ box },
                    { body(it) },
                    { it.closeThreadResources() })

    private fun <T, R> boxSingleUsingWithCallable(box: Box<T>, body: (Box<T>) -> R): Single<R> =
            boxSingleUsing(box, { Single.fromCallable { body(box) } })

    inline fun <reified Entity> BoxStore.boxFor(): Box<Entity> = boxFor(Entity::class.java)
}