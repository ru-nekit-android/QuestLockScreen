package ru.nekit.android.domain.model

import io.reactivex.Single

data class Optional<out T>(val data: T? = null) {

    fun isEmpty(): Boolean {
        return data == null
    }

    fun isNotEmpty(): Boolean {
        return data != null
    }

    val nonNullData: T
        get() = data!!

    companion object {
        fun <T> setValueIf(value: T, predicate: Boolean): Optional<T> = Optional(if (predicate) value else null)

        fun <T> just(data: T? = null): Single<Optional<T>> = Single.just(Optional(data))
    }

}

