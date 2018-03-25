package ru.nekit.android.domain.model

data class Optional<out T>(val data: T? = null) {

    fun isEmpty(): Boolean {
        return data == null
    }

    fun isNotEmpty(): Boolean {
        return data != null
    }

    val nonNullData: T
        get() = data!!

}