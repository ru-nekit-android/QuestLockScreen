package ru.nekit.android.qls.data.representation

import ru.nekit.android.qls.domain.model.resources.ResourceGroupCollection
import ru.nekit.android.qls.domain.model.resources.ResourceGroupCollection.*
import ru.nekit.android.questData.R.string.*

object VisualResourceGroupRepresentationProvider :
        StringIdRepresentationProvider<ResourceGroupCollection>() {

    init {
        createRepresentation(CHOICE, qvrg_choice_title)
        createRepresentation(FRUIT, qvrg_fruit_title)
        createRepresentation(MATH_OPERATOR, qvrg_math_operator_title)
        createRepresentation(BERRY, qvrg_berry_title)
        createRepresentation(POMUM, qvrg_pomum_title)
        createRepresentation(SEASONS, qvrg_seasons_title)
        createRepresentation(COINS, qvrg_coins_title)
        createRepresentation(SEX, qvrg_sex_title)
        createRepresentation(GIRL, qvrg_girl_title)
        createRepresentation(BOY, qvrg_boy_title)
        createRepresentation(CHILDREN_TOY, qvrg_children_toy_title)
    }

}

fun ResourceGroupCollection.getRepresentation() = VisualResourceGroupRepresentationProvider.getRepresentation(this)