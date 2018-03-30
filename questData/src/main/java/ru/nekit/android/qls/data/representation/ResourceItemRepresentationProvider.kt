package ru.nekit.android.qls.data.representation

import ru.nekit.android.qls.domain.model.resources.ChildrenToysVisualResourceCollection
import ru.nekit.android.qls.domain.model.resources.CoinVisualResourceCollection
import ru.nekit.android.qls.domain.model.resources.ColorResourceCollection
import ru.nekit.android.qls.domain.model.resources.SimpleVisualResourceCollection
import ru.nekit.android.qls.domain.model.resources.common.IResourceHolder

fun IResourceHolder.getRepresentation() = when (this) {
    is ColorResourceCollection -> getRepresentation()
    is SimpleVisualResourceCollection -> getRepresentation()
    is ChildrenToysVisualResourceCollection -> getRepresentation()
    is CoinVisualResourceCollection -> getRepresentation()
    else -> throw NotImplementedError("This type of resource item is not implemented")
}

