package ru.nekit.android.qls.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.domain.repository.ICounter
import ru.nekit.android.domain.repository.IReactiveCRUD
import ru.nekit.android.domain.repository.IStateRepository
import ru.nekit.android.qls.domain.model.*
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.model.resources.ResourceGroupCollection
import ru.nekit.android.qls.domain.model.resources.common.IResourceHolder
import ru.nekit.android.qls.domain.model.resources.common.IVisualResourceHolder
import ru.nekit.android.qls.shared.model.*
import ru.nekit.android.qls.shared.repository.ISetupWizardSettingsRepository

interface IRepositoryHolder {

    fun getPupilRepository(): IPupilRepository
    fun getCurrentPupilRepository(): ICurrentPupilRepository
    fun getQuestSetupWizardSettingRepository(): IQuestSetupWizardSettingRepository
    fun getRewardRepository(): IRewardRepository
    fun getUnlockSecretRepository(): IUnlockSecretRepository
    fun getPhoneContactRepository(): IPhoneContactRepository
    fun getSessionRepository(): ISessionRepository
    fun getQuestTrainingProgramRepository(): IQuestTrainingProgramRepository
    fun getPupilStatisticsRepository(): IPupilStatisticsRepository
    fun getQuestStateRepository(): IQuestStateRepository
    fun getQuestRepository(): IQuestRepository
    fun getQuestResourceRepository(): IQuestResourceRepository
    fun getLockScreenRepository(): ILockScreenRepository
    fun getTransitionChoreographRepository(): ITransitionChoreographRepository
    fun getQuestStatisticsReportRepository(): IQuestStatisticsReportRepository
    fun getQuestHistoryRepository(): IQuestHistoryRepository
    fun getQuestParams(): IQuestParams
    fun getQuestHistoryCriteriaRepository(): IQuestHistoryCriteriaRepository
}

interface IQuestParams {

    val delayedPlayDelay: Long

}

interface IQuestHistoryCriteriaRepository {

    fun getQuestHistoryCriteria(reward: Reward,
                                rewardVariant: IRewardVariant,
                                questAndQuestionType: QuestAndQuestionType? = null): List<QuestHistoryCriteria>?

}

interface IQuestStatisticsReportRepository {

    fun save(pupil: Pupil, report: QuestStatisticsReport): Completable

    fun getOrCreate(pupil: Pupil,
                    questAndQuestionType: QuestAndQuestionType): Single<QuestStatisticsReport>

    fun getAll(pupil: Pupil): Single<List<QuestStatisticsReport>>

}

interface IQuestHistoryRepository {

    fun add(pupil: Pupil, history: QuestHistory): Completable

    fun getLastHistoryByLimit(pupil: Pupil,
                              limit: Long,
                              questAndQuestionType: QuestAndQuestionType? = null): Single<List<QuestHistory>>

    fun getHistoriesByCriteriaList(pupil: Pupil, criteriaList: List<QuestHistoryCriteria>): Single<List<QuestHistory>>

    fun getHistoryByPeriod(pupil: Pupil, timestamp: Long): Single<List<QuestHistory>>

    fun getPreviousHistoryItemWithBestSessionTime(pupil: Pupil, questAndQuestionType: QuestAndQuestionType):
            Single<Optional<QuestHistory>>

    fun updateLastHistoryItem(pupil: Pupil, item: QuestHistory): Completable

}

interface IPupilRepository : IReactiveCRUD<Pupil, String> {

    fun getCurrentPupil(): Single<Optional<Pupil>>

    fun setCurrentPupil(pupil: Pupil): Completable

    fun dropCurrentPupil(): Completable

    class PupilIsNotExist : Throwable("Pupil in not set")

    class CurrentPupilIsNotSet : Throwable("Current pupil in not save")

}

interface ICurrentPupilRepository {

    fun getCurrentUuid(): Single<Optional<String>>

    fun setCurrentUuid(pupilUuid: String): Completable

    fun removeCurrentUuid(): Completable

}

interface IQuestSetupWizardSettingRepository : ISetupWizardSettingsRepository {

    val skipAfterRightAnswer: Boolean

    val timeForSkipAfterRightAnswer: Long

}

interface IRewardRepository {

    fun add(reward: Reward): Completable

    fun remove(reward: Reward): Completable

    fun getCount(reward: Reward): Single<Int>

    //for test
    fun clear()

}

interface IUnlockSecretRepository {

    fun get(): String?

    fun set(value: String)

}

interface IPhoneContactRepository {

    fun add(pupil: Pupil, contact: PhoneContact): Completable

    fun remove(pupil: Pupil, contact: PhoneContact): Completable

    fun getAll(pupil: Pupil): Single<List<PhoneContact>>

    fun getByContactId(pupil: Pupil, contactId: Long): Single<Optional<PhoneContact>>
}


interface ISessionRepository {

    fun get(sessionName: String): Long

    fun set(sessionName: String, time: Long)

}

interface IQuestStateRepository : IStateRepository<QuestState>

interface IQuestRepository {

    fun hasSavedQuest(pupil: Pupil): Single<Boolean>

    fun save(pupil: Pupil, quest: Quest): Completable

    fun restoreQuest(pupil: Pupil): Single<Optional<Quest>>

    fun clear(pupil: Pupil): Completable

    fun add(pupil: Pupil, quest: Quest, questString: String): Completable

}

interface IQuestTrainingProgramRepository {

    fun create(
            sex: PupilSex,
            complexity: Complexity,
            forceUpdate: Boolean
    ): Single<Boolean>

    fun get(
            sex: PupilSex,
            complexity: Complexity
    ): Single<Optional<QuestTrainingProgram>>

    fun getLevel(
            sex: PupilSex,
            complexity: Complexity,
            index: Int
    ): Single<Optional<QuestTrainingProgramLevel>>

    fun getAllLevels(
            sex: PupilSex,
            complexity: Complexity)
            : Single<List<QuestTrainingProgramLevel>>

    fun getQuestRule(
            sex: PupilSex,
            complexity: Complexity,
            level: QuestTrainingProgramLevel,
            questType: QuestType, questionType: QuestionType
    ): Single<Optional<QuestTrainingProgramRule>>

    fun getQuestRules(
            sex: PupilSex,
            complexity: Complexity,
            level: QuestTrainingProgramLevel
    ): Single<List<QuestTrainingProgramRule>>

    fun getPriorityRule(
            sex: PupilSex,
            complexity: Complexity,
            questType: QuestType,
            questionType: QuestionType
    ): Single<Optional<QuestTrainingProgramRulePriority>>

    fun getAllPriorityRule(
            sex: PupilSex,
            complexity: Complexity
    ): Single<List<QuestTrainingProgramRulePriority>>
}

interface IPupilStatisticsRepository {

    fun create(pupil: Pupil): Single<Boolean>

    fun get(pupil: Pupil): Single<PupilStatistics>

    fun update(pupil: Pupil, statistics: PupilStatistics): Completable

}

interface IQuestResourceRepository {

    val visualResourceList: List<IVisualResourceHolder>

    fun getWordList(wordLength: Int): List<String>

    fun getVisualResourceItemsByGroup(group: ResourceGroupCollection): List<IVisualResourceHolder>

    fun getVisualResourceItemIdsByGroup(group: ResourceGroupCollection): List<Int>

    fun getVisualResourceItemId(item: IVisualResourceHolder): Int

    fun getVisualResourceItemById(id: Int): IVisualResourceHolder

    fun getNounStringRepresentation(resourceHolder: IVisualResourceHolder): String

    fun localizeAdjectiveAndNounStringResourceIfNeed(
            adjectiveItemHolder: IResourceHolder,
            nounItemHolder: IVisualResourceHolder,
            formatString: String
    ): String
}

interface ILockScreenRepository {

    fun switchOn(value: Boolean)

    fun isSwitchedOn(): Boolean

    fun incomeCallInProcess(value: Boolean)

    fun incomeCallInProcess(): Boolean

    fun outgoingCallInProcess(value: Boolean)

    fun outgoingCallInProcess(): Boolean

    fun getLastStartType(): Single<Optional<LockScreenStartType>>

    fun saveStartType(value: LockScreenStartType): Completable

}

interface ITransitionChoreographRepository {

    fun setTransition(type: Transition.Type, transition: Transition?)

    fun getTransition(type: Transition.Type): Transition?

    fun advertWasShown(value: Boolean)

    fun advertWasShown(): Boolean

    fun introductionWasShown(value: Boolean)

    fun introductionWasShown(): Boolean

    val introductionIsPresented: Boolean

    val advertIsPresented: Boolean

    val advertCounter: ICounter

    val questSeriesCounter: ICounter

    val advertStartValue: Int
}