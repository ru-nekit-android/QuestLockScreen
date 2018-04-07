package ru.nekit.android.qls.domain.model

import io.reactivex.Single
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.AnswerType.RIGHT
import ru.nekit.android.qls.domain.model.AnswerType.WRONG
import ru.nekit.android.qls.domain.model.LockScreenStartType.ON_NOTIFICATION_CLICK
import ru.nekit.android.qls.domain.model.ReachVariant.Independence
import ru.nekit.android.qls.domain.model.ReachVariant.RightSeries
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.qls.domain.useCases.GetCurrentQuestUseCase
import ru.nekit.android.qls.shared.model.Complexity
import ru.nekit.android.qls.shared.model.Complexity.*

interface IRewardVariant
interface IRewardVariantWithQuestAndQuestionType : IRewardVariant {
    var questAndQuestionType: QuestAndQuestionType?
}

interface IRewardLevelType : IRewardVariant {
    var levelIndex: Int
}

object DefaultRewardVariant : IRewardVariant

sealed class ReachVariant : IRewardVariant {
    object RightSeries : ReachVariant()
    object Independence : ReachVariant()

    object Values {
        fun get(): List<ReachVariant> = listOf(RightSeries, Independence)
    }
}

sealed class MedalType(val min: Int, val max: Int) {
    object Gold : MedalType(0, 1)
    object Silver : MedalType(2, 3)
    object Bronze : MedalType(5, 6)

    object Values {
        fun get(): List<MedalType> = listOf(Gold, Silver, Bronze)
    }
}

sealed class AchievementVariant(val count: Int) : IRewardVariant {

    object Newbe : AchievementVariant(5)
    data class NewbeByQuestAndQuestType(override var questAndQuestionType: QuestAndQuestionType? = null) :
            AchievementVariant(5), IRewardVariantWithQuestAndQuestionType
    //data class PlayedAllOnLevel()

    object Values {
        fun get(): List<AchievementVariant> = listOf(Newbe, NewbeByQuestAndQuestType())
    }
}

sealed class Reward {

    //it cam be reached only one time
    data class Achievement(var achievementVariant: AchievementVariant? = null) : Reward() {

        override var variant: IRewardVariant?
            get() = achievementVariant
            set(value) {
                achievementVariant = value as AchievementVariant
            }

        override fun getAmountForReaching(complexity: Complexity): Int =
                achievementVariant?.count ?: -1

        override fun getVariants(): List<IRewardVariant> = AchievementVariant.Values.get()

        override fun computeRemainingAmountForReaching(repository: IRepositoryHolder,
                                                       complexity: Complexity,
                                                       historyList: List<QuestHistory>): Single<Optional<Int>> =
                Single.just(Optional(null))

        override fun getReachedReward(repository: IRepositoryHolder,
                                      complexity: Complexity,
                                      historyList: List<QuestHistory>): Single<Optional<Reward>> =

                GetCurrentQuestUseCase().build().flatMap { quest ->
                    if (variant is IRewardVariantWithQuestAndQuestionType)
                        (variant as IRewardVariantWithQuestAndQuestionType).questAndQuestionType = quest.questAndQuestionType()
                    repository.getRewardRepository().getCount(this).flatMap { count ->
                        if (count == 1) Single.just(Optional(null)) else {
                            getActualAmount(repository,
                                    complexity,
                                    historyList).map { it.nonNullData }.map { actualAmount ->
                                Optional(if (actualAmount == getAmountForReaching(complexity))
                                    this else null)
                            }
                        }
                    }
                }

        override fun getActualAmount(repository: IRepositoryHolder,
                                     complexity: Complexity,
                                     historyList: List<QuestHistory>): Single<Optional<Int>> =
                Single.just(Optional(historyList.size))
    }

    //only one variant - default
    data class Medal(var medalType: MedalType? = null) : Reward() {

        override var variant: IRewardVariant?
            get() = null
        //empty setter
            set(value) {}

        override fun getVariants(): List<IRewardVariant> = listOf(DefaultRewardVariant)

        override fun getAmountForReaching(complexity: Complexity): Int = 10

        private fun getMedalTypeBy(allAmount: Int, rightAnswerAmount: Int, wrongAnswerAmount: Int): MedalType? =
                MedalType.Values.get().firstOrNull {
                    wrongAnswerAmount in it.min..it.max
                }

        override fun computeRemainingAmountForReaching(repository: IRepositoryHolder,
                                                       complexity: Complexity,
                                                       historyList: List<QuestHistory>): Single<Optional<Int>> =
                Single.just(Optional(null))

        override fun getReachedReward(repository: IRepositoryHolder,
                                      complexity: Complexity,
                                      historyList: List<QuestHistory>): Single<Optional<Reward>> =
                getActualAmount(repository,
                        complexity,
                        historyList).map { it.nonNullData }.map { actualAmount ->
                    var result: Medal? = null
                    if (actualAmount >= getAmountForReaching(complexity)) {
                        val wrongCount = historyList.count { it.answerType == WRONG }
                        getMedalTypeBy(historyList.size, actualAmount, wrongCount)?.let {
                            result = Medal(it)
                        }
                    }
                    Optional(result)
                }

        override fun getActualAmount(repository: IRepositoryHolder,
                                     complexity: Complexity,
                                     historyList: List<QuestHistory>): Single<Optional<Int>> =
                Single.just(Optional(historyList.count { it.answerType == RIGHT }))
    }

    data class UnlockKey(private var reachVariant: ReachVariant? = null) : Reward() {

        override var variant: IRewardVariant?
            get() = reachVariant
            set(value) {
                reachVariant = value as ReachVariant
            }

        override fun getVariants(): List<IRewardVariant> = ReachVariant.Values.get()

        override fun getReachedReward(repository: IRepositoryHolder,
                                      complexity: Complexity,
                                      historyList: List<QuestHistory>): Single<Optional<Reward>> =
                getActualAmount(repository,
                        complexity,
                        historyList).map { it.nonNullData }.map { actualAmount ->
                    Optional(if (actualAmount == getAmountForReaching(complexity))
                        this else null)
                }

        override fun getActualAmount(repository: IRepositoryHolder,
                                     complexity: Complexity,
                                     historyList: List<QuestHistory>): Single<Optional<Int>> {
            return when (variant) {
                RightSeries ->
                    Single.fromCallable {
                        val actualAmount = historyList.size
                        val seriesLength = Math.min(actualAmount,
                                getAmountForReaching(complexity))
                        Optional(historyList.subList(actualAmount - seriesLength, actualAmount)
                                .count { it.answerType == RIGHT })
                    }
                Independence -> {
                    val h = historyList
                    Single.fromCallable {
                        val count = historyList.count {
                            it.answerType == RIGHT &&
                                    it.lockScreenStartType == ON_NOTIFICATION_CLICK
                        }
                        val countByStartType = historyList.count {
                            it.lockScreenStartType == ON_NOTIFICATION_CLICK
                        }
                        Optional(if (countByStartType == count)
                            count
                        else
                            0)
                    }
                }
                else -> Single.just(Optional(null))
            }
        }

        override fun getAmountForReaching(complexity: Complexity): Int =
                when (variant) {
                    RightSeries ->
                        when (complexity) {
                            HARD -> 12
                            NORMAL -> 10
                            EASY -> 8
                        }
                    Independence -> 2
                    else -> TODO()
                }
    }

    object Values {
        fun get(): List<Reward> = arrayListOf(UnlockKey(), Medal(), Achievement())
    }

    object Creator {
        fun create(reward: Reward, rewardVariant: IRewardVariant): Reward {
            return when (reward) {
                is UnlockKey -> UnlockKey(rewardVariant as ReachVariant)
                is Medal -> Medal()
                is Achievement -> Achievement(rewardVariant as AchievementVariant)
            }
        }
    }

    abstract fun getAmountForReaching(complexity: Complexity): Int

    abstract fun getReachedReward(repository: IRepositoryHolder,
                                  complexity: Complexity,
                                  historyList: List<QuestHistory>): Single<Optional<Reward>>

    open fun getVariants(): List<IRewardVariant> = ArrayList()

    abstract fun getActualAmount(repository: IRepositoryHolder,
                                 complexity: Complexity,
                                 historyList: List<QuestHistory>): Single<Optional<Int>>

    open fun computeRemainingAmountForReaching(repository: IRepositoryHolder,
                                               complexity: Complexity,
                                               historyList: List<QuestHistory>): Single<Optional<Int>> =
            getActualAmount(repository, complexity, historyList).map {
                Optional(getAmountForReaching(complexity) - (it.data ?: 0))
            }

    abstract var variant: IRewardVariant?
}