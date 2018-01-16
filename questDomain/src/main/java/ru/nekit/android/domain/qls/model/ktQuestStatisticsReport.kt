package ru.nekit.android.domain.qls.model

import ru.nekit.android.domain.shared.model.ktQuestType
import ru.nekit.android.domain.shared.model.ktQuestionType

data class ktQuestStatisticsReport(val pupilUuid: String,
                                   val questType: ktQuestType,
                                   val questionType: ktQuestionType,
                                   var rightAnswerCount: Int,
                                   var rightAnswerSeriesCounter: Int,
                                   var wrongAnswerCount: Int,
                                   var bestAnswerTime: Long,
                                   var worseAnswerTime: Long,
                                   var rightAnswerSummandTime: Long
)