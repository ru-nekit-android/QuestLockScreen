package ru.nekit.android.qls.data.providers

import android.content.Context
import android.support.v4.content.res.ResourcesCompat

interface IDrawableResourceProvider : IDrawableResourceIdHolder {

    fun getDrawable(context: Context) = ResourcesCompat.getDrawable(context.resources,
            drawableResourceId, null)

}