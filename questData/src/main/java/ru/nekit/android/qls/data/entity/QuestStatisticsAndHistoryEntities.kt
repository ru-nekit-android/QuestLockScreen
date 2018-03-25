package ru.nekit.android.qls.data.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.LockScreenStartType
import ru.nekit.android.qls.domain.model.Reward
import ru.nekit.android.qls.shared.model.QuestType
import ru.nekit.android.qls.shared.model.QuestionType

@Entity
data class QuestStatisticsReportEntity(
        @Id
        var id: Long = 0,
        var pupilId: Long = 0,
        @Convert(converter = QuestTypeConverter::class, dbType = String::class)
        var questType: QuestType? = null,
        @Convert(converter = QuestionTypeConverter::class, dbType = String::class)
        var questionType: QuestionType? = null,
        var rightAnswerCount: Int = 0,
        var rightAnswerSeriesCount: Int = 0,
        var rightAnswerSeriesCounter: Int = 0,
        var wrongAnswerCount: Int = 0,
        var wrongAnswerSeriesCounter: Int = 0,
        var bestAnswerTime: Long = 0,
        var worseAnswerTime: Long = 0,
        var rightAnswerSummandTime: Long = 0
)

@Entity
data class QuestHistoryEntity(
        @Id
        var id: Long = 0,
        var pupilId: Long = 0,
        //var questId: Long = 0,
        @Convert(converter = QuestTypeConverter::class, dbType = String::class)
        val questType: QuestType? = null,
        @Convert(converter = QuestionTypeConverter::class, dbType = String::class)
        var questionType: QuestionType? = null,
        var score: Int = 0,
        @Convert(converter = LockScreenStartTypeConverter::class, dbType = String::class)
        var lockScreenStartType: LockScreenStartType? = null,
        @Convert(converter = AnswerTypeConverter::class, dbType = String::class)
        var answerType: AnswerType? = null,
        @Convert(converter = RewardListConverter::class, dbType = String::class)
        var rewards: List<Reward>? = null,
        val sessionTime: Long = 0,
        val recordTypes: Int = 0,
        val levelUp: Boolean = false,
        val timeStamp: Long = 0
)


fun QuestType.asParameter(): String = QuestTypeConverter().convertToDatabaseValue(this)
fun QuestionType.asParameter(): String = QuestionTypeConverter().convertToDatabaseValue(this)
fun AnswerType.asParameter(): String = AnswerTypeConverter().convertToDatabaseValue(this)
fun Reward.asParameter(): String = RewardListConverter().createPropertyConverter().convertToDatabaseValue(this)
fun LockScreenStartType.asParameter(): String = LockScreenStartTypeConverter().convertToDatabaseValue(this)