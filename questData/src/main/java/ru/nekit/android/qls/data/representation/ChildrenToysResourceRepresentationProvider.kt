package ru.nekit.android.qls.data.representation

import ru.nekit.android.qls.data.representation.common.ColorfullVisualResourceStruct
import ru.nekit.android.qls.data.representation.common.ColorfullVisualResourceStruct.ColorType.*
import ru.nekit.android.qls.domain.model.resources.ChildrenToysVisualResourceCollection
import ru.nekit.android.qls.domain.model.resources.ChildrenToysVisualResourceCollection.*
import ru.nekit.android.qls.domain.model.resources.LocalizedNounStringResourceCollection
import ru.nekit.android.qls.domain.model.resources.LocalizedNounStringResourceCollection.BOOTS
import ru.nekit.android.questData.R.drawable.*

object ChildrenToysResourceRepresentationProvider :
        ResourceRepresentationProvider<ChildrenToysVisualResourceCollection, ChildrenToysRepresentation>() {

    init {

        createRepresentation(CAR,
                listOf(
                        ColorfullVisualResourceStruct(qvri_car_background, PRIMARY),
                        ColorfullVisualResourceStruct(qvri_car_content, NONE),
                        ColorfullVisualResourceStruct(qvri_car_foreground, SECONDARY)
                ),
                qvri_car,
                LocalizedNounStringResourceCollection.CAR
        )

        createRepresentation(DOLL_BOOTS,
                listOf(
                        ColorfullVisualResourceStruct(qvri_doll_background, NONE),
                        ColorfullVisualResourceStruct(qvri_doll_boots, PRIMARY),
                        ColorfullVisualResourceStruct(qvri_doll_skirt, PRIMARY_INVERSE),
                        ColorfullVisualResourceStruct(qvri_doll_blouse, SECONDARY_INVERSE),
                        ColorfullVisualResourceStruct(qvri_doll_foreground, NONE)
                ),
                qvri_girl,
                BOOTS
        )

        createRepresentation(DOLL_SKIRT,
                listOf(
                        ColorfullVisualResourceStruct(qvri_doll_background, NONE),
                        ColorfullVisualResourceStruct(qvri_doll_boots, PRIMARY_INVERSE),
                        ColorfullVisualResourceStruct(qvri_doll_skirt, PRIMARY),
                        ColorfullVisualResourceStruct(qvri_doll_blouse, SECONDARY_INVERSE),
                        ColorfullVisualResourceStruct(qvri_doll_foreground, NONE)
                ),
                qvri_girl,
                LocalizedNounStringResourceCollection.DOLL_SKIRT
        )

        createRepresentation(DOLL_BLOUSE,
                listOf(
                        ColorfullVisualResourceStruct(qvri_doll_background, NONE),
                        ColorfullVisualResourceStruct(qvri_doll_boots, PRIMARY_INVERSE),
                        ColorfullVisualResourceStruct(qvri_doll_skirt, SECONDARY_INVERSE),
                        ColorfullVisualResourceStruct(qvri_doll_blouse, PRIMARY),
                        ColorfullVisualResourceStruct(qvri_doll_foreground, NONE)
                ),
                qvri_girl,
                LocalizedNounStringResourceCollection.DOLL_BLOUSE
        )
    }

    private fun createRepresentation(key: ChildrenToysVisualResourceCollection,
                                     coloredVisualResourceList: List<ColorfullVisualResourceStruct>,
                                     drawableResourceId: Int,
                                     localizedStringResource: LocalizedNounStringResourceCollection) {
        createRepresentation(key, ChildrenToysRepresentation(coloredVisualResourceList,
                drawableResourceId, localizedStringResource))
    }
}

fun ChildrenToysVisualResourceCollection.getRepresentation() = ChildrenToysResourceRepresentationProvider.getRepresentation(this)