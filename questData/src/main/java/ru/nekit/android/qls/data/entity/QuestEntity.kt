package ru.nekit.android.qls.data.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.nekit.android.qls.shared.model.QuestType
import ru.nekit.android.qls.shared.model.QuestionType

@Entity
data class QuestEntity(@Id
                       var id: Long = 0,
                       var pupilId: Long = 0,
                       @Convert(converter = QuestTypeConverter::class, dbType = String::class)
                       val questType: QuestType? = null,
                       @Convert(converter = QuestionTypeConverter::class, dbType = String::class)
                       val questionType: QuestionType? = null,
                       val questString: String
)