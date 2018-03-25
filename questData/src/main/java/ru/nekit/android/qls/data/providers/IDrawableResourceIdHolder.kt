package ru.nekit.android.qls.data.providers

import android.support.annotation.DrawableRes

interface IDrawableResourceIdHolder {

    @get:DrawableRes
    val drawableResourceId: Int

}