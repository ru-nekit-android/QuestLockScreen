package ru.nekit.android.qls.domain.providers

interface IBilling {

    fun start()

    fun destroy()

    fun querySKUPurchases()

    fun querySKUDetails()

}