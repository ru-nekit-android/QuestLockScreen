package ru.nekit.android.qls.data.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
/*import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required*/
import ru.nekit.android.domain.shared.model.ktQuestType
import ru.nekit.android.domain.shared.model.ktQuestionType


/*@RealmClass
open class ktQuestStatisticsReportEntity : RealmObject {

    companion object KeyFormatter {

        private val DELIMITER: String = ":"
        val PUPIL_UUID: String = "pupilUuid"
        val KEY: String = "key"

        fun format(pupilUuid: String, questType: ktQuestType, questionType: ktQuestionType)
                = "$pupilUuid${DELIMITER}${questType.name}${DELIMITER}${questionType.name}"

        fun getQuestType(key: String): ktQuestType =
                ktQuestType.valueOf(key.split(DELIMITER)[1])


        fun getQuestionType(key: String): ktQuestionType =
                ktQuestionType.valueOf(key.split(DELIMITER)[2])

    }

    @Required
    @PrimaryKey
    private var key: String? = null
    @Required
    var pupilUuid: String? = null
    var rightAnswerCount: Int? = 0
    var rightAnswerSeriesCounter: Int? = 0
    var wrongAnswerCount: Int? = 0
    var bestAnswerTime: Long? = Long.MAX_VALUE
    var worseAnswerTime: Long? = 0
    var rightAnswerSummandTime: Long? = 0

    val questType: ktQuestType
        get() = getQuestType(key!!)

    val questionType: ktQuestionType
        get() = getQuestionType(key!!)

    constructor(
            pupilUuid: String,
            questType: ktQuestType,
            questionType: ktQuestionType,
            rightAnswerCount: Int,
            rightAnswerSeriesCounter: Int,
            wrongAnswerCount: Int,
            bestAnswerTime: Long,
            worseAnswerTime: Long,
            rightAnswerSummandTime: Long
    ) : super() {
        key = format(pupilUuid, questType, questionType)
        this.pupilUuid = pupilUuid
        this.rightAnswerCount = rightAnswerCount
        this.rightAnswerSeriesCounter = rightAnswerSeriesCounter
        this.wrongAnswerCount = wrongAnswerCount
        this.bestAnswerTime = bestAnswerTime
        this.worseAnswerTime = worseAnswerTime
        this.rightAnswerSummandTime = rightAnswerSummandTime
    }

    constructor() : super()

}*/

@Entity
data class ktObjectBoxQuestStatisticsReportEntity(
        @Id
        var id: Long = 0,
        var pupilUuid: String? = null,
        @Convert(converter = QuestTypeConverter::class, dbType = String::class)
        var questType: ktQuestType? = null,
        @Convert(converter = QuestionTypeConverter::class, dbType = String::class)
        var questionType: ktQuestionType? = null,
        var rightAnswerCount: Int = 0,
        var rightAnswerSeriesCounter: Int = 0,
        var wrongAnswerCount: Int = 0,
        var bestAnswerTime: Long = 0,
        var worseAnswerTime: Long = 0,
        var rightAnswerSummandTime: Long = 0
)

open class QuestTypeConverter : PropertyConverter<ktQuestType, String> {
    override fun convertToEntityProperty(databaseValue: String): ktQuestType {
        return ktQuestType.valueOf(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: ktQuestType): String {
        return entityProperty.name
    }
}

open class QuestionTypeConverter : PropertyConverter<ktQuestionType, String> {
    override fun convertToEntityProperty(databaseValue: String): ktQuestionType {
        return ktQuestionType.valueOf(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: ktQuestionType): String {
        return entityProperty.name
    }
}

fun ktQuestType.asParameter(): String = QuestTypeConverter().convertToDatabaseValue(this)
fun ktQuestionType.asParameter(): String = QuestionTypeConverter().convertToDatabaseValue(this)