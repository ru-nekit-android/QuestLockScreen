package ru.nekit.android.qls.domain.model

import ru.nekit.android.qls.shared.model.Complexity
import ru.nekit.android.qls.shared.model.PupilSex
import ru.nekit.android.qls.shared.model.QuestType
import ru.nekit.android.qls.shared.model.QuestionType

//No one nullable items!!!

data class QuestTrainingProgram(
        val name: String,
        val version: Float,
        val description: String,
        val sex: PupilSex,
        val complexity: Complexity)

data class QuestTrainingProgramLevel(
        val name: String,
        val description: String,
        val index: Int,
        val pointsMultiplier: Double,
        val pointsWeight: Int,
        val delayedPlay: Int) {

    override fun toString(): String {
        return (index + 1).toString()
    }

}

data class QuestTrainingProgramRulePriority(
        val questType: QuestType,
        val questionTypes: List<QuestionType>,
        val startPriority: Double,
        val wrongAnswerPriority: Double)

open class QuestTrainingProgramRule {
    lateinit var questType: QuestType
    lateinit var questionTypes: List<QuestionType>
    var reward: Int = 0
    var delayedPlay: Int = 0
}

class MemberCountQuestTrainingRule(
        val memberCount: Int
) :
        QuestTrainingProgramRule()

class TimeQuestTrainingProgramRule(
        val memberCount: Int,
        val accuracy: Int
) :
        QuestTrainingProgramRule()

class ChoiceQuestTrainingProgramRule(
        var types: List<Int>
) :
        QuestTrainingProgramRule()

class FruitArithmeticQuestTrainingProgramRule(
        var memberCount: Int,
        var answerVariants: Int,
        var memberMinAndMaxValues: List<List<Int>>
) :
        QuestTrainingProgramRule()

class SimpleTrainingProgramRule :
        QuestTrainingProgramRule()