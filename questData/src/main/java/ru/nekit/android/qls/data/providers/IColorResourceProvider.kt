package ru.nekit.android.qls.data.providers

import android.content.Context
import android.support.annotation.ColorInt
import android.support.v4.content.res.ResourcesCompat

interface IColorResourceProvider : IColorResourceIdProvider {

    @ColorInt
    fun getColor(context: Context) = ResourcesCompat.getColor(context.resources, colorResourceId, null)

}

