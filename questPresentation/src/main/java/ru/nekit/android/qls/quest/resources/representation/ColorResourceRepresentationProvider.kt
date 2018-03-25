package ru.nekit.android.qls.quest.resources.representation

import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.resources.ColorResourceCollection
import ru.nekit.android.qls.domain.model.resources.LocalizedAdjectiveStringResourceCollection
import ru.nekit.android.qls.quest.resources.representation.common.ColorResourceRepresentation
import ru.nekit.android.qls.quest.resources.representation.common.ResourceRepresentationProvider

object ColorResourceRepresentationProvider : ResourceRepresentationProvider<ColorResourceCollection,
        ColorResourceRepresentation>() {

    init {

        createRepresentation(ColorResourceCollection.BLACK, R.color.black, LocalizedAdjectiveStringResourceCollection.BLACK)
        createRepresentation(ColorResourceCollection.WHITE, R.color.white, LocalizedAdjectiveStringResourceCollection.WHITE)
        createRepresentation(ColorResourceCollection.GREEN, R.color.green, LocalizedAdjectiveStringResourceCollection.GREEN)
        createRepresentation(ColorResourceCollection.RED, R.color.red, LocalizedAdjectiveStringResourceCollection.RED)

    }

    private fun createRepresentation(key: ColorResourceCollection,
                                     colorResId: Int,
                                     adjectiveStringResourceCollection: LocalizedAdjectiveStringResourceCollection) {
        createRepresentation(key, ColorResourceRepresentation(colorResId, adjectiveStringResourceCollection))
    }

}

fun ColorResourceCollection.getRepresentation() = ColorResourceRepresentationProvider.getRepresentation(this)
