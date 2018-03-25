package ru.nekit.android.qls.quest.resources.representation

import android.support.annotation.StringRes
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.resources.ResourceGroupCollection
import ru.nekit.android.qls.domain.model.resources.ResourceGroupCollection.*
import ru.nekit.android.qls.quest.resources.representation.common.ResourceRepresentationProvider
import ru.nekit.android.qls.quest.resources.representation.common.StringIdRepresentation

object VisualResourceGroupRepresentationProvider :
        ResourceRepresentationProvider<ResourceGroupCollection,
                StringIdRepresentation>() {

    init {
        registerRepresentation(CHOICE, R.string.qvrg_choice_title)
        registerRepresentation(FRUIT, R.string.qvrg_fruit_title)
        registerRepresentation(MATH_OPERATOR, R.string.qvrg_math_operator_title)
        registerRepresentation(BERRY, R.string.qvrg_berry_title)
        registerRepresentation(POMUM, R.string.qvrg_pomum_title)
        registerRepresentation(SEASONS, R.string.qvrg_seasons_title)
        registerRepresentation(COINS, R.string.qvrg_coins_title)
        registerRepresentation(SEX, R.string.qvrg_sex_title)
        registerRepresentation(GIRL, R.string.qvrg_girl_title)
        registerRepresentation(BOY, R.string.qvrg_boy_title)
        registerRepresentation(CHILDREN_TOY, R.string.qvrg_children_toy_title)
    }

    private fun registerRepresentation(key: ResourceGroupCollection,
                                       @StringRes stringResourceId: Int) {
        createRepresentation(key, StringIdRepresentation(stringResourceId))
    }

}

fun ResourceGroupCollection.getRepresentation() = VisualResourceGroupRepresentationProvider.getRepresentation(this)