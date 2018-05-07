package ru.nekit.android.data.shared

import android.content.Context
import android.support.annotation.StringRes
import ru.nekit.android.R.string.*
import ru.nekit.android.qls.shared.model.QuestType
import ru.nekit.android.qls.shared.model.QuestType.*

object QuestTypeRepresentation {

    @StringRes
    private fun getSynonymResourceId(questType: QuestType): Int {
        return when (questType) {
            TRAFFIC_LIGHT -> quest_traffic_light_synonym
            COINS -> quest_coins_synonym
            FRUIT_ARITHMETIC -> quest_fruit_arithmetic_synonym
            TIME -> quest_time_synonym
            CURRENT_TIME -> quest_current_time_synonym
            CURRENT_SEASON -> quest_current_season_synonym
            CHOICE -> quest_choice_synonym
            MISMATCH -> quest_mismatch_synonym
            COLORS -> quest_colors_synonym
            DIRECTION -> quest_direction_synonym
            METRICS,
            PERIMETER,
            TEXT_CAMOUFLAGE,
            SIMPLE_EXAMPLE -> 0
        }
    }

    fun getByNameOrSynonym(context: Context, value: String): QuestType? {
        val localValue = value.toLowerCase()
        for (questType in values()) {
            var synonym: String? = null
            val synonymResourceId = getSynonymResourceId(questType)
            if (synonymResourceId != 0) {
                synonym = context.getString(synonymResourceId)
            }
            if (questType.name.toLowerCase() == localValue || synonym != null && synonym == localValue) {
                return questType
            }
        }
        return null
    }

}