package ru.nekit.android.qls.quest.resources.representation

import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.resources.ChildrenToysVisualResourceCollection
import ru.nekit.android.qls.domain.model.resources.ChildrenToysVisualResourceCollection.*
import ru.nekit.android.qls.domain.model.resources.LocalizedNounStringResourceCollection
import ru.nekit.android.qls.domain.model.resources.LocalizedNounStringResourceCollection.BOOTS
import ru.nekit.android.qls.quest.resources.representation.common.ChildrenToysRepresentation
import ru.nekit.android.qls.quest.resources.representation.common.ResourceRepresentationProvider
import ru.nekit.android.qls.quest.resources.struct.ColorfullVisualResourceStruct
import ru.nekit.android.qls.quest.resources.struct.ColorfullVisualResourceStruct.ColorType.*

object ChildrenToysResourceRepresentationProvider :
        ResourceRepresentationProvider<ChildrenToysVisualResourceCollection, ChildrenToysRepresentation>() {

    init {

        createRepresentation(CAR,
                listOf(
                        ColorfullVisualResourceStruct(R.drawable.qvri_car_background, PRIMARY),
                        ColorfullVisualResourceStruct(R.drawable.qvri_car_content, NONE),
                        ColorfullVisualResourceStruct(R.drawable.qvri_car_foreground, ColorfullVisualResourceStruct.ColorType.SECONDARY)
                ),
                R.drawable.qvri_car,
                LocalizedNounStringResourceCollection.CAR
        )

        createRepresentation(DOLL_BOOTS,
                listOf(
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_background, NONE),
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_boots, PRIMARY),
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_skirt, PRIMARY_INVERSE),
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_blouse, SECONDARY_INVERSE),
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_foreground, NONE)
                ),
                R.drawable.qvri_girl,
                BOOTS
        )

        createRepresentation(DOLL_SKIRT,
                listOf(
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_background, NONE),
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_boots, PRIMARY_INVERSE),
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_skirt, PRIMARY),
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_blouse, SECONDARY_INVERSE),
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_foreground, NONE)
                ),
                R.drawable.qvri_girl,
                LocalizedNounStringResourceCollection.DOLL_SKIRT
        )

        createRepresentation(DOLL_BLOUSE,
                listOf(
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_background, NONE),
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_boots, PRIMARY_INVERSE),
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_skirt, SECONDARY_INVERSE),
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_blouse, PRIMARY),
                        ColorfullVisualResourceStruct(R.drawable.qvri_doll_foreground, NONE)
                ),
                R.drawable.qvri_girl,
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