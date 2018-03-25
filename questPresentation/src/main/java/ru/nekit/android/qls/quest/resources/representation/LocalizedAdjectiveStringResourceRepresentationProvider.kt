package ru.nekit.android.qls.quest.resources.representation

import android.support.annotation.StringRes
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.resources.LocalizedAdjectiveStringResourceCollection
import ru.nekit.android.qls.domain.model.resources.LocalizedAdjectiveStringResourceCollection.*
import ru.nekit.android.qls.quest.resources.representation.common.ResourceRepresentationProvider
import ru.nekit.android.qls.quest.resources.representation.common.StringIdRepresentation

object LocalizedAdjectiveStringResourceRepresentationProvider :
        ResourceRepresentationProvider<LocalizedAdjectiveStringResourceCollection,
                StringIdRepresentation>() {

    init {
        createRepresentation(WHITE, R.string.color_white)
        createRepresentation(BLACK, R.string.color_black)
        createRepresentation(RED, R.string.color_red)
        createRepresentation(GREEN, R.string.color_green)
    }

    private fun createRepresentation(key: LocalizedAdjectiveStringResourceCollection,
                                     @StringRes stringResourceId: Int) {
        createRepresentation(key, StringIdRepresentation(stringResourceId))
    }
}

fun LocalizedAdjectiveStringResourceCollection.getRepresentation() = LocalizedAdjectiveStringResourceRepresentationProvider.getRepresentation(this)


