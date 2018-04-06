package ru.nekit.android.qls.data.representation

import android.content.Context
import ru.nekit.android.qls.domain.model.AchievementVariant.Newbe
import ru.nekit.android.qls.domain.model.AchievementVariant.NewbeByQuestAndQuestType
import ru.nekit.android.qls.domain.model.MedalType
import ru.nekit.android.qls.domain.model.MedalType.*
import ru.nekit.android.qls.domain.model.ReachVariant
import ru.nekit.android.qls.domain.model.Reward
import ru.nekit.android.questData.R.drawable.*
import ru.nekit.android.questData.R.string.*

fun Reward.getRepresentation() = StringIdRepresentation(
        (when (this) {
            is Reward.UnlockKey -> reward_unlock_key_title
            is Reward.Medal -> medalType?.getRepresentation()?.stringResourceId
            is Reward.Achievement -> {
                when (achievementVariant) {
                    Newbe -> reward_achievement_newbe_title
                    is NewbeByQuestAndQuestType -> reward_achievement_newbe_by_quest_and_question_type_title
                    null -> null
                }
            }
        }) ?: reward_default_title)

fun MedalType.getRepresentation() = StringIdRepresentation(
        when (this) {
            Gold -> reward_medal_gold_title
            Silver -> reward_medal_silver_title
            Bronze -> reward_medal_bronze_title
        }
)

fun MedalType.getRepresentationContraction() = StringIdRepresentation(
        when (this) {
            Gold -> reward_medal_gold_contraction_title
            Silver -> reward_medal_silver_contraction_title
            Bronze -> reward_medal_bronze_contraction_title
        }
)

fun MedalType.getDrawableRepresentation() = DrawableRepresentation(
        when (this) {
            Gold -> gold_medal
            Silver -> silver_medal
            Bronze -> bronze_medal
        }
)

fun Reward.UnlockKey.getDrawableRepresentation() = DrawableRepresentation(reward_key)

fun Reward.getReachRuleRepresentation(context: Context, count: Int): String {
    return when (this) {
        is Reward.UnlockKey -> {
            when (variant) {
                is ReachVariant.RightSeries ->
                    String.format(context.getString(
                            reward_unlock_key_right_series_reach_rule_title),
                            count)
                is ReachVariant.Independence ->
                    String.format(context.getString(
                            reward_unlock_key_independence_reach_rule_title),
                            count)
                else -> context.getString(reward_default_reach_rule_title)
            }
        }
        else -> context.getString(reward_default_reach_rule_title)
    }
}