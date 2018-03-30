package ru.nekit.android.qls.data.representation.common

import android.support.annotation.DrawableRes

class ColorfullVisualResourceStruct(@param:DrawableRes
                                    @field:DrawableRes
                                    val drawableResourceId: Int,
                                    var colorType: ColorType) {

    enum class ColorType {

        NONE,
        PRIMARY,
        SECONDARY,
        PRIMARY_INVERSE,
        SECONDARY_INVERSE

    }
}