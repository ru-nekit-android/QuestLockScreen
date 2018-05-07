package ru.nekit.android.qls.domain.useCases

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.interactor.*
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.model.*
import ru.nekit.android.qls.domain.model.StatisticsPeriodType.MONTHLY
import ru.nekit.android.qls.domain.model.StatisticsPeriodType.WEEKLY
import ru.nekit.android.qls.domain.providers.UseCaseSupport
import ru.nekit.android.qls.domain.repository.IRepositoryHolder

class GetCurrentQuestStatisticsReportUseCase(private val repository: IRepositoryHolder,
                                             scheduler: ISchedulerProvider? = null
) : ParameterlessSingleUseCase<QuestStatisticsReport>(scheduler) {

    override fun build(): Single<QuestStatisticsReport> =
            GetCurrentQuestUseCase()
                    .build()
                    .flatMap { quest ->
                        pupilFlatMap {
                            repository
                                    .getQuestStatisticsReportRepository()
                                    .getOrCreate(it, quest.questAndQuestionType())
                        }
                    }
}

class SaveStatisticsReportUseCase(private val repository: IRepositoryHolder,
                                  scheduler: ISchedulerProvider? = null
) : CompletableUseCase<QuestStatisticsReport>(scheduler) {

    override fun build(parameter: QuestStatisticsReport): Completable =
            pupilFlatMapCompletable {
                repository.getQuestStatisticsReportRepository().save(it, parameter)
            }
}

class AddHistoryUseCase(private val repository: IRepositoryHolder,
                        scheduler: ISchedulerProvider? = null
) : CompletableUseCase<QuestHistory>(scheduler) {

    override fun build(parameter: QuestHistory): Completable =
            pupilFlatMapCompletable {
                repository.getQuestHistoryRepository().add(it, parameter)
            }

}

class GetAllStatisticsReportsUseCase(private val repository: IRepositoryHolder,
                                     scheduler: ISchedulerProvider? = null
) : ParameterlessSingleUseCase<List<QuestStatisticsReport>>(scheduler) {

    override fun build(): Single<List<QuestStatisticsReport>> =
            pupilFlatMap {
                repository.getQuestStatisticsReportRepository()
                        .getAll(it)
            }

}

class GetLastHistoryByLimitUseCase(private val repository: IRepositoryHolder,
                                   scheduler: ISchedulerProvider? = null
) : SingleUseCase<List<QuestHistory>, Long>(scheduler) {

    override fun build(parameter: Long): Single<List<QuestHistory>> = pupilFlatMap {
        repository.getQuestHistoryRepository().getLastHistoryByLimit(it, parameter)
    }

}

class FetchFirstResultableHistoryByCriteriaListUseCase(private val repository: IRepositoryHolder,
                                                       scheduler: ISchedulerProvider? = null
) : SingleUseCase<List<QuestHistory>, List<QuestHistoryCriteria>>(scheduler) {

    override fun build(parameter: List<QuestHistoryCriteria>): Single<List<QuestHistory>> =
            pupilFlatMap {
                repository.getQuestHistoryRepository()
                        .getHistoriesByCriteriaList(it, parameter)
            }

}

object QuestStatisticsAndHistoryUseCases : UseCaseSupport() {

    private val questHistoryRepository
        get() = repositoryHolder.getQuestHistoryRepository()

    internal fun lastHistory() = singleUseCase(schedulerProvider) {
        pupilFlatMap {
            questHistoryRepository
                    .getLastHistoryByLimit(it, 1)
                    .map {
                        Optional(if (it.isEmpty()) null else it[0])
                    }
        }
    }

    fun getLastHistory(body: (QuestHistory?) -> Unit) = lastHistory().use {
        body(it.data)
    }

    private fun getHistoryByStatisticsPeriodType(parameter: StatisticsPeriodType) = buildSingleUseCase(schedulerProvider) {
        pupilFlatMap {
            repositoryHolder.getQuestHistoryRepository().getHistoryByPeriod(it,
                    timeProvider.getTimestampBy(parameter))
        }
    }

    fun getStatisticsForMonth() = getStatisticsForPeriod(MONTHLY to WEEKLY)

    fun getStatisticsForPeriod(parameter: Pair<StatisticsPeriodType, StatisticsPeriodType>) =
            singleUseCase<List<Statistics>>(schedulerProvider) {
                getHistoryByStatisticsPeriodType(parameter.first).map { history ->
                    val periodIntervals = timeProvider.getPeriodIntervalForPeriod(parameter)
                    val currentTime = timeProvider.getCurrentTime()
                    val statisticsByPeriod = ArrayList<Statistics>(periodIntervals.size)
                    var periodNumber = 0
                    periodIntervals.forEach { periodInterval ->
                        val isCurrentPeriod = periodInterval.first <= currentTime &&
                                periodInterval.second >= currentTime
                        val isReachedPeriod = periodInterval.first < currentTime
                        val historyByPeriod = history.filter { it ->
                            it.timeStamp >= periodInterval.first
                                    && it.timeStamp < periodInterval.second
                        }
                        val statisticsByQuestAndQuestionType: MutableMap<QuestAndQuestionType,
                                StatisticsByQuestAndQuestionType> = HashMap()
                        historyByPeriod.forEach { history ->
                            history.questAndQuestionType.let {
                                if (statisticsByQuestAndQuestionType[it] == null)
                                    statisticsByQuestAndQuestionType[it] =
                                            StatisticsByQuestAndQuestionType(ArrayList())
                                statisticsByQuestAndQuestionType[it]?.history?.add(history)
                            }
                        }
                        var allTime: Long = 0
                        statisticsByQuestAndQuestionType.keys.forEach {
                            val statistics = statisticsByQuestAndQuestionType[it]
                            var time: Long = 0
                            var bestTime = Long.MAX_VALUE
                            var worstTime: Long = 0
                            var rightAnswerCount = 0
                            statistics?.let {
                                it.history.filter { it.answerType == AnswerType.RIGHT }.forEach {
                                    time += it.sessionTime
                                    worstTime = Math.max(worstTime, it.sessionTime)
                                    bestTime = Math.min(bestTime, it.sessionTime)
                                    rightAnswerCount++
                                }
                                it.answerCount = it.history.size
                                it.rightAnswerCount = rightAnswerCount
                                it.bestAnswerTime = bestTime
                                it.worseAnswerTime = worstTime
                                it.averageAnswerTime = time / it.history.size.toLong()
                            }
                            allTime += time
                        }
                        val allAnswerCount = historyByPeriod.size
                        statisticsByPeriod.add((if (allAnswerCount > 0) {
                            val rightAnswerCount = historyByPeriod.filter { it.answerType == AnswerType.RIGHT }.size
                            val rewardList = historyByPeriod.filter {
                                it.rewards.isNotEmpty()
                            }.map { it.rewards }.flatMap { it }
                            val rewardsMap: HashMap<Reward, Int> = HashMap()
                            rewardList.forEach { reward ->
                                if (rewardsMap[reward] == null)
                                    rewardsMap[reward] = 1
                                else
                                    rewardsMap[reward] = rewardsMap[reward]!! + 1
                            }
                            Statistics(periodNumber,
                                    parameter.second,
                                    periodInterval,
                                    allAnswerCount,
                                    rightAnswerCount,
                                    historyByPeriod,
                                    statisticsByQuestAndQuestionType,
                                    allTime / rightAnswerCount.toLong(),
                                    rewardsMap,
                                    isCurrentPeriod,
                                    isReachedPeriod)
                        } else Statistics(periodNumber, parameter.second, periodInterval,
                                isCurrentPeriod = isCurrentPeriod)))
                        periodNumber++
                    }
                    statisticsByPeriod.reversed()
                }
            }

    fun getPreviousHistoryWithBestSessionTime(questAndQuestionType: QuestAndQuestionType, body: (Optional<QuestHistory>) -> Unit) =
            useSingleUseCase<Optional<QuestHistory>>(schedulerProvider, {
                pupilFlatMap {
                    repositoryHolder.getQuestHistoryRepository()
                            .getPreviousHistoryItemWithBestSessionTime(it, questAndQuestionType)
                }
            }, body)

}

class UpdateLastHistoryItemUseCase(private val repository: IRepositoryHolder,
                                   scheduler: ISchedulerProvider? = null
) : CompletableUseCase<QuestHistory>(scheduler) {

    override fun build(parameter: QuestHistory): Completable =
            pupilFlatMapCompletable {
                repository.getQuestHistoryRepository().updateLastHistoryItem(it, parameter)
            }

}