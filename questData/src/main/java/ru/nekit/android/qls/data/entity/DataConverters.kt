package ru.nekit.android.qls.data.entity

import io.objectbox.converter.PropertyConverter
import ru.nekit.android.data.support.PropertyListConverter
import ru.nekit.android.data.support.PropertyListListConverter
import ru.nekit.android.qls.domain.model.*
import ru.nekit.android.qls.shared.model.Complexity
import ru.nekit.android.qls.shared.model.PupilSex
import ru.nekit.android.qls.shared.model.QuestType
import ru.nekit.android.qls.shared.model.QuestionType

open class PupilSexConverter : PropertyConverter<PupilSex?, String?> {
    override fun convertToEntityProperty(databaseValue: String?): PupilSex? {
        return if (databaseValue == null) null else PupilSex.valueOf(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: PupilSex?): String? {
        return entityProperty?.name
    }
}

open class ComplexityConverter : PropertyConverter<Complexity?, String?> {
    override fun convertToEntityProperty(databaseValue: String?): Complexity? {
        return if (databaseValue == null) null else Complexity.valueOf(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: Complexity?): String? {
        return entityProperty?.name
    }
}

open class QuestTypeConverter : PropertyConverter<QuestType, String> {
    override fun convertToEntityProperty(databaseValue: String): QuestType {
        return QuestType.valueOf(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: QuestType): String {
        return entityProperty.name
    }
}

open class AnswerTypeConverter : PropertyConverter<AnswerType, String> {
    override fun convertToEntityProperty(databaseValue: String): AnswerType {
        return AnswerType.valueOf(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: AnswerType): String {
        return entityProperty.name
    }
}

open class LockScreenStartTypeConverter : PropertyConverter<LockScreenStartType, String> {
    override fun convertToEntityProperty(databaseValue: String): LockScreenStartType {
        return LockScreenStartType.valueOf(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: LockScreenStartType): String {
        return entityProperty.name
    }
}

open class QuestionTypeConverter : PropertyConverter<QuestionType, String> {
    override fun convertToEntityProperty(databaseValue: String): QuestionType {
        return QuestionType.valueOf(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: QuestionType): String {
        return entityProperty.name
    }
}

class QuestionTypeListConverter : PropertyListConverter<QuestionType>() {

    override fun createPropertyConverter(): PropertyConverter<QuestionType, String> = QuestionTypeConverter()

}

class RewardListConverter : PropertyListConverter<Reward>() {

    override fun createPropertyConverter(): PropertyConverter<Reward, String> = object : PropertyConverter<Reward, String> {

        override fun convertToDatabaseValue(entityProperty: Reward?): String = RewardDataConverter.to(entityProperty!!)

        override fun convertToEntityProperty(databaseValue: String?): Reward = RewardDataConverter.from(databaseValue!!)

    }
}

object RewardDataConverter {

    const val DELIMITER: String = ":"

    fun MedalType.name(): String = javaClass.simpleName
    fun IRewardVariant.name(): String = javaClass.simpleName

    fun to(reward: Reward): String = reward.name()

    fun from(value: String): Reward {
        val parts = value.split(DELIMITER)
        val reward = getByName(parts[0])
        if (parts.size > 1)
            reward.variant = when (reward) {
                is Reward.UnlockKey -> ReachVariant.Values.get().first { it.name() == parts[1] }
                is Reward.Achievement ->
                    AchievementVariant.Values.get().first { it.name() == parts[1] }
                else -> null
            }
        if (reward is Reward.Medal) {
            reward.medalType = MedalType.Values.get().first { it.name() == parts[1] }
        }
        return reward
    }

    private fun getByName(name: String): Reward =
            Reward.Values.get().first {
                it.name() == name
            }

    fun Reward.name(): String =
            ArrayList<String>().apply {
                val reward = this@name
                add(reward.javaClass.simpleName)
                variant?.let { add(it.name()) }
                (variant as? IRewardVariantWithQuestAndQuestionType)?.let {
                    it.let {
                        add(it.name())
                    }
                }
                if (reward is Reward.Medal && reward.medalType != null)
                    add(reward.medalType!!.name())
            }.joinToString(RewardDataConverter.DELIMITER)

}

class IntListConverter : PropertyListConverter<Int>() {

    override fun createPropertyConverter(): PropertyConverter<Int, String> = object : PropertyConverter<Int, String> {

        override fun convertToEntityProperty(databaseValue: String): Int {
            return databaseValue.toInt()
        }

        override fun convertToDatabaseValue(entityProperty: Int): String {
            return entityProperty.toString()
        }

    }
}

fun QuestAndQuestionType.name(): String = "${questType.name}:${questionType.name}"

class IntListListConverter : PropertyListListConverter<Int>() {

    override fun createPropertyConverter(): PropertyConverter<Int, String> = object : PropertyConverter<Int, String> {

        override fun convertToEntityProperty(databaseValue: String): Int {
            return databaseValue.toInt()
        }

        override fun convertToDatabaseValue(entityProperty: Int): String {
            return entityProperty.toString()
        }

    }
}