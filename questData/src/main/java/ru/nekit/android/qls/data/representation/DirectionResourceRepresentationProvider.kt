package ru.nekit.android.qls.data.representation

import ru.nekit.android.qls.domain.model.resources.DirectionResourceCollection
import ru.nekit.android.qls.domain.model.resources.DirectionResourceCollection.*
import ru.nekit.android.questData.R.string.*

object DirectionResourceRepresentationProvider : StringListIdRepresentationProvider<DirectionResourceCollection>() {

    init {
        createRepresentation(UP, quest_direction_title_up)
        createRepresentation(RIGHT, quest_direction_title_right)
        createRepresentation(DOWN, quest_direction_title_down)
        createRepresentation(LEFT, quest_direction_title_left)
    }

}

fun DirectionResourceCollection.getRepresentation() = DirectionResourceRepresentationProvider.getRepresentation(this)
