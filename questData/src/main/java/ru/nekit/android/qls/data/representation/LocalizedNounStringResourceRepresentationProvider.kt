package ru.nekit.android.qls.data.representation

import ru.nekit.android.qls.data.repository.Declension
import ru.nekit.android.qls.data.repository.Declension.Gender.*
import ru.nekit.android.qls.domain.model.resources.LocalizedNounStringResourceCollection
import ru.nekit.android.questData.R.string.*

object LocalizedNounStringResourceRepresentationProvider : ResourceRepresentationProvider<
        LocalizedNounStringResourceCollection,
        LocalizedNounStringResourceRepresentation>() {

    init {
        createRepresentation(LocalizedNounStringResourceCollection.CAR,
                qvri_car_title,
                FEMALE,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.BOOTS,
                qvri_doll_boots_title,
                MALE,
                true)
        createRepresentation(LocalizedNounStringResourceCollection.DOLL_SKIRT, qvri_doll_skirt_title,
                FEMALE,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.DOLL_BLOUSE, qvri_doll_blouse_title,
                FEMALE,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.WINTER,
                qvri_winter_title,
                FEMALE,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.FALL,
                qvri_fall_title,
                FEMALE,
                false, "ь")
        createRepresentation(LocalizedNounStringResourceCollection.SPRING,
                qvri_spring_title,
                FEMALE,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.SUMMER,
                qvri_summer_title,
                NEUTER,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.ORANGE,
                qvri_orange_title,
                MALE,
                false, "")
        createRepresentation(LocalizedNounStringResourceCollection.STRAWBERRY,
                qvri_strawberry_title,
                FEMALE,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.APPLE,
                qvri_apple_title,
                NEUTER,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.PEAR,
                qvri_pear_title,
                FEMALE,
                false)
        createRepresentation(LocalizedNounStringResourceCollection.CHERRY,
                qvri_cherry_title,
                FEMALE,
                false, "ю")
        createRepresentation(LocalizedNounStringResourceCollection.RASPBERRY,
                qvri_raspberry_title,
                FEMALE, false)
        createRepresentation(LocalizedNounStringResourceCollection.PINEAPPLE,
                qvri_pineapple_title,
                MALE,
                false,
                "")
        createRepresentation(LocalizedNounStringResourceCollection.BLACKBERRY,
                qvri_blackberry_title,
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

