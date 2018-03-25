package ru.nekit.android.qls.data.providers

import android.support.annotation.StringRes

interface IStringListResourceIdHolder {

    @get:StringRes
    val stringListResourceId: List<Int>

}