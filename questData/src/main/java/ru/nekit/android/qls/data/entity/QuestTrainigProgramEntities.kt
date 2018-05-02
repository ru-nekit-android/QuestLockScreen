package ru.nekit.android.qls.data.entity

import io.objectbox.annotation.BaseEntity
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.nekit.android.qls.shared.model.Complexity
import ru.nekit.android.qls.shared.model.PupilSex
import ru.nekit.android.qls.shared.model.QuestType
import ru.nekit.android.qls.shared.model.QuestionType

@Entity
data class QuestTrainingProgramEntity(
        @Id
        var id: Long = 0,
        val name: String,
        val version: Float,
        val description: String,
        @Convert(converter = PupilSexConverter::class, dbType = String::class)
        val sex: PupilSex,
        @Convert(converter = ComplexityConverter::class, dbType = String::class)
        val complexity: Complexity)

@Entity
data class QuestTrainingProgramLevelEntity(
        @Id
        var id: Long = 0,
        val qtpId: Long,
        val name: String,
        val description: String,
        val index: Int,
        val pointsMultiplier: Double,
        val pointsWeight: Int,
        val delayedPlay: Int
)

@Entity
data class QuestTrainingProgramPriorityRuleEntity(
        @Id
        var id: Long = 0,
        val qtpId: Long = 0,
        @Convert(converter = QuestTypeConverter::class, dbType = String::class)
        val questType: QuestType? = null,
        @Convert(converter = QuestionTypeListConverter::class, dbType = String::class)
        val questionTypes: List<QuestionType>? = null,
        val startPriority: Double = 0.0,
        val wrongAnswerPriority: Double = 0.0
)

@BaseEntity
open class BaseQuestTrainingProgramRuleEntity {
    @Id
    var id: Long = 0
    var qtpId: Long = 0
    var levelId: Long = 0
    @Convert(converter = QuestTypeConverter::class, dbType = String::class)
    lateinit var questType: QuestType
    @Convert(converter = QuestionTypeListConverter::class, dbType = String::class)
    lateinit var questionTypes: List<QuestionType>
    var reward: Int = 0
    var delayedPlay: Int = 0
    var enabled: Boolean = true
}

@Entity
class MemberCountQuestTrainingProgramRuleEntity(
        val memberCount: Int = 0
) :
        BaseQuestTrainingProgramRuleEntity()

@Entity
class TimeQuestTrainingProgramRuleEntity(
        val memberCount: Int = 0,
        val accuracy: Int = 0
) :
        BaseQuestTrainingProgramRuleEntity()

@Entity
class ChoiceQuestTrainingProgramRuleEntity(
        @Convert(converter = IntListConverter::class, dbType = String::class)
        var types: List<Int>? = null
) :
        BaseQuestTrainingProgramRuleEntity()

@Entity
class FruitArithmeticQuestTrainingProgramRuleEntity(
        val memberCount: Int = 0,
        val answerVariants: Int = 0,
        @Convert(converter = IntListListConverter::class, dbType = String::class)
        val memberMinAndMaxValues: List<List<Int>>? = null
) :
        BaseQuestTrainingProgramRuleEntity()

@Entity
class SimpleTrainingProgramRuleEntity :
        BaseQuestTrainingProgramRuleEntity()