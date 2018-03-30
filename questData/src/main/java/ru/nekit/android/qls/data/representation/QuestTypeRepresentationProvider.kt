package ru.nekit.android.qls.data.representation

import ru.nekit.android.qls.domain.model.QuestAndQuestionType
import ru.nekit.android.qls.shared.model.QuestType
import ru.nekit.android.qls.shared.model.QuestType.*
import ru.nekit.android.qls.shared.model.QuestionType
import ru.nekit.android.questData.R.string.*

object QuestTypeRepresentationProvider : StringIdRepresentationProvider<QuestType>() {

    init {
        createRepresentation(SIMPLE_EXAMPLE,
                quest_simple_example_title)

        createRepresentation(TRAFFIC_LIGHT,
                quest_traffic_light_title)

        createRepresentation(COINS,
                quest_coins_title)

        createRepresentation(METRICS,
                quest_metrics_title)

        createRepresentation(PERIMETER,
                quest_perimeter_title)

        createRepresentation(FRUIT_ARITHMETIC,
                quest_fruit_arithmetic_title)

        createRepresentation(TEXT_CAMOUFLAGE, quest_text_camouflage_title)

        createRepresentation(TIME,
                quest_time_title)

        createRepresentation(CURRENT_TIME,
                quest_current_time_title)

        createRepresentation(CURRENT_SEASON,
                quest_current_season_title)

        createRepresentation(CHOICE,
                quest_choice_title)

        createRepresentation(MISMATCH,
                quest_mismatch_title)

        createRepresentation(COLORS,
                quest_colors_title)

        createRepresentation(DIRECTION,
                quest_direction_title)
    }

}

object QuestionTypeRepresentationProvider : StringIdRepresentationProvider<QuestionType>() {

    init {
        createRepresentation(QuestionType.SOLUTION,
                question_type_solution_title)

        createRepresentation(QuestionType.COMPARISON,
                question_type_comparison_title)

        createRepresentation(QuestionType.UNKNOWN_MEMBER,
                question_type_unknown_member_title)

        createRepresentation(QuestionType.UNKNOWN_OPERATION,
                question_type_unknown_operator_title)

    }

}

fun QuestType.getRepresentation() = QuestTypeRepresentationProvider.getRepresentation(this)
fun QuestionType.getRepresentation() = QuestionTypeRepresentationProvider.getRepresentation(this)
fun QuestAndQuestionType.getStringRepresentation(): String =
        "${questType.getRepresentation()} ${questionType.getRepresentation()}"