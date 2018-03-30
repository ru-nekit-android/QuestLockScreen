package ru.nekit.android.qls.domain.creator

import ru.nekit.android.qls.domain.model.MemberCountQuestTrainingRule
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.domain.model.resources.CoinVisualResourceCollection
import ru.nekit.android.qls.domain.quest.generator.NumberSummandQuestGenerator
import ru.nekit.android.qls.shared.model.QuestionType

class CoinsQuestCreator(val rule: MemberCountQuestTrainingRule) :
        IQuestCreator<NumberSummandQuest> {

    override fun create(questionType: QuestionType): NumberSummandQuest =
            NumberSummandQuestGenerator(questionType).also { generator ->
                generator.setMemberCounts(rule.memberCount, 0)
                generator.setAvailableMemberValues(CoinVisualResourceCollection.values())
                generator.setFlags(NumberSummandQuestGenerator.Flag.ONLY_POSITIVE_SUMMANDS)
            }.generate()

}