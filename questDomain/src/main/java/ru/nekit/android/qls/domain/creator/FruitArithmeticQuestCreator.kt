package ru.nekit.android.qls.domain.creator

import ru.nekit.android.qls.domain.model.FruitArithmeticQuestTrainingProgramRule
import ru.nekit.android.qls.domain.model.quest.FruitArithmeticQuest
import ru.nekit.android.qls.domain.model.resources.ResourceGroupCollection.*
import ru.nekit.android.qls.domain.model.resources.SimpleVisualResourceCollection.*
import ru.nekit.android.qls.domain.model.resources.common.IGroupWeightComparisonQuest
import ru.nekit.android.qls.domain.model.resources.common.IVisualResourceHolder
import ru.nekit.android.qls.domain.quest.generator.NumberSummandQuestGenerator
import ru.nekit.android.qls.domain.quest.generator.NumberSummandQuestGenerator.Flag.*
import ru.nekit.android.qls.domain.repository.IQuestResourceRepository
import ru.nekit.android.qls.shared.model.QuestionType
import ru.nekit.android.qls.shared.model.QuestionType.COMPARISON
import ru.nekit.android.qls.shared.model.QuestionType.SOLUTION
import ru.nekit.android.utils.MathUtils

class FruitArithmeticQuestCreator(
        val rule: FruitArithmeticQuestTrainingProgramRule,
        private val questResourceRepository: IQuestResourceRepository
) : IQuestCreator<FruitArithmeticQuest> {

    override fun create(questionType: QuestionType): FruitArithmeticQuest {
        var memberCount = rule.memberCount
        var answerVariants = rule.answerVariants
        return NumberSummandQuestGenerator(questionType).also { generator ->
            when (questionType) {

                SOLUTION -> {
                    memberCount = Math.max(VALUE_DEFAULT_MEMBER_COUNT, memberCount)
                    answerVariants = Math.max(VALUE_DEFAULT_ANSWER_VARIANTS, answerVariants)
                    generator.setMembersMinAndMaxValues(rule.memberMinAndMaxValues)
                    generator.setFlags(
                            AVOID_NEGATIVE_ANSWER,
                            AVOID_NEGATIVE_ANSWER_WHILE_CALCULATION,
                            AVOID_ZERO_ANSWER,
                            AVOID_ZERO_ANSWER_WHILE_CALCULATION
                    )
                }

                COMPARISON -> {
                    memberCount = Math.max(VALUE_DEFAULT_MEMBER_COUNT_FOR_COMPARISON, memberCount)
                    val visualResourceItemList = questResourceRepository.getVisualResourceItemsByGroup(FRUIT)
                    answerVariants = Math.min(
                            Math.max(
                                    VALUE_DEFAULT_ANSWER_VARIANTS_FOR_COMPARISON,
                                    answerVariants
                            ),
                            visualResourceItemList.size)
                    generator.setAvailableMemberValues(
                            visualResourceItemList.shuffled().subList(0, answerVariants).map
                            {
                                questResourceRepository.getVisualResourceItemId(it)
                            }
                    )
                }
            }
            generator.setMemberCounts(memberCount, 0)
        }.let {
                    var i = 0
                    var length = 0
                    val fruitArithmeticQuest = FruitArithmeticQuest()
                    with(it.generate()) {
                        fruitArithmeticQuest.leftNode = leftNode
                        fruitArithmeticQuest.rightNode = rightNode
                        fruitArithmeticQuest.questionType = questionType
                        length = leftNode.size
                    }
                    when (questionType) {

                        SOLUTION -> {

                            val sourceVisualResourceItemList = questResourceRepository.getVisualResourceItemsByGroup(
                                    if (MathUtils.randBoolean())
                                        POMUM
                                    else
                                        BERRY).toMutableList()
                            val questVisualResourceItemList: MutableList<IVisualResourceHolder> = ArrayList()
                            while (i < length) {
                                sourceVisualResourceItemList[MathUtils.randUnsignedInt(sourceVisualResourceItemList.size - 1)].apply {
                                    questVisualResourceItemList.add(this)
                                    sourceVisualResourceItemList.remove(this)
                                }
                                i++
                            }
                            i = 0
                            while (i < length) {
                                val value = Math.abs(fruitArithmeticQuest.leftNode[i])
                                for (j in 0 until value)
                                    fruitArithmeticQuest.visualRepresentationList.add(questResourceRepository.getVisualResourceItemId(questVisualResourceItemList[i]))
                                if (i < length - 1)
                                    fruitArithmeticQuest.visualRepresentationList.add(questResourceRepository.getVisualResourceItemId(
                                            if (fruitArithmeticQuest.leftNode[i + 1] > 0) PLUS else MINUS))
                                i++
                            }
                            fruitArithmeticQuest.visualRepresentationList.add(questResourceRepository.getVisualResourceItemId(EQUAL))
                            val answer = fruitArithmeticQuest.answer
                            val availableVariantList = ArrayList<Int>()
                            var leftShift = MathUtils.randUnsignedInt(Math.min(
                                    answerVariants - 1, answer))
                            if (answer - leftShift <= 0) {
                                leftShift = 0
                            }
                            val rightShift = answerVariants - leftShift - 1
                            i = 0
                            while (i < leftShift) {
                                availableVariantList.add(answer - leftShift + i)
                                i++
                            }
                            availableVariantList.add(answer)
                            i = 1
                            while (i <= rightShift) {
                                availableVariantList.add(answer + i)
                                i++
                            }
                            fruitArithmeticQuest.availableAnswerVariants = availableVariantList

                        }

                        COMPARISON -> {
                            fruitArithmeticQuest.groupComparisonType = MathUtils.randInt(
                                    IGroupWeightComparisonQuest.MIN_GROUP_WEIGHT,
                                    IGroupWeightComparisonQuest.MAX_GROUP_WEIGHT
                            )
                            while (i < length) {
                                fruitArithmeticQuest.visualRepresentationList.add(
                                        questResourceRepository.getVisualResourceItemId(
                                                questResourceRepository.getVisualResourceItemById(
                                                        fruitArithmeticQuest.leftNode[i])))
                                i++
                            }
                        }

                    }
                    fruitArithmeticQuest
                }
    }

    companion object {

        private val VALUE_DEFAULT_ANSWER_VARIANTS = 4
        private val VALUE_DEFAULT_MEMBER_COUNT = 2

        private val VALUE_DEFAULT_ANSWER_VARIANTS_FOR_COMPARISON = 2
        private val VALUE_DEFAULT_MEMBER_COUNT_FOR_COMPARISON = 4
    }
}