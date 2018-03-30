package ru.nekit.android.qls.domain.creator

import ru.nekit.android.qls.domain.model.ChoiceQuestTrainingProgramRule
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.domain.model.resources.ResourceGroupCollection
import ru.nekit.android.qls.domain.model.resources.common.IVisualResourceHolder
import ru.nekit.android.qls.domain.repository.IQuestResourceRepository
import ru.nekit.android.qls.shared.model.QuestionType
import ru.nekit.android.utils.MathUtils.randByListLength
import ru.nekit.android.utils.MathUtils.randItem

class MismatchQuestCreator(private val rule: ChoiceQuestTrainingProgramRule,
                           private val questResourceRepository: IQuestResourceRepository) :
        IQuestCreator<NumberSummandQuest> {

    override fun create(questionType: QuestionType): NumberSummandQuest =
            NumberSummandQuest().apply {
                val targetGroup = ResourceGroupCollection.getGroup(randItem(rule.types))
                questResourceRepository.getVisualResourceItemIdsByGroup(targetGroup).shuffled().toMutableList().apply {
                    var mismatchGroup: ResourceGroupCollection? = null
                    val questVisualResourceGroups = ResourceGroupCollection.values().toList().shuffled()
                    var mismatchItems: List<IVisualResourceHolder>?
                    do {
                        for (group in questVisualResourceGroups) {
                            if (!group.hasParent(targetGroup) && !targetGroup.hasParent(group)) {
                                mismatchGroup = group
                                break
                            }
                        }
                        mismatchItems = questResourceRepository.getVisualResourceItemsByGroup(mismatchGroup!!)
                    } while (mismatchItems == null || mismatchItems.isEmpty())
                    unknownMemberIndex = randByListLength<Int>(this)
                    add(unknownMemberIndex, questResourceRepository.getVisualResourceItemId(
                            mismatchItems[randByListLength(mismatchItems)]))
                    removeAt(unknownMemberIndex + 1)
                    leftNode = toIntArray()
                }

            }
}