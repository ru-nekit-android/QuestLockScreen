package ru.nekit.android.qls.domain.quest.creator

import ru.nekit.android.qls.domain.model.ChoiceQuestTrainingProgramRule
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.domain.model.resources.ResourceGroupCollection.Companion.getGroup
import ru.nekit.android.qls.domain.quest.creator.common.IQuestCreator
import ru.nekit.android.qls.domain.repository.IQuestResourceRepository
import ru.nekit.android.qls.shared.model.QuestionType
import ru.nekit.android.utils.MathUtils.randByListLength
import ru.nekit.android.utils.MathUtils.randItem

class ChoiceQuestCreator(
        private val rule: ChoiceQuestTrainingProgramRule,
        private val questResourceRepository: IQuestResourceRepository
) : IQuestCreator<NumberSummandQuest> {

    override fun create(questionType: QuestionType) =
            NumberSummandQuest().also { quest ->
                questResourceRepository.getVisualResourceItemIdsByGroup(
                        getGroup(randItem(rule.types))
                ).shuffled().let {
                    quest.leftNode = it.toIntArray()
                    quest.unknownMemberIndex = randByListLength(it)
                }
            }
}
