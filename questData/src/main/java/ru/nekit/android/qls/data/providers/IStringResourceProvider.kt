package ru.nekit.android.qls.data.providers

import android.content.Context

interface IStringResourceProvider : IStringResourceIdHolder {

    fun getString(context: Context): String = context.getString(stringResourceId)

}