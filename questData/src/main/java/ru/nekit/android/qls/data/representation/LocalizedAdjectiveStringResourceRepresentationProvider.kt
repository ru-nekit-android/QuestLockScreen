package ru.nekit.android.qls.data.representation

import android.support.annotation.StringRes
import ru.nekit.android.qls.domain.model.resources.LocalizedAdjectiveStringResourceCollection
import ru.nekit.android.qls.domain.model.resources.LocalizedAdjectiveStringResourceCollection.*
import ru.nekit.android.questData.R.string.*

object LocalizedAdjectiveStringResourceRepresentationProvider :
        ResourceRepresentationProvider<LocalizedAdjectiveStringResourceCollection,
                StringIdRepresentation>() {

    init {
        createRepresentation(WHITE, color_white)
        createRepresentation(BLACK, color_black)
        createRepresentation(RED, color_red)
        createRepresentation(GREEN, color_green)
    }

    private fun createRepresentation(key: LocalizedAdjectiveStringResourceCollection,
                                     @StringRes stringResourceId: Int) {
        createRepresentation(key, StringIdRepresentation(stringResourceId))
    }
}

fun LocalizedAdjectiveStringResourceCollection.getRepresentation() = LocalizedAdjectiveStringResourceRepresentationProvider.getRepresentation(this)


