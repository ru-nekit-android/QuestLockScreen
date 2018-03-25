package ru.nekit.android.qls.data.providers

import android.support.annotation.ColorRes

interface IColorResourceIdProvider {

    @get:ColorRes
    val colorResourceId: Int

}

