package ru.nekit.android.data.shared

import android.content.Context
import android.support.annotation.StringRes
import ru.nekit.android.R
import ru.nekit.android.qls.shared.model.QuestType

object ktQuestTypeSupport {

    @StringRes
    private fun getSynonymResourceId(questType: QuestType): Int {
        return when (questType) {
            QuestType.TRAFFIC_LIGHT -> R.string.quest_traffic_light_synonym
            QuestType.COINS -> R.string.quest_coins_synonym
            QuestType.FRUIT_ARITHMETIC -> R.string.quest_fruit_arithmetic_synonym
            QuestType.TIME -> R.string.quest_time_synonym
            QuestType.CURRENT_TIME -> R.string.quest_current_time_synonym
            QuestType.CURRENT_SEASON -> R.string.quest_current_season_synonym
            QuestType.CHOICE -> R.string.quest_choice_synonym
            QuestType.MISMATCH -> R.string.quest_mismatch_synonym
            QuestType.COLORS -> R.string.quest_colors_synonym
            QuestType.DIRECTION -> R.string.quest_direction_synonym
            QuestType.METRICS,
            QuestType.PERIMETER,
            QuestType.TEXT_CAMOUFLAGE,
            QuestType.SIMPLE_EXAMPLE -> 0
        }
    }

    fun getByNameOrSynonym(context: Context, value: String): QuestType? {
        val localValue = value.toLowerCase()
        for (questType in QuestType.values()) {
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