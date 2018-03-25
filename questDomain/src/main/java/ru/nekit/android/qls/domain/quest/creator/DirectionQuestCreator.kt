package ru.nekit.android.qls.domain.quest.creator

import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.domain.model.resources.DirectionResourceCollection
import ru.nekit.android.qls.domain.quest.creator.common.IQuestCreator
import ru.nekit.android.qls.domain.quest.generator.NumberSummandQuestGenerator
import ru.nekit.android.qls.shared.model.QuestionType

class DirectionQuestCreator : IQuestCreator<NumberSummandQuest> {

    override fun create(questionType: QuestionType): NumberSummandQuest =
            NumberSummandQuestGenerator(questionType).also { generator ->
                generator.setMemberCounts(1, 0)
                generator.setAvailableMemberValues(DirectionResourceCollection.values())
            }.generate()
}