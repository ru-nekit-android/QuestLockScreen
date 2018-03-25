package ru.nekit.android.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.model.Optional

interface IKeyValueStore<in Key, Value> {

    fun put(key: Key, value: Value)

    fun get(key: Key): Value?

    fun get(key: Key, default: Value): Value?

    fun contains(key: Key): Boolean

    fun remove(key: Key)

}

interface ISingleKeyValueStore<Value> {

    fun put(value: Value)

    fun get(): Value?

    fun get(default: Value): Value?

    fun contains(): Boolean

    fun remove()
}


interface IStringKeyValueStore<Value> : IKeyValueStore<String, Value> {

    override fun put(key: String, value: Value)

    override fun get(key: String): Value

    override fun get(key: String, default: Value): Value

    override fun contains(key: String): Boolean

}

interface IReactiveKeyValueStore<in Key, Value> {

    fun put(key: Key, value: Value): Completable

    fun get(key: Key): Single<Optional<Value>>

    fun contains(key: Key): Single<Boolean>

    fun remove(key: String): Completable

}

interface IReactiveStringKeyValueStore<Value> : IReactiveKeyValueStore<String, Value> {

    override fun put(key: String, value: Value): Completable

    override fun get(key: String): Single<Optional<Value>>

    override fun contains(key: String): Single<Boolean>

    override fun remove(key: String): Completable

}

interface IReactiveCRUD<R, in T> {

    fun create(value: R): Completable

    fun read(value: T): Single<Optional<R>>

    fun update(value: R): Completable

    fun delete(value: R): Completable

}

interface IEntityMapper<E, I> : IEntityFromMapper<E, I>, IEntityToMapper<I, E>

interface IEntityToMapper<in E, out I> {

    fun to(value: E): I

}

interface IEntityFromMapper<in E, out I> {

    fun from(value: E): I

}

interface ICounter {

    var startValue: Int

    val value: Int

    fun reset()

    fun zeroWasReached(): Boolean

    fun countDown()

    fun countUp()

}

interface IStateRepository<in T> {

    fun add(value: T): Completable

    fun has(value: T): Single<Boolean>

    fun clear(): Completable

    fun remove(value: T): Completable

    fun replace(oldValue: T, newValue: T): Completable

}