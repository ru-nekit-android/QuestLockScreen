package ru.nekit.android.qls.domain.quest.creator

import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.domain.model.resources.ResourceGroupCollection.SEASONS
import ru.nekit.android.qls.domain.model.resources.SimpleVisualResourceCollection.*
import ru.nekit.android.qls.domain.quest.creator.common.IQuestCreator
import ru.nekit.android.qls.domain.repository.IQuestResourceRepository
import ru.nekit.android.qls.shared.model.QuestionType
import java.util.*

class CurrentSeasonQuestCreator(private val questResourceRepository: IQuestResourceRepository) :
        IQuestCreator<NumberSummandQuest> {

    override fun create(questionType: QuestionType): NumberSummandQuest =
            NumberSummandQuest().also { quest ->
                questResourceRepository.getVisualResourceItemIdsByGroup(SEASONS).shuffled().let {
                    quest.leftNode = it.toIntArray()
                    quest.unknownMemberIndex = it.indexOf(
                            questResourceRepository.getVisualResourceItemId(
                                    when (Calendar.getInstance().get(Calendar.MONTH)) {
                                        in 2..4 -> SPRING
                                        in 5..7 -> SUMMER
                                        in 8..10 -> FALL
                                        else -> WINTER
                                    }
                            )
                    )
                }
            }
}