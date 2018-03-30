package ru.nekit.android.qls.data.representation

import ru.nekit.android.qls.domain.model.AchievementVariant.Newbe
import ru.nekit.android.qls.domain.model.AchievementVariant.NewbeByQuestAndQuestType
import ru.nekit.android.qls.domain.model.MedalType
import ru.nekit.android.qls.domain.model.Reward
import ru.nekit.android.questData.R

fun Reward.getRepresentation() = StringIdRepresentation(
        (when (this) {
            is Reward.UnlockKey -> R.string.reward_unlock_key_title
            is Reward.Medal -> when (medalType) {
                MedalType.Gold -> R.string.reward_medal_gold_title
                MedalType.Silver -> R.string.reward_medal_silver_title
                MedalType.Bronze -> R.string.reward_medal_bronze_title
                null -> null
            }
            is Reward.Achievement -> {
                when (achievementVariant) {
                    Newbe -> R.string.reward_achievement_newbe_title
                    is NewbeByQuestAndQuestType -> R.string.reward_achievement_newbe_by_quest_and_question_type_title
                    null -> null
                }
            }
        }) ?: R.string.reward_default_title)