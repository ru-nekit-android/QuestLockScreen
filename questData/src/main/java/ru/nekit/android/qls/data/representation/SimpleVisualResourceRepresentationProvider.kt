package ru.nekit.android.qls.data.representation

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import ru.nekit.android.qls.domain.model.resources.LocalizedNounStringResourceCollection
import ru.nekit.android.qls.domain.model.resources.SimpleVisualResourceCollection
import ru.nekit.android.qls.domain.model.resources.SimpleVisualResourceCollection.*
import ru.nekit.android.questData.R
import ru.nekit.android.questData.R.drawable.*

object SimpleVisualResourceRepresentationProvider : ResourceRepresentationProvider<SimpleVisualResourceCollection,
        SimpleVisualResourceRepresentation>() {

    init {
        createRepresentation(MINUS,
                qvri_minus,
                R.string.qvri_minus_title)
        createRepresentation(PLUS,
                qvri_plus,
                R.string.qvri_plus_title)
        createRepresentation(EQUAL,
                qvri_equal,
                R.string.qvri_equal_title)
        createRepresentation(ORANGE,
                qvri_orange,
                LocalizedNounStringResourceCollection.ORANGE)
        createRepresentation(STRAWBERRY,
                qvri_strawberry,
                LocalizedNounStringResourceCollection.STRAWBERRY)
        createRepresentation(APPLE,
                qvri_apple,
                LocalizedNounStringResourceCollection.APPLE)
        createRepresentation(PEAR,
                qvri_pear,
                LocalizedNounStringResourceCollection.PEAR)
        createRepresentation(CHERRY,
                qvri_cherry,
                LocalizedNounStringResourceCollection.CHERRY)
        createRepresentation(RASPBERRY,
                qvri_raspberry,
                LocalizedNounStringResourceCollection.RASPBERRY)
        createRepresentation(PINEAPPLE,
                qvri_pineapple,
                LocalizedNounStringResourceCollection.PINEAPPLE)
        createRepresentation(BLACKBERRY,
                qvri_blackberry,
                LocalizedNounStringResourceCollection.BLACKBERRY)
        createRepresentation(WINTER,
                qvri_tree_winter,
                LocalizedNounStringResourceCollection.WINTER)
        createRepresentation(SPRING,
                qvri_tree_spring,
                LocalizedNounStringResourceCollection.SPRING)
        createRepresentation(SUMMER,
                qvri_tree_summer,
                LocalizedNounStringResourceCollection.SUMMER)
        createRepresentation(FALL,
                qvri_tree_fall,
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

