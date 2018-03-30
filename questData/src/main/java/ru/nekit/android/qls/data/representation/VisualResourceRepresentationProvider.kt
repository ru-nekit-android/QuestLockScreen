package ru.nekit.android.qls.data.representation

import android.support.annotation.DrawableRes
import ru.nekit.android.qls.data.providers.IDrawableResourceProvider
import ru.nekit.android.qls.domain.model.resources.common.IVisualResourceHolder

fun IVisualResourceHolder.getDrawableRepresentation(): IDrawableResourceProvider = getRepresentation() as IDrawableResourceProvider

@DrawableRes
fun IVisualResourceHolder.getDrawableId(): Int = getDrawableRepresentation().drawableResourceId