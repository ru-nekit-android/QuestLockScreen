package ru.nekit.android.qls.domain.model

import io.reactivex.Single
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.AnswerType.RIGHT
import ru.nekit.android.qls.domain.model.AnswerType.WRONG
import ru.nekit.android.qls.domain.model.ReachVariant.Independence
import ru.nekit.android.qls.domain.model.ReachVariant.RightSeries
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.qls.domain.useCases.InternalGetCurrentQuestUseCase
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
    object Gold : MedalType(0, 0)
    object Silver : MedalType(1, 2)
    object Bronze : MedalType(3, 5)

    object Values {
        fun get(): List<MedalType> = listOf(Gold, Silver, Bronze)
    }
}

sealed class AchievementVariant(val count: Int) : IRewardVariant {

    object Newbe : AchievementVariant(2)
    data class NewbeByQuestAndQuestType(override var questAndQuestionType: QuestAndQuestionType? = null) :
            AchievementVariant(2), IRewardVariantWithQuestAndQuestionType
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

        override fun getAmountForReaching(rewardVariant: IRewardVariant, complexity: Complexity): Int =
                achievementVariant?.count ?: -1

        override fun getVariants(): List<IRewardVariant> = AchievementVariant.Values.get()

        override fun computeRemainingAmountForReaching(repository: IRepositoryHolder,
                                                       rewardVariant: IRewardVariant,
                                                       complexity: Complexity,
                                                       historyList: List<QuestHistory>): Single<Optional<Int>> =
                Single.just(Optional(null))

        override fun getReachedReward(repository: IRepositoryHolder,
                                      complexity: Complexity,
                                      historyList: List<QuestHistory>,
                                      rewardVariant: IRewardVariant): Single<Optional<Reward>> =

                InternalGetCurrentQuestUseCase().build().flatMap { quest ->
                    variant = rewardVariant
                    if (rewardVariant is IRewardVariantWithQuestAndQuestionType)
                        rewardVariant.questAndQuestionType = quest.questAndQuestionType()
                    repository.getRewardRepository().getCount(this).flatMap { count ->
                        if (count == 1) Single.just(Optional(null)) else {
                            getActualAmount(repository,
                                    rewardVariant,
                                    complexity,
                                    historyList).map { it.nonNullData }.map { actualAmount ->
                                Optional(if (actualAmount == getAmountForReaching(rewardVariant, complexity))
                                    Achievement(rewardVariant as AchievementVariant) else null)
                            }
                        }
                    }
                }

        override fun getActualAmount(repository: IRepositoryHolder,
                                     rewardVariant: IRewardVariant,
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

        override fun getAmountForReaching(rewardVariant: IRewardVariant, complexity: Complexity): Int = 2

        private fun getMedalTypeBy(allAmount: Int, rightAnswerAmount: Int, wrongAnswerAmount: Int): MedalType? =
                MedalType.Values.get().first {
                    wrongAnswerAmount in it.min..it.max
                }

        override fun computeRemainingAmountForReaching(repository: IRepositoryHolder,
                                                       rewardVariant: IRewardVariant,
                                                       complexity: Complexity,
                                                       historyList: List<QuestHistory>): Single<Optional<Int>> =
                Single.just(Optional(null))

        override fun getReachedReward(repository: IRepositoryHolder,
                                      complexity: Complexity,
                                      historyList: List<QuestHistory>,
                                      rewardVariant: IRewardVariant): Single<Optional<Reward>> =
                getActualAmount(repository,
                        rewardVariant,
                        complexity,
                        historyList).map { it.nonNullData }.map { actualAmount ->
                    var result: Medal? = null
                    if (actualAmount >= getAmountForReaching(rewardVariant, complexity)) {
                        getMedalTypeBy(historyList.size, actualAmount,
                                historyList.count { it.answerType == WRONG })?.let {
                            result = Medal(it)
                        }
                    }
                    Optional(result)
                }

        override fun getActualAmount(repository: IRepositoryHolder,
                                     rewardVariant: IRewardVariant,
                                     complexity: Complexity,
                                     historyList: List<QuestHistory>): Single<Optional<Int>> =
                Single.just(Optional(historyList.count { it.answerType == RIGHT }))
    }

    data class UnlockKey(var reachVariant: ReachVariant? = null) : Reward() {

        override var variant: IRewardVariant?
            get() = reachVariant
            set(value) {
                reachVariant = value as ReachVariant
            }

        override fun getVariants(): List<IRewardVariant> = ReachVariant.Values.get()

        override fun getReachedReward(repository: IRepositoryHolder,
                                      complexity: Complexity,
                                      historyList: List<QuestHistory>,
                                      rewardVariant: IRewardVariant): Single<Optional<Reward>> =
                getActualAmount(repository,
                        rewardVariant,
                        complexity,
                        historyList).map { it.nonNullData }.map { actualAmount ->
                    Optional(if (actualAmount == getAmountForReaching(rewardVariant, complexity))
                        UnlockKey(rewardVariant as ReachVariant) else null)
                }

        override fun getActualAmount(repository: IRepositoryHolder,
                                     rewardVariant: IRewardVariant,
                                     complexity: Complexity,
                                     historyList: List<QuestHistory>): Single<Optional<Int>> {
            return when (rewardVariant) {
                RightSeries ->
                    Single.fromCallable {
                        val actualAmount = historyList.size
                        val seriesLength = Math.min(actualAmount,
                                getAmountForReaching(rewardVariant, complexity))
                        Optional(historyList.subList(actualAmount - seriesLength, actualAmount)
                                .count { it.answerType == RIGHT })
                    }
                Independence -> {
                    Single.just(
                            Optional(historyList.count {
                                it.lockScreenStartType == LockScreenStartType.ON_NOTIFICATION_CLICK
                            })
                    )
                }
                else -> Single.just(Optional(null))
            }
        }

        override fun getAmountForReaching(rewardVariant: IRewardVariant,
                                          complexity: Complexity): Int =
                when (rewardVariant) {
                    RightSeries ->
                        when (complexity) {
                            HARD -> 4
                            NORMAL -> 1
                            EASY -> 2
                        }
                    Independence -> 2
                    else -> TODO()
                }
    }

    object Values {
        fun get(): List<Reward> = arrayListOf(UnlockKey(), Medal(), Achievement())
    }

    abstract fun getAmountForReaching(rewardVariant: IRewardVariant,
                                      complexity: Complexity): Int

    abstract fun getReachedReward(repository: IRepositoryHolder,
                                  complexity: Complexity,
                                  historyList: List<QuestHistory>,
                                  rewardVariant: IRewardVariant): Single<Optional<Reward>>

    open fun getVariants(): List<IRewardVariant> = ArrayList()

    abstract fun getActualAmount(repository: IRepositoryHolder,
                                 rewardVariant: IRewardVariant,
                                 complexity: Complexity,
                                 historyList: List<QuestHistory>): Single<Optional<Int>>

    open fun computeRemainingAmountForReaching(repository: IRepositoryHolder,
                                               rewardVariant: IRewardVariant,
                                               complexity: Complexity,
                                               historyList: List<QuestHistory>): Single<Optional<Int>> =
            getActualAmount(repository, rewardVariant, complexity, historyList).map {
                Optional(getAmountForReaching(rewardVariant, complexity) - (it.data ?: 0))
            }

    abstract var variant: IRewardVariant?
}