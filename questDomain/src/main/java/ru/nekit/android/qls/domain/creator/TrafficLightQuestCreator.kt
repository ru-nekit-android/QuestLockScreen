package ru.nekit.android.qls.domain.creator

import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.domain.model.resources.TrafficLightResourceCollection
import ru.nekit.android.qls.domain.quest.generator.NumberSummandQuestGenerator
import ru.nekit.android.qls.shared.model.QuestionType

class TrafficLightQuestCreator : IQuestCreator<NumberSummandQuest> {

    override fun create(questionType: QuestionType): NumberSummandQuest =
            NumberSummandQuestGenerator(questionType).also { generator ->
                generator.setMemberCounts(1, 0)
                generator.setAvailableMemberValues(TrafficLightResourceCollection.values())
            }.generate()
}
