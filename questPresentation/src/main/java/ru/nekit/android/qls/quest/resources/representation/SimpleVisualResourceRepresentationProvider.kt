package ru.nekit.android.qls.quest.resources.representation

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.resources.LocalizedNounStringResourceCollection
import ru.nekit.android.qls.domain.model.resources.SimpleVisualResourceCollection
import ru.nekit.android.qls.domain.model.resources.SimpleVisualResourceCollection.*
import ru.nekit.android.qls.quest.resources.representation.common.ResourceRepresentationProvider
import ru.nekit.android.qls.quest.resources.representation.common.SimpleVisualResourceRepresentation

object SimpleVisualResourceRepresentationProvider : ResourceRepresentationProvider<SimpleVisualResourceCollection,
        SimpleVisualResourceRepresentation>() {

    init {
        createRepresentation(MINUS,
                R.drawable.qvri_minus,
                R.string.qvri_minus_title)
        createRepresentation(PLUS,
                R.drawable.qvri_plus,
                R.string.qvri_plus_title)
        createRepresentation(EQUAL,
                R.drawable.qvri_equal,
                R.string.qvri_equal_title)
        createRepresentation(ORANGE,
                R.drawable.qvri_orange,
                LocalizedNounStringResourceCollection.ORANGE)
        createRepresentation(STRAWBERRY,
                R.drawable.qvri_strawberry,
                LocalizedNounStringResourceCollection.STRAWBERRY)
        createRepresentation(APPLE,
                R.drawable.qvri_apple,
                LocalizedNounStringResourceCollection.APPLE)
        createRepresentation(PEAR,
                R.drawable.qvri_pear,
                LocalizedNounStringResourceCollection.PEAR)
        createRepresentation(CHERRY,
                R.drawable.qvri_cherry,
                LocalizedNounStringResourceCollection.CHERRY)
        createRepresentation(RASPBERRY,
                R.drawable.qvri_raspberry,
                LocalizedNounStringResourceCollection.RASPBERRY)
        createRepresentation(PINEAPPLE,
                R.drawable.qvri_pineapple,
                LocalizedNounStringResourceCollection.PINEAPPLE)
        createRepresentation(BLACKBERRY,
                R.drawable.qvri_blackberry,
                LocalizedNounStringResourceCollection.BLACKBERRY)
        createRepresentation(WINTER,
                R.drawable.qvri_tree_winter,
                LocalizedNounStringResourceCollection.WINTER)
        createRepresentation(SPRING,
                R.drawable.qvri_tree_spring,
                LocalizedNounStringResourceCollection.SPRING)
        createRepresentation(SUMMER,
                R.drawable.qvri_tree_summer,
                LocalizedNounStringResourceCollection.SUMMER)
        createRepresentation(FALL,
                R.drawable.qvri_tree_fall,
                LocalizedNounStringResourceCollection.FALL)
    }

    private fun createRepresentation(key: SimpleVisualResourceCollection,
                                     @DrawableRes drawableResourceId: Int,
                                     @StringRes stringResourceId: Int) {
        createRepresentation(key, SimpleVisualResourceRepresentation(key, drawableResourceId, stringResourceId))
    }

    private fun createRepresentation(key: SimpleVisualResourceCollection,
                                     @DrawableRes drawableResourceId: Int,
                                     localizedNounStringResourceCollection: LocalizedNounStringResourceCollection) {
        createRepresentation(key, SimpleVisualResourceRepresentation(key,
                drawableResourceId, localizedNounStringResourceCollection))
    }

}

fun SimpleVisualResourceCollection.getRepresentation() = SimpleVisualResourceRepresentationProvider.getRepresentation(this)

