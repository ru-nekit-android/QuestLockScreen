package ru.nekit.android.qls.domain.providers

interface ILogger {

    fun d(message: String)

    fun e(message: String)

    fun e(throwable: Throwable)

}