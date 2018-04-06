package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.FlowableProcessor
import io.reactivex.schedulers.Schedulers
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.*
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.QuestAndQuestionType
import ru.nekit.android.qls.domain.model.Reward
import ru.nekit.android.qls.domain.repository.IRepositoryHolder
import ru.nekit.android.utils.asBooleanSingleIf
import java.util.*

class AddRewardUseCase(private val repository: IRepositoryHolder,
                       scheduler: ISchedulerProvider? = null) :
        CompletableUseCase<Reward>(scheduler) {

    override fun build(parameter: Reward): Completable =
            repository.getRewardRepository().add(parameter)

}

class RemoveRewardUseCase(private val repository: IRepositoryHolder,
                          scheduler: ISchedulerProvider? = null) :
        CompletableUseCase<Reward>(scheduler) {

    override fun build(parameter: Reward): Completable =
            repository.getRewardRepository().remove(parameter)

}

class GetReachedRewardsUseCase(private val repository: IRepositoryHolder,
                               scheduler: ISchedulerProvider? = null) :
        SingleUseCase<List<Reward>, QuestAndQuestionType>(scheduler) {

    override fun build(parameter: QuestAndQuestionType): Single<List<Reward>> =
            pupil(repository) { pupil ->
                Flowable.fromIterable(ArrayList<Single<Optional<Reward>>>().also { list ->
                    Reward.Values.get().forEach { rewardSource ->
                        rewardSource.getVariants().forEach { rewardVariant ->
                            val reward = Reward.Creator.create(rewardSource, rewardVariant)
                            repository.getQuestHistoryCriteriaRepository()
                                    .getQuestHistoryCriteria(reward, parameter)?.let { criteria ->
                                        list.add(FetchFirstResultableHistoryByCriteriaListUseCase(repository)
                                                .build(criteria).flatMap { historyList ->
                                                    reward.getReachedReward(repository,
                                                            pupil.complexity!!,
                                                            historyList)
                                                })
                                    }
                        }
                    }
                }).flatMapMaybe { task -> task.filter { it.isNotEmpty() }.map { it.nonNullData }.subscribeOn(Schedulers.computation()) }
                        .toList()
            }
}

class ConsumeRewardUseCase(private val repository: IRepositoryHolder,
                           private val scheduler: ISchedulerProvider? = null) :
        SingleUseCase<Boolean, Reward>(scheduler) {
    override fun build(parameter: Reward): Single<Boolean> = GetRewardCountUseCase(repository)
            .build(parameter)
            .flatMap {
                RemoveRewardUseCase(repository)
                        .build(parameter)
                        .doOnComplete {
                            RewardHolder.getAndNotify(parameter, repository, scheduler)
                        }.asBooleanSingleIf { it > 0 }
            }

}

class GetRewardCountUseCase(private val repository: IRepositoryHolder,
                            val scheduler: ISchedulerProvider? = null) :
        SingleUseCase<Int, Reward>(scheduler) {
    override fun build(parameter: Reward): Single<Int> =
            repository.getRewardRepository().getCount(parameter)

}

class GetRemainingAmountForReaching(private val repository: IRepositoryHolder,
                                    val scheduler: ISchedulerProvider? = null) :
        ParameterlessSingleUseCase<List<Pair<Reward, Int>>>(scheduler) {

    override fun build(): Single<List<Pair<Reward, Int>>> =
            pupil(repository) { pupil ->
                Flowable.fromIterable(ArrayList<Single<Pair<Reward, Optional<Int>>>>().also { list ->
                    Reward.Values.get().forEach { rewardSource ->
                        rewardSource.getVariants().forEach { rewardVariant ->
                            val reward = Reward.Creator.create(rewardSource, rewardVariant)
                            repository.getQuestHistoryCriteriaRepository()
                                    .getQuestHistoryCriteria(reward)?.let { criteria ->
                                        list.add(FetchFirstResultableHistoryByCriteriaListUseCase(repository)
                                                .build(criteria).flatMap { historyList ->
                                                    reward.computeRemainingAmountForReaching(repository,
                                                            pupil.complexity!!,
                                                            historyList).map {
                                                        Pair(reward, it)
                                                    }
                                                })
                                    }
                        }
                    }
                }).flatMapMaybe { task ->
                    task.filter { it.second.isNotEmpty() }.map {
                        Pair(it.first, it.second.nonNullData)
                    }.subscribeOn(Schedulers.computation())
                }.toList()
            }
}

class ListenRewardUseCase(
        private val repository: IRepositoryHolder,
        private val scheduler: ISchedulerProvider? = null) :
        FlowableUseCase<Int, Reward>(scheduler) {

    override fun build(parameter: Reward): Flowable<Int> = RewardHolder.publisher.doOnSubscribe {
        RewardHolder.getAndNotify(parameter, repository, scheduler)
    }.filter { it.first == parameter }.map { it.second }.distinctUntilChanged()
}

internal object RewardHolder {

    private fun notify(reward: Reward, value: Int) = publisher.onNext(reward to value)

    val publisher: FlowableProcessor<Pair<Reward, Int>> = BehaviorProcessor.create<Pair<Reward, Int>>().toSerialized()

    fun getAndNotify(targetReward: Reward?, repository: IRepositoryHolder,
                     scheduler: ISchedulerProvider?) {
        if (targetReward == null) {
            Reward.Values.get().forEach { reward ->
                GetRewardCountUseCase(repository, scheduler).use(reward) {
                    notify(reward, it)
                }
            }
        } else {
            GetRewardCountUseCase(repository, scheduler).use(targetReward) {
                notify(targetReward, it)
            }
        }
    }

}