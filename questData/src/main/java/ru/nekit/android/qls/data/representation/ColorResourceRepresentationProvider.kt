package ru.nekit.android.qls.data.representation

import ru.nekit.android.qls.domain.model.resources.ColorResourceCollection
import ru.nekit.android.qls.domain.model.resources.LocalizedAdjectiveStringResourceCollection
import ru.nekit.android.qls.domain.model.resources.LocalizedAdjectiveStringResourceCollection.*
import ru.nekit.android.questData.R.color.*

object ColorResourceRepresentationProvider : ResourceRepresentationProvider<ColorResourceCollection,
        ColorResourceRepresentation>() {

    init {

        createRepresentation(ColorResourceCollection.BLACK, black, BLACK)
        createRepresentation(ColorResourceCollection.WHITE, white, WHITE)
        createRepresentation(ColorResourceCollection.GREEN, green, GREEN)
        createRepresentation(ColorResourceCollection.RED, red, RED)

    }

    private fun createRepresentation(key: ColorResourceCollection,
                                     colorResId: Int,
                                     adjectiveStringResourceCollection: LocalizedAdjectiveStringResourceCollection) {
        createRepresentation(key, ColorResourceRepresentation(colorResId, adjectiveStringResourceCollection))
    }

}

fun ColorResourceCollection.getRepresentation() = ColorResourceRepresentationProvider.getRepresentation(this)
