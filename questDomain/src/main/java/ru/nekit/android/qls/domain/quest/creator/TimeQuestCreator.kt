package ru.nekit.android.qls.domain.quest.creator

import ru.nekit.android.qls.domain.model.TimeQuestTrainingProgramRule
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.domain.quest.creator.common.IQuestCreator
import ru.nekit.android.qls.domain.quest.generator.NumberSummandQuestGenerator
import ru.nekit.android.qls.shared.model.QuestionType

open class TimeQuestCreator(private val rule: TimeQuestTrainingProgramRule) :
        IQuestCreator<NumberSummandQuest> {

    //filter accuracy
    protected open val accuracy: Int
        get() = Math.max(rule.accuracy, VALUE_DEFAULT_ACCURACY)

    override fun create(questionType: QuestionType): NumberSummandQuest =
            NumberSummandQuestGenerator(questionType).also { generator ->
                val accuracy = this.accuracy
                val memberCount = rule.memberCount
                generator.setMemberCounts(memberCount, 0)
                val minAndMaxValues: ArrayList<ArrayList<Int>> = ArrayList()
                for (i in 0..1) {
                    minAndMaxValues.add(ArrayList())
                    for (j in 0 until memberCount)
                    //i = 0..1 -> 0 or VALUE_MAX_TIME
                        minAndMaxValues[i].add(i * VALUE_MAX_TIME)
                }
                generator.leftNodeEachItemTransformationFunction { value ->
                    Math.max(accuracy, value - value % accuracy)
                }
                generator.setMembersMinAndMaxValues(minAndMaxValues)
                generator.setFlags(NumberSummandQuestGenerator.Flag.ONLY_POSITIVE_SUMMANDS)
            }.generate()


    companion object {

        private const val VALUE_DEFAULT_ACCURACY = 5
        private const val VALUE_MAX_TIME = 12 * 60

    }
}