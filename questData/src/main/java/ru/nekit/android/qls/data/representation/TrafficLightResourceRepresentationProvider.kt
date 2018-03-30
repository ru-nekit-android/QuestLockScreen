package ru.nekit.android.qls.data.representation

import ru.nekit.android.qls.domain.model.resources.TrafficLightResourceCollection
import ru.nekit.android.qls.domain.model.resources.TrafficLightResourceCollection.GREEN
import ru.nekit.android.qls.domain.model.resources.TrafficLightResourceCollection.RED
import ru.nekit.android.questData.R.string.*

object TrafficLightResourceRepresentationProvider : StringListIdRepresentationProvider<TrafficLightResourceCollection>() {


    init {
        createRepresentation(GREEN, listOf(go, cross))
        createRepresentation(RED, listOf(stand, wait))
    }

}

fun TrafficLightResourceCollection.getRepresentation() = TrafficLightResourceRepresentationProvider.getRepresentation(this)
