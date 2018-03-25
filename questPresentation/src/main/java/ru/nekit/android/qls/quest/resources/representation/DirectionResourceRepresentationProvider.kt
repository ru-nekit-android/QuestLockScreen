package ru.nekit.android.qls.quest.resources.representation

import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.resources.DirectionResourceCollection
import ru.nekit.android.qls.domain.model.resources.DirectionResourceCollection.*
import ru.nekit.android.qls.quest.resources.representation.common.ResourceRepresentationProvider
import ru.nekit.android.qls.quest.resources.representation.common.StringListIdRepresentation

object DirectionResourceRepresentationProvider : ResourceRepresentationProvider<DirectionResourceCollection,
        StringListIdRepresentation>() {


    init {
        createRepresentation(UP, listOf(R.string.quest_direction_title_up))
        createRepresentation(RIGHT, listOf(R.string.quest_direction_title_right))
        createRepresentation(DOWN, listOf(R.string.quest_direction_title_down))
        createRepresentation(LEFT, listOf(R.string.quest_direction_title_left))

    }

    private fun createRepresentation(key: DirectionResourceCollection, stringListResourceId: List<Int>) {
        createRepresentation(key, StringListIdRepresentation(stringListResourceId))
    }

}

fun DirectionResourceCollection.getRepresentation() = DirectionResourceRepresentationProvider.getRepresentation(this)
