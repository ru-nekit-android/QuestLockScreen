package ru.nekit.android.qls.quest.resources.representation

import android.support.annotation.StringRes
import ru.nekit.android.qls.R
import ru.nekit.android.qls.quest.resources.representation.common.StringListIdRepresentation
import ru.nekit.android.qls.quest.resources.representation.common.StringListIdRepresentationProvider
import ru.nekit.android.qls.shared.model.QuestType
import ru.nekit.android.qls.shared.model.QuestType.*


object QuestRepresentationProvider : StringListIdRepresentationProvider<QuestType>() {

    init {
        createRepresentation(SIMPLE_EXAMPLE,
                R.string.quest_simple_example_title)

        createRepresentation(TRAFFIC_LIGHT,
                R.string.quest_traffic_light_title)

        createRepresentation(COINS,
                R.string.quest_coins_title)

        createRepresentation(METRICS,
                R.string.quest_metrics_title)

        createRepresentation(PERIMETER,
                R.string.quest_perimeter_title)

        createRepresentation(FRUIT_ARITHMETIC,
                R.string.quest_fruit_arithmetic_title)

        createRepresentation(TEXT_CAMOUFLAGE, R.string.quest_text_camouflage_title)

        createRepresentation(TIME,
                R.string.quest_time_title)

        createRepresentation(CURRENT_TIME,
                R.string.quest_current_time_title)

        createRepresentation(CURRENT_SEASON,
                R.string.quest_current_season_title)

        createRepresentation(CHOICE,
                R.string.quest_choice_title)

        createRepresentation(MISMATCH,
                R.string.quest_mismatch_title)

        createRepresentation(COLORS,
                R.string.quest_colors_title)

        createRepresentation(DIRECTION,
                R.string.quest_direction_title)
    }

    private fun createRepresentation(key: QuestType, stringIdList: List<Int>) {
        createRepresentation(key, StringListIdRepresentation(stringIdList))
    }

    private fun createRepresentation(key: QuestType, @StringRes stringId: Int) {
        createRepresentation(key, StringListIdRepresentation(listOf(stringId)))
    }

}

fun QuestType.getRepresentation() = QuestRepresentationProvider.getRepresentation(this)
