package ru.nekit.android.qls.data.providers

import android.support.annotation.StringRes

interface IStringResourceIdHolder {

    @get:StringRes
    val stringResourceId: Int

}