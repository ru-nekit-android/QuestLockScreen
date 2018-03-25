package ru.nekit.android.qls.data.providers

import android.content.Context
import ru.nekit.android.utils.MathUtils

interface IStringListResourceProvider : IStringListResourceIdHolder {

    fun getString(context: Context, index: Int = 0): String =
            context.getString(stringListResourceId[index])

    fun getRandomString(context: Context): String =
            getString(context, MathUtils.randByListLength(stringListResourceId))

}