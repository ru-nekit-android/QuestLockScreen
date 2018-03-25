package ru.nekit.android.qls.quest.resources.representation

import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.resources.TrafficLightResourceCollection
import ru.nekit.android.qls.domain.model.resources.TrafficLightResourceCollection.GREEN
import ru.nekit.android.qls.domain.model.resources.TrafficLightResourceCollection.RED
import ru.nekit.android.qls.quest.resources.representation.common.ResourceRepresentationProvider
import ru.nekit.android.qls.quest.resources.representation.common.StringListIdRepresentation

object TrafficLightResourceRepresentationProvider : ResourceRepresentationProvider<TrafficLightResourceCollection,
        StringListIdRepresentation>() {


    init {
        createRepresentation(GREEN, listOf(R.string.go, R.string.cross))
        createRepresentation(RED, listOf(R.string.stand, R.string.wait))
    }

    private fun createRepresentation(key: TrafficLightResourceCollection, stringListResourceId: List<Int>) {
        createRepresentation(key, StringListIdRepresentation(stringListResourceId))
    }

}

fun TrafficLightResourceCollection.getRepresentation() = TrafficLightResourceRepresentationProvider.getRepresentation(this)
