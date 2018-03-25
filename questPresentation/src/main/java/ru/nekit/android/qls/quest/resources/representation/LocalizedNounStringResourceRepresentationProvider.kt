package ru.nekit.android.qls.quest.resources.representation

import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.resources.LocalizedNounStringResourceCollection
import ru.nekit.android.qls.domain.utils.Declension
import ru.nekit.android.qls.domain.utils.Declension.Gender.*
import ru.nekit.android.qls.quest.resources.representation.common.LocalizedNounStringResourceRepresentation
import ru.nekit.android.qls.quest.resources.representation.common.ResourceRepresentationProvider

object LocalizedNounStringResourceRepresentationProvider : ResourceRepresentationProvider<
        LocalizedNounStringResourceCollection,
        LocalizedNounStringResourceRepresentation>() {

    init {
        createRepresentation(LocalizedNounStringResourceCollection.CAR,
                R.string.qvri_car_title,
                FEMALE,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.BOOTS,
                R.string.qvri_doll_boots_title,
                MALE,
                true)
        createRepresentation(LocalizedNounStringResourceCollection.DOLL_SKIRT, R.string.qvri_doll_skirt_title,
                FEMALE,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.DOLL_BLOUSE, R.string.qvri_doll_blouse_title,
                FEMALE,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.WINTER,
                R.string.qvri_winter_title,
                FEMALE,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.FALL,
                R.string.qvri_fall_title,
                FEMALE,
                false, "ь")
        createRepresentation(LocalizedNounStringResourceCollection.SPRING,
                R.string.qvri_spring_title,
                FEMALE,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.SUMMER,
                R.string.qvri_summer_title,
                NEUTER,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.ORANGE,
                R.string.qvri_orange_title,
                MALE,
                false, "")
        createRepresentation(LocalizedNounStringResourceCollection.STRAWBERRY,
                R.string.qvri_strawberry_title,
                FEMALE,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.APPLE,
                R.string.qvri_apple_title,
                NEUTER,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.PEAR,
                R.string.qvri_pear_title,
                FEMALE,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.CHERRY,
                R.string.qvri_cherry_title,
                FEMALE,
                false, "ю")
        createRepresentation(LocalizedNounStringResourceCollection.RASPBERRY,
                R.string.qvri_raspberry_title,
                FEMALE, false)
        createRepresentation(LocalizedNounStringResourceCollection.PINEAPPLE,
                R.string.qvri_pineapple_title,
                MALE,
                false,
                "")
        createRepresentation(LocalizedNounStringResourceCollection.BLACKBERRY,
                R.string.qvri_blackberry_title,
                FEMALE,
                false)
    }

    private fun createRepresentation(key: LocalizedNounStringResourceCollection,
                                     stringResourceId: Int,
                                     gender: Declension.Gender,
                                     isPlural: Boolean,
                                     vararg ownRule: String) {
        createRepresentation(key, LocalizedNounStringResourceRepresentation(stringResourceId, gender, isPlural, ownRule.toList()))
    }


}

fun LocalizedNounStringResourceCollection.getRepresentation() = LocalizedNounStringResourceRepresentationProvider.getRepresentation(this)

