package ru.nekit.android.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.model.Optional

interface IKeyValueStore<in Key, Value> {

    fun put(key: Key, value: Value)

    fun get(key: Key): Value?

    fun contains(key: Key): Boolean

}

interface IReactiveCRUD<R, in T> {

    fun create(value: R): Completable

    fun read(value: T): Single<Optional<R>>

    fun update(value: R): Completable

    fun delete(value: R): Completable

}

interface EntityMapper<E, I> {

    fun from(value: E): I

    fun to(value: I): E

}