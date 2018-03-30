package ru.nekit.android.qls.domain.creator

import ru.nekit.android.qls.domain.model.MemberCountQuestTrainingRule
import ru.nekit.android.qls.domain.model.quest.VisualRepresentationalNumberSummandQuest
import ru.nekit.android.qls.domain.model.resources.ColorResourceCollection
import ru.nekit.android.qls.domain.model.resources.ResourceGroupCollection.CHILDREN_TOY
import ru.nekit.android.qls.domain.repository.IQuestResourceRepository
import ru.nekit.android.qls.shared.model.QuestionType
import ru.nekit.android.utils.MathUtils.randItem
import ru.nekit.android.utils.MathUtils.randLength
import java.util.*

class ColoredVisualRepresentationQuestCreator(private val rule: MemberCountQuestTrainingRule,
                                              private val questResourceRepository: IQuestResourceRepository) :
        IQuestCreator<VisualRepresentationalNumberSummandQuest> {

    override fun create(questionType: QuestionType): VisualRepresentationalNumberSummandQuest {
        val allColors = ColorResourceCollection.values().toList()
        val memberCount = Math.min(Math.max(rule.memberCount, VALUE_DEFAULT_MEMBER_COUNT), allColors.size)
        return VisualRepresentationalNumberSummandQuest().also { quest ->
            quest.leftNode = IntArray(memberCount)
            quest.rightNode = IntArray(memberCount) { -1 }
            val visualRepresentationList = ArrayList<Int>()
            val targetColors = allColors.shuffled().subList(0, memberCount)
            var i = 0
            val questVisualResourceItem = randItem(questResourceRepository.getVisualResourceItemsByGroup(CHILDREN_TOY))
            while (i < memberCount) {
                quest.leftNode[i] = targetColors[i].id
                visualRepresentationList.add(questResourceRepository.getVisualResourceItemId(questVisualResourceItem))
                i++
            }
            quest.unknownMemberIndex = randLength(memberCount)
            val secondaryColors = allColors.shuffled().subList(0, memberCount).toMutableList()
            i = 0
            while (i < memberCount) {
                var colorResourceItem: ColorResourceCollection
                do {
                    colorResourceItem = randItem(secondaryColors)
                    if (i == memberCount - 1 && quest.leftNode[i] == colorResourceItem.id) {
                        //swap
                        val colorResourceItemId = colorResourceItem.id
                        colorResourceItem = ColorResourceCollection.getById(quest.rightNode[i - 1])
                        quest.rightNode[i - 1] = colorResourceItemId
                        break
                    }
                } while (quest.leftNode[i] == colorResourceItem.id)
                quest.rightNode[i] = colorResourceItem.id
                secondaryColors.remove(colorResourceItem)
                i++
            }
            quest.visualRepresentationList = visualRepresentationList
        }
    }

    companion object {

        private const val VALUE_DEFAULT_MEMBER_COUNT = 2
    }
}