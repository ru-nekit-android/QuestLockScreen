package ru.nekit.android.qls.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.Property
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.nekit.android.data.*
import ru.nekit.android.data.shared.SetupWizardBaseSettingsRepository
import ru.nekit.android.data.shared.ktQuestTypeSupport
import ru.nekit.android.data.support.JsonHelper
import ru.nekit.android.data.support.ObjectBoxSupport
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.domain.repository.IEntityFromMapper
import ru.nekit.android.domain.repository.IEntityMapper
import ru.nekit.android.qls.data.entity.*
import ru.nekit.android.qls.data.entity.QuestHistoryEntity_.*
import ru.nekit.android.qls.data.entity.RewardDataConverter.name
import ru.nekit.android.qls.data.repository.store.QuestStore
import ru.nekit.android.qls.domain.model.*
import ru.nekit.android.qls.domain.model.Transition.ADVERT
import ru.nekit.android.qls.domain.model.Transition.INTRODUCTION
import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.domain.model.resources.ResourceGroupCollection
import ru.nekit.android.qls.domain.repository.*
import ru.nekit.android.qls.domain.repository.IPupilRepository.PupilIsNotExist
import ru.nekit.android.qls.shared.model.*
import ru.nekit.android.qls.shared.model.QuestType.*
import ru.nekit.android.utils.toSingle
import java.io.InputStream
import java.io.InputStreamReader

class PupilRepository(private val repository: IRepositoryHolder, boxStore: BoxStore
) : ObjectBoxSupport<PupilEntity>(boxStore), IPupilRepository {

    override fun createBox(): Box<PupilEntity> = initBox()

    override fun getCurrentPupil(): Single<Optional<Pupil>> =
            repository.getCurrentPupilRepository().getCurrentUuid().flatMap {
                read(it.nonNullData)
            }

    override fun setCurrentPupil(pupil: Pupil): Completable =
            repository.getCurrentPupilRepository().setCurrentUuid(pupil.uuid)

    override fun dropCurrentPupil(): Completable =
            repository.getCurrentPupilRepository().removeCurrentUuid()

    override fun create(value: Pupil): Completable =
            boxCompletableUsingFromRunnable {
                it.put(Mapper.to(value))
            }

    fun getEntityId(pupil: Pupil): Single<Long> =
            getEntity(pupil).map { it.data?.id ?: -1 }

    fun getCurrentEntity(): Single<Long> = getCurrentPupil().map { it.nonNullData }.flatMap {
        getEntityId(it)
    }

    private fun getEntity(pupil: Pupil): Single<Optional<PupilEntity>> =
            boxSingleUsingWithCallable {
                Optional(it.query().equal(PupilEntity_.uuid, pupil.uuid).build().findUnique())
            }

    override fun read(value: String): Single<Optional<Pupil>> =
            readInternal(value).map { o -> Optional(o.data?.let { from(o.nonNullData) }) }

    private fun readInternal(value: String): Single<Optional<PupilEntity>> =
            boxSingleUsingWithCallable {
                Optional(it.queryByUuid(value).findFirst())
            }

    @Throws(PupilIsNotExist::class)
    override fun update(value: Pupil) =
            boxCompletableUsing { box ->
                readInternal(value.uuid).flatMapCompletable { pupilEntity ->
                    if (pupilEntity.isEmpty()) {
                        Completable.error(PupilIsNotExist())
                    } else
                        Completable.fromCallable {
                            box.put(Mapper.to(value).also {
                                with(pupilEntity.nonNullData) {
                                    //non updatable
                                    //it.uuid = uuid
                                    it.id = id
                                }
                            })
                        }
                }
            }

    override fun delete(value: Pupil) =
            boxCompletableUsingFromRunnable {
                it.queryByUuid(value.uuid).remove()
            }

    private fun Box<PupilEntity>.queryByUuid(uuid: String): Query<PupilEntity> = queryBy(PupilEntity_.uuid, uuid)

    companion object Mapper : IEntityMapper<PupilEntity, Pupil> {
        override fun from(value: PupilEntity): Pupil {
            with(value) {
                return Pupil(
                        uuid,
                        name,
                        sex,
                        complexity,
                        avatar
                )
            }
        }

        override fun to(value: Pupil): PupilEntity {
            with(value) {
                return PupilEntity(
                        0,
                        uuid,
                        name,
                        sex,
                        complexity,
                        avatar
                )
            }
        }
    }
}

open class CurrentPupilRepository(sharedPreferences: SharedPreferences) : ReactiveStringKeyStringValueStore(sharedPreferences),
        ICurrentPupilRepository {

    companion object NAMES {
        private const val CURRENT: String = "current_pupil"
    }

    override fun setCurrentUuid(pupilUuid: String): Completable = put(CURRENT, pupilUuid)

    override fun getCurrentUuid(): Single<Optional<String>> = get(CURRENT)

    override fun removeCurrentUuid(): Completable = remove(CURRENT)

}

open class RewardRepository(sharedPreferences: SharedPreferences) : IRewardRepository {

    private val store: StringKeyIntValueStore = StringKeyIntValueStore(sharedPreferences)

    override fun add(reward: Reward): Completable =
            getCount(reward).map {
                it + 1
            }.flatMapCompletable { count ->
                        Completable.fromRunnable { store.put(name(reward), count) }
                    }

    override fun remove(reward: Reward): Completable =
            getCount(reward).map {
                it - 1
            }.flatMapCompletable { count ->
                        Completable.fromRunnable { store.put(name(reward), count) }
                    }

    override fun getCount(reward: Reward): Single<Int> = Single.fromCallable {
        val name = name(reward)
        if (store.contains(name))
            store.get(name)
        else 0
    }

    override fun clear() = store.clear()

    private fun name(reward: Reward): String {
        val result: ArrayList<String> = ArrayList()
        result.add(reward.javaClass.simpleName)
        when (reward) {
            is Reward.UnlockKey -> {
            }
            is Reward.Medal ->
                reward.medalType?.let {
                    result.add(it.name())
                }
            is Reward.Achievement -> {
                reward.variant?.let {
                    result.add(it.name())
                }
            }
        }
        if (reward.variant is IRewardVariantWithQuestAndQuestionType) {
            reward.variant?.let {
                (it as IRewardVariantWithQuestAndQuestionType).questAndQuestionType?.let {
                    result.add(it.name())
                }
            }
        }
        return result.joinToString(RewardDataConverter.DELIMITER)
    }

}

class QuestStatisticsReportRepository(repository: IRepositoryHolder, boxStore: BoxStore) :
        ObjectBoxSupport<QuestStatisticsReportEntity>(boxStore),
        IQuestStatisticsReportRepository {

    private val pupilRepository = PupilRepository(repository, boxStore)

    override fun createBox(): Box<QuestStatisticsReportEntity> = initBox()

    override fun getOrCreate(pupil: Pupil, questAndQuestionType: QuestAndQuestionType): Single<QuestStatisticsReport> =
            pupilRepository.getEntityId(pupil).flatMap { id ->
                internalGetBy(id, questAndQuestionType).map { entity ->
                    if (entity.isEmpty())
                        QuestStatisticsReport(questAndQuestionType)
                    else
                        from(entity.nonNullData)
                }
            }

    override fun save(pupil: Pupil, report: QuestStatisticsReport): Completable =
            pupilRepository.getEntityId(pupil).flatMapCompletable { id ->
                internalGetBy(id, report.questAndQuestionType).flatMapCompletable { entityOptional ->
                    boxCompletableUsingFromRunnable {
                        it.put(
                                Mapper.to(report).apply {
                                    if (entityOptional.isNotEmpty())
                                        this.id = entityOptional.nonNullData.id
                                    pupilId = id
                                }
                        )
                    }
                }
            }

    private fun Box<QuestStatisticsReportEntity>.queryBy(pupilId: Long,
                                                         questType: QuestType? = null,
                                                         questionType: QuestionType? = null): Query<QuestStatisticsReportEntity> =
            query().equal(QuestStatisticsReportEntity_.pupilId, pupilId).also {
                if (questType != null)
                    it.equal(QuestStatisticsReportEntity_.questType, questType.asParameter())
            }.also {
                        if (questionType != null)
                            it.equal(QuestStatisticsReportEntity_.questionType, questionType.asParameter())
                    }.build()

    private fun internalGetBy(pupilId: Long, questAndQuestionType: QuestAndQuestionType) =
            boxSingleUsingWithCallable {
                Optional(it.queryBy(pupilId, questAndQuestionType.questType,
                        questAndQuestionType.questionType).findFirst())
            }

    override fun getAll(pupil: Pupil): Single<List<QuestStatisticsReport>> =
            pupilRepository.getEntityId(pupil).flatMap { id ->
                boxSingleUsingWithCallable {
                    it.queryBy(id).find().map { from(it) }
                }
            }

    companion object Mapper : IEntityMapper<QuestStatisticsReportEntity, QuestStatisticsReport> {

        override fun to(value: QuestStatisticsReport): QuestStatisticsReportEntity {
            with(value) {
                return QuestStatisticsReportEntity(
                        0,
                        0,
                        questAndQuestionType.questType,
                        questAndQuestionType.questionType,
                        rightAnswerCount,
                        rightAnswerSeriesCount,
                        rightAnswerSeriesCounter,
                        wrongAnswerCount,
                        wrongAnswerSeriesCounter,
                        bestAnswerTime,
                        worseAnswerTime,
                        rightAnswerSummandTime
                )
            }
        }

        override fun from(value: QuestStatisticsReportEntity): QuestStatisticsReport {
            with(value) {
                return QuestStatisticsReport(
                        questType!! + questionType!!,
                        rightAnswerCount,
                        rightAnswerSeriesCount,
                        rightAnswerSeriesCounter,
                        wrongAnswerCount,
                        wrongAnswerSeriesCounter,
                        bestAnswerTime,
                        worseAnswerTime,
                        rightAnswerSummandTime
                )
            }
        }
    }
}

class QuestHistoryRepository(repository: IRepositoryHolder, boxStore: BoxStore) :
        ObjectBoxSupport<QuestHistoryEntity>(boxStore),
        IQuestHistoryRepository {

    override fun getHistoryByPeriod(pupil: Pupil, timestamp: Long): Single<List<QuestHistory>> =
            pupilRepository.getEntityId(pupil).flatMap { pupilId ->
                boxSingleUsingWithCallable {
                    getQueryBuilderByCriteria(it, pupilId, QuestHistoryCriteria(
                            timestampGreaterThan = true,
                            timestamp = timestamp
                    )).build().find().map { Mapper.from(it) }
                }
            }

    private val pupilRepository = PupilRepository(repository, boxStore)

    override fun createBox(): Box<QuestHistoryEntity> = initBox()

    override fun add(pupil: Pupil, history: QuestHistory): Completable =
            pupilRepository.getEntityId(pupil).flatMapCompletable { pupilId ->
                boxCompletableUsingFromRunnable {
                    it.put(Mapper.to(history).apply {
                        this.pupilId = pupilId
                    })
                }
            }

    override fun getPreviousHistoryItemWithBestSessionTime(pupil: Pupil,
                                                           questAndQuestionType: QuestAndQuestionType):
            Single<Optional<QuestHistory>> =
            pupilRepository.getEntityId(pupil).flatMap { pupilId ->
                boxSingleUsingWithCallable {
                    val lastId = getQueryBuilderByCriteria(it, pupilId,
                            QuestHistoryCriteria(
                                    questType = questAndQuestionType.questType,
                                    questionType = questAndQuestionType.questionType,
                                    answerType = AnswerType.RIGHT))
                            .build()
                            .property(id)
                            .max()
                    val query = getQueryBuilderByCriteria(it, pupilId,
                            QuestHistoryCriteria(
                                    questType = questAndQuestionType.questType,
                                    questionType = questAndQuestionType.questionType,
                                    answerType = AnswerType.RIGHT))
                            .notEqual(id, lastId)
                            .build()
                    var result: QuestHistoryEntity? = null
                    if (query.count() > 0) {
                        val minSessionTime = query.property(sessionTime).min()
                        result = getQueryBuilderByCriteria(it, pupilId,
                                QuestHistoryCriteria(
                                        questType = questAndQuestionType.questType,
                                        questionType = questAndQuestionType.questionType,
                                        answerType = AnswerType.RIGHT))
                                .equal(sessionTime, minSessionTime)
                                .build().findFirst()
                    }
                    Optional(result?.let {
                        Mapper.from(it)
                    })
                }
            }

    override fun getLastHistoryByLimit(pupil: Pupil, limit: Long, questAndQuestionType: QuestAndQuestionType?)
            : Single<List<QuestHistory>> =
            pupilRepository.getEntityId(pupil).flatMap { pupilId ->
                boxSingleUsingWithCallable {
                    getQueryBuilderByCriteria(it, pupilId,
                            QuestHistoryCriteria())
                            .build().let {
                        val queryCount = it.count()
                        val count = if (limit <= queryCount) queryCount else limit
                        it.find(count - limit, limit).map { Mapper.from(it) }
                    }
                }
            }

    private fun getQueryBuilderByCriteria(box: Box<QuestHistoryEntity>, pupilId: Long, criteria: QuestHistoryCriteria)
            : QueryBuilder<QuestHistoryEntity> =
            box.query().equal(QuestHistoryEntity_.pupilId, pupilId).apply {
                if (criteria.answerType != null)
                    equal(answerType, criteria.answerType!!.asParameter())
                if (criteria.lockScreenStartType != null)
                    equal(lockScreenStartType, criteria.lockScreenStartType!!.asParameter())
                if (criteria.questType != null)
                    equal(questType, criteria.questType!!.asParameter())
                if (criteria.questionType != null)
                    equal(questionType, criteria.questionType!!.asParameter())
                if (criteria.rewards != null)
                    criteria.rewards!!.forEach {
                        contains(rewards, it.asParameter())
                    }
                if (criteria.timestampGreaterThan != null) {
                    if (criteria.timestamp != null)
                        if (criteria.timestampGreaterThan == true)
                            greater(timeStamp, criteria.timestamp!!)
                        else
                            less(timeStamp, criteria.timestamp!!)
                }
            }

    override fun getHistoriesByCriteriaList(pupil: Pupil,
                                            criteriaList: List<QuestHistoryCriteria>):
            Single<List<QuestHistory>> {
        return pupilRepository.getEntityId(pupil)
                .flatMap { pupilId ->
                    boxSingleUsingWithCallable { box ->
                        var result: List<QuestHistoryEntity> = ArrayList()
                        for (criteria in criteriaList) {
                            val query = getQueryBuilderByCriteria(box, pupilId, criteria).build()
                            val count: Long = query.count()
                            if (count > 0) {
                                if (criteria.limitByLastItem) {
                                    val rewardList = query.find()
                                    result = box.query()
                                            .greater(id, rewardList.last().id)
                                            .build()
                                            .find()
                                    break
                                } else
                                    result = query.find()
                                if (result.isNotEmpty())
                                    break
                            }
                        }
                        result.map { Mapper.from(it) }
                    }
                }
    }

    override fun updateLastHistoryItem(pupil: Pupil, item: QuestHistory): Completable =
            pupilRepository.getEntityId(pupil).flatMapCompletable { pupilId ->
                boxCompletableUsingFromRunnable {
                    val query = it.queryBy(QuestHistoryEntity_.pupilId, pupilId)
                    val count = query.count()
                    if (count > 0) {
                        val entities = query.find(count - 1, 1.toLong())
                        if (entities.size == 1) {
                            val entity = entities[0]
                            it.put(Mapper.to(item).apply {
                                id = entity.id
                                this.pupilId = pupilId
                            })
                        } else
                            Completable.complete()
                    } else
                        Completable.complete()
                }
            }

    companion object Mapper : IEntityMapper<QuestHistoryEntity, QuestHistory> {

        override fun to(value: QuestHistory): QuestHistoryEntity {

            with(value) {
                return QuestHistoryEntity(0, 0,
                        questAndQuestionType.questType,
                        questAndQuestionType.questionType,
                        score,
                        lockScreenStartType,
                        answerType,
                        rewards,
                        sessionTime,
                        recordTypes,
                        levelUp,
                        timeStamp)
            }
        }

        override fun from(value: QuestHistoryEntity): QuestHistory {
            with(value) {
                return QuestHistory(questType!! + questionType!!,
                        score,
                        lockScreenStartType!!,
                        answerType!!,
                        sessionTime,
                        rewards ?: ArrayList(),
                        recordTypes,
                        levelUp,
                        timeStamp)
            }
        }
    }
}

class QuestRepository(repository: IRepositoryHolder,
                      private val questStore: QuestStore,
                      boxStore: BoxStore) :
        ObjectBoxSupport<QuestEntity>(boxStore), IQuestRepository {

    private val pupilRepository = PupilRepository(repository, boxStore)

    override fun createBox(): Box<QuestEntity> = initBox()

    override fun hasSavedQuest(pupil: Pupil): Single<Boolean> = Single.fromCallable {
        questStore.hasSaved(pupil.uuid)
    }

    override fun save(pupil: Pupil, quest: Quest): Completable = Single.fromCallable {
        questStore.save(quest, pupil.uuid)
    }.flatMapCompletable {
                add(pupil, quest, questStore.questString)
            }

    override fun restoreQuest(pupil: Pupil): Single<Optional<Quest>> = Single.fromCallable {
        Optional(questStore.restore(pupil.uuid))
    }

    override fun clear(pupil: Pupil): Completable = Completable.fromRunnable {
        questStore.clear(pupil.uuid)
    }

    override fun add(pupil: Pupil, quest: Quest, questString: String): Completable =
            pupilRepository.getEntityId(pupil).flatMapCompletable { pupilId ->
                boxCompletableUsingFromRunnable {
                    it.put(QuestEntity(0,
                            pupilId,
                            quest.questType,
                            quest.questionType, questString))
                }
            }

    fun getLastId(pupil: Pupil): Single<Optional<Long?>> =
            boxSingleUsingWithCallable {
                val query = it.query().build()
                val lastList = query.find(query.count() - 1, 1)
                Optional(if (lastList.size > 0) {
                    lastList.last().id
                } else null)
            }
}

class PhoneContactRepository(repository: IRepositoryHolder, boxStore: BoxStore) :
        ObjectBoxSupport<PhoneContactEntity>(boxStore),
        IPhoneContactRepository {

    private val pupilRepository: PupilRepository = PupilRepository(repository, boxStore)

    override fun createBox(): Box<PhoneContactEntity> = initBox()

    override fun add(pupil: Pupil, contact: PhoneContact): Completable =
            pupilRepository.getEntityId(pupil).flatMapCompletable { pupilEntityId ->
                boxCompletableUsingFromRunnable {
                    with(Mapper.to(contact)) {
                        pupilId = pupilEntityId
                        it.put(this)
                    }
                }
            }

    override fun getByContactId(pupil: Pupil, contactId: Long): Single<Optional<PhoneContact>> =
            pupilRepository.getEntityId(pupil).flatMap { pupilEntityId ->
                boxSingleUsingWithCallable {
                    val phoneContactEntity = it.query().equal(PhoneContactEntity_.contactId,
                            contactId).equal(PhoneContactEntity_.pupilId, pupilEntityId).build().findUnique()
                    Optional(phoneContactEntity?.let { Mapper.from(phoneContactEntity) })
                }
            }

    override fun remove(pupil: Pupil, contact: PhoneContact): Completable =
            pupilRepository.getEntityId(pupil).flatMapCompletable { pupilEntityId ->
                boxCompletableUsingFromRunnable {
                    it.query().equal(PhoneContactEntity_.contactId, contact.contactId)
                            .equal(PhoneContactEntity_.pupilId, pupilEntityId).build().remove()
                }
            }

    override fun getAll(pupil: Pupil): Single<List<PhoneContact>> =
            pupilRepository.getEntityId(pupil).flatMap { pupilEntityId ->
                boxSingleUsingWithCallable {
                    it.query().equal(PhoneContactEntity_.pupilId, pupilEntityId).build().find().mapTo(ArrayList()) { Mapper.from(it) }
                }
            }

    object Mapper : IEntityMapper<PhoneContactEntity, PhoneContact> {

        override fun to(value: PhoneContact): PhoneContactEntity {
            with(value) {
                return PhoneContactEntity(0, contactId, 0, name, phoneNumber)
            }
        }

        override fun from(value: PhoneContactEntity): PhoneContact {
            with(value) {
                return PhoneContact(
                        contactId,
                        name,
                        phoneNumber
                )
            }
        }
    }
}

open class UnlockSecretRepository(sharedPreferences: SharedPreferences) : IUnlockSecretRepository {

    companion object {

        private const val UNLOCK_SECRET = "ru.nekit.android.qls.unlock_secret_v2"

    }

    private val store: StringKeyStringValueStore = StringKeyStringValueStore(sharedPreferences)

    override fun get(): String? = store.get(UNLOCK_SECRET)

    override fun set(value: String) {
        store.put(UNLOCK_SECRET, value)
    }
}

open class SessionRepository(sharedPreferences: SharedPreferences) : ISessionRepository {

    private val store: StringKeyLongValueStore = StringKeyLongValueStore(sharedPreferences)

    override fun get(sessionName: String): Long = store.get(sessionName)

    override fun set(sessionName: String, time: Long) {
        store.put(sessionName, time)
    }
}

class QuestSetupWizardSettingRepository(sharedPreferences: SharedPreferences) : SetupWizardBaseSettingsRepository(sharedPreferences),
        IQuestSetupWizardSettingRepository {


    override val skipAfterRightAnswer: Boolean = CONST.SKIP_AFTER_RIGHT_ANSWER
    override val timeForSkipAfterRightAnswer: Long = CONST.TIME_FOR_SKIP_AFTER_RIGHT_ANSWER


}

class QuestTrainingProgramRepository(private val context: Context,
                                     boxStore: BoxStore) :
        ObjectBoxSupport<QuestTrainingProgramEntity>(boxStore), IQuestTrainingProgramRepository {

    private val questPriorityRuleRepository = QuestTrainingProgramPriorityRuleRepository(context, boxStore)
    private val questTrainingProgramLevelRepository = QuestTrainingProgramLevelRepository(context, boxStore)

    override fun createBox(): Box<QuestTrainingProgramEntity> = initBox()

    override fun get(sex: PupilSex, complexity: Complexity): Single<Optional<QuestTrainingProgram>> =
            getEntity(sex, complexity).map { Optional(it.data?.let { Mapper.from(it) }) }

    private fun getEntity(sex: PupilSex, complexity: Complexity): Single<Optional<QuestTrainingProgramEntity>> =
            boxSingleUsingWithCallable {
                Optional(it.query().equal(QuestTrainingProgramEntity_.sex,
                        sex.asParameter()!!).equal(QuestTrainingProgramEntity_.complexity,
                        complexity.asParameter()!!).build().findUnique())
            }

    override fun removeAll() {
        super.removeAll()
        questPriorityRuleRepository.removeAll()
        questTrainingProgramLevelRepository.removeAll()
    }

    override fun getPriorityRule(sex: PupilSex,
                                 complexity: Complexity, questType: QuestType, questionType: QuestionType): Single<Optional<QuestTrainingProgramRulePriority>> =
            getEntity(sex, complexity).flatMap {
                if (it.data == null)
                    Single.just(null)
                else
                    questPriorityRuleRepository.get(it.nonNullData.id, questType, questionType)
            }

    override fun getAllPriorityRule(sex: PupilSex, complexity: Complexity): Single<List<QuestTrainingProgramRulePriority>> =
            getEntity(sex, complexity).flatMap { questPriorityRuleRepository.getAll(it.nonNullData.id) }


    override fun getAllLevels(sex: PupilSex, complexity: Complexity): Single<List<QuestTrainingProgramLevel>> = getEntity(sex, complexity).flatMap {
        if (it.data == null)
            Single.just(ArrayList())
        else
            questTrainingProgramLevelRepository.getAllLevels(it.nonNullData.id)
    }

    override fun getQuestRules(sex: PupilSex, complexity: Complexity, level: QuestTrainingProgramLevel): Single<List<QuestTrainingProgramRule>> =
            getRules(sex, complexity, level.index)

    private fun getRules(sex: PupilSex, complexity: Complexity, levelIndex: Int): Single<List<QuestTrainingProgramRule>> = getEntity(sex, complexity).flatMap {
        if (it.data == null)
            Single.just(ArrayList())
        else
            questTrainingProgramLevelRepository.getAllRules(it.nonNullData.id, levelIndex)
    }

    override fun getQuestRule(sex: PupilSex,
                              complexity: Complexity,
                              level: QuestTrainingProgramLevel,
                              questType: QuestType, questionType: QuestionType): Single<Optional<QuestTrainingProgramRule>> = getEntity(sex, complexity).flatMap {
        if (it.data == null)
            Single.just(null)
        else
            questTrainingProgramLevelRepository.getRule(it.nonNullData.id, level.index, questType, questionType)
    }

    override fun create(sex: PupilSex, complexity: Complexity, forceUpdate: Boolean): Single<Boolean> {
        val json = createJson()
        val jsonReader = createJsonReader(sex, complexity)
        val jo = createJsonObject(jsonReader)
        return getEntity(sex, complexity).flatMap {
            doFill(sex, complexity, it.data, jo, forceUpdate).flatMap { qtpEntity ->
                if (qtpEntity.isNotEmpty())
                    questPriorityRuleRepository.doFill(qtpEntity.nonNullData, json, jo).flatMap {
                        questTrainingProgramLevelRepository.doFill(qtpEntity.nonNullData, json, jo)
                    }
                else
                    false.toSingle()
            }
        }.doOnEvent { _, _ -> jsonReader.close() }
    }

    private fun createJson(): Gson = GsonBuilder().create()

    private fun createJsonReader(sex: PupilSex, complexity: Complexity): JsonReader {
        val qtpStream: InputStream = context.assets.open(QTP_FOLDER_NAME +
                "/" +
                getTrainingProgramResourcePath(sex, complexity) +
                "." +
                QTP_FILE_EXT)
        val inputStreamReader = InputStreamReader(qtpStream)
        return JsonReader(inputStreamReader)
    }

    private fun createJsonObject(jsonReader: JsonReader): JsonObject {
        return JsonParser().parse(jsonReader).asJsonObject
    }

    private fun doFill(sex: PupilSex,
                       complexity: Complexity,
                       qtpEntity: QuestTrainingProgramEntity?,
                       qtpJO: JsonObject, forceUpdate: Boolean): Single<Optional<QuestTrainingProgramEntity>> {
        val name = JsonHelper.readString(NAME, qtpJO)
        val version = JsonHelper.readFloat(VERSION, qtpJO)
        val description = JsonHelper.readString(DESCRIPTION, qtpJO)
        return boxSingleUsingWithCallable { box ->
            Optional(if (qtpEntity == null || qtpEntity.version != version || forceUpdate) {
                removeAll()
                QuestTrainingProgramEntity(
                        qtpEntity?.id ?: 0,
                        name,
                        version,
                        description,
                        sex,
                        complexity).also {
                    box.put(it)
                }
            } else
                null)

        }
    }

    override fun getLevel(sex: PupilSex, complexity: Complexity, index: Int): Single<Optional<QuestTrainingProgramLevel>> = getEntity(sex, complexity).flatMap {
        if (it.isEmpty())
            Single.just(Optional(null))
        else
            questTrainingProgramLevelRepository.getLevel(it.nonNullData.id, index)
    }

    object Mapper : IEntityMapper<QuestTrainingProgramEntity, QuestTrainingProgram> {

        override fun to(value: QuestTrainingProgram): QuestTrainingProgramEntity {
            with(value) {
                return QuestTrainingProgramEntity(
                        0, name, version, description, sex, complexity
                )
            }
        }

        override fun from(value: QuestTrainingProgramEntity): QuestTrainingProgram {
            with(value) {
                return QuestTrainingProgram(
                        name, version, description, sex, complexity
                )
            }
        }
    }

    companion object {
        internal const val QTP_FOLDER_NAME = "questTrainingProgramResources"
        internal const val QTP_FILE_EXT = "json"
        private const val QTP_FILE_BASE_NAME = "qtp"
        private const val QTP_FILE_NAME_SEPARATOR = "_"

        const val REWARD_BY_DEFAULT = 10
        const val ENABLED = "enabled"
        const val QUESTION_TYPES = "questionTypes"
        const val TYPES = "types"
        const val REWARD = "reward"
        const val MEMBER_COUNTS = "memberCounts"
        const val MEMBER_COUNT = "memberCount"
        const val ACCURACY = "accuracy"
        const val EACH_MEMBER_MIN_AND_MAX_VALUES = "eachMemberMinAndMaxValues"
        const val MEMBER_MIN_AND_MAX_VALUES = "memberMinAndMaxValues"
        const val FLAGS = "flags"
        const val ALL = "all"
        const val WORD_LENGTH = "wordLength"
        const val CAMOUFLAGE_LENGTH = "camouflageLength"
        const val DELAYED_PLAY = "delayedPlay"
        const val ANSWER_VARIANTS = "answerVariants"
        const val VERSION = "version"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val LEVELS = "levels"
        const val PRIORITY_RULES = "priorityRules"
        const val QUEST_TYPE = "questType"
        const val POINTS_MULTIPLIER = "pointsMultiplier"
        const val POINTS_WEIGHT = "pointsWeight"
        const val RULES = "quests"
        const val START_PRIORITY = "startPriority"
        const val WRONG_ANSWER_PRIORITY = "wrongAnswerPriority"

        internal fun getTrainingProgramResourcePath(sex: PupilSex, complexity: Complexity): String {
            val resourceNameBuilder = StringBuilder(QTP_FILE_BASE_NAME)
            resourceNameBuilder.append(QTP_FILE_NAME_SEPARATOR)
            resourceNameBuilder.append(sex.name.toLowerCase())
            resourceNameBuilder.append(QTP_FILE_NAME_SEPARATOR)
            resourceNameBuilder.append(complexity.name.toLowerCase())
            return resourceNameBuilder.toString()
        }

    }
}

class QuestTrainingProgramLevelRepository(context: Context, boxStore: BoxStore) :
        ObjectBoxSupport<QuestTrainingProgramLevelEntity>(boxStore) {

    override fun createBox(): Box<QuestTrainingProgramLevelEntity> = initBox()

    private val questTrainingProgramRuleRepositoryCollection = QuestTrainingProgramRuleRepositoryCollection(context, boxStore)

    override fun removeAll() {
        super.removeAll()
        questTrainingProgramRuleRepositoryCollection.removeAll()
    }

    fun getLevel(qtpId: Long, index: Int): Single<Optional<QuestTrainingProgramLevel>> =
            getLevelEntity(qtpId, index).map { Optional(if (it.data == null) null else Mapper.from(it.nonNullData)) }

    private fun getLevelEntity(qtpId: Long, index: Int): Single<Optional<QuestTrainingProgramLevelEntity>> =
            boxSingleUsingWithCallable {
                Optional(it.query().equal(QuestTrainingProgramLevelEntity_.qtpId, qtpId).equal(QuestTrainingProgramLevelEntity_.index, index.toLong()).build().findUnique())
            }

    fun getAllLevels(qtpId: Long): Single<List<QuestTrainingProgramLevel>> =
            boxSingleUsingWithCallable {
                it.query().equal(QuestTrainingProgramLevelEntity_.qtpId, qtpId).build().find().map { Mapper.from(it) }
            }

    fun getAllRules(qtpId: Long, index: Int): Single<List<QuestTrainingProgramRule>> =
            getLevelEntity(qtpId, index).flatMap {
                if (it.data == null)
                    Single.just(ArrayList())
                else
                    questTrainingProgramRuleRepositoryCollection.getAll(qtpId, it.nonNullData.id)
            }

    fun getRule(qtpId: Long, index: Int, questType: QuestType, questionType: QuestionType):
            Single<Optional<QuestTrainingProgramRule>> =
            getLevelEntity(qtpId, index).flatMap {
                if (it.data == null)
                    Single.just(Optional(null))
                else
                    questTrainingProgramRuleRepositoryCollection.get(qtpId, it.nonNullData.id, questType, questionType)
            }

    internal fun doFill(qtpEntity: QuestTrainingProgramEntity, json: Gson, qtpJO: JsonObject): Single<Boolean> {
        if (qtpJO.has(QuestTrainingProgramRepository.LEVELS)) {
            val levelJA = qtpJO.get(QuestTrainingProgramRepository.LEVELS).asJsonArray
            val actionList: MutableList<Single<Boolean>> = ArrayList()
            levelJA.mapIndexedTo(actionList) { index, item ->
                val levelJO = item.asJsonObject
                boxSingleUsingWithCallable { box ->
                    QuestTrainingProgramLevelEntity(0,
                            qtpEntity.id,
                            JsonHelper.readString(QuestTrainingProgramRepository.NAME, levelJO),
                            JsonHelper.readString(QuestTrainingProgramRepository.DESCRIPTION, levelJO),
                            index,
                            JsonHelper.readDouble(QuestTrainingProgramRepository.POINTS_MULTIPLIER, levelJO, 1.0),
                            JsonHelper.readInt(QuestTrainingProgramRepository.POINTS_WEIGHT, levelJO),
                            JsonHelper.readBooleanAsInt(QuestTrainingProgramRepository.DELAYED_PLAY, levelJO)
                    ).also {
                        box.put(it)
                    }
                }.flatMap { levelEntity ->
                    if (levelJO.has(QuestTrainingProgramRepository.RULES)) {
                        questTrainingProgramRuleRepositoryCollection.doFill(levelEntity, json, levelJO)
                    } else
                        false.toSingle()
                }
            }
            return Flowable.fromIterable(actionList).flatMapSingle { task -> task.subscribeOn(Schedulers.computation()) }.toList().map { true }
        } else
            return false.toSingle()
    }

    object Mapper : IEntityFromMapper<QuestTrainingProgramLevelEntity, QuestTrainingProgramLevel> {

        override fun from(value: QuestTrainingProgramLevelEntity): QuestTrainingProgramLevel {
            with(value) {
                return QuestTrainingProgramLevel(
                        name, description, index, pointsMultiplier, pointsWeight, delayedPlay
                )
            }
        }
    }
}

abstract class BaseQuestTrainingProgramRuleObjectBoxSupport<E : BaseQuestTrainingProgramRuleEntity,
        out R : QuestTrainingProgramRule>(boxStore: BoxStore) :
        ObjectBoxSupport<E>(boxStore), IEntityFromMapper<E, R> {

    private val qtpIdProperty = Property(1, 2, Long::class.javaPrimitiveType!!, "qtpId")
    private val levelIdProperty = Property(2, 3, Long::class.javaPrimitiveType!!, "levelId")
    private val enabledProperty = Property(7, 8, Boolean::class.javaPrimitiveType!!, "enabled")
    private val questTypeProperty = Property(3, 4, String::class.java, "questType", false, "questType", QuestTypeConverter::class.java, QuestType::class.java)
    private val questionTypeProperty = Property(4, 5, String::class.java, "questionTypes", false, "questionTypes", QuestionTypeListConverter::class.java, List::class.java)

    fun put(value: E, data: EntityBaseData) = boxUsing {
        applyRuleBaseData(value, data)
        it.put(value)
    }

    private fun applyRuleBaseData(value: E, data: EntityBaseData): BaseQuestTrainingProgramRuleEntity {
        with(data) {
            value.questType = questType
            value.questionTypes = questionTypes
            value.delayedPlay = delayedPlay
            value.qtpId = qtpId
            value.levelId = levelId
            value.enabled = enabled
            value.reward = reward
        }
        return value
    }

    fun get(qtpId: Long, levelId: Long): List<R> = boxUsing {
        it.query().equal(qtpIdProperty, qtpId).equal(levelIdProperty, levelId).equal(enabledProperty, true).build().find().map {
            from(it)
        }
    }

    fun getByQuestAndQuestionType(qtpId: Long, levelId: Long, questType: QuestType, questionType: QuestionType): R? = boxUsing {
        val result = it.query().equal(qtpIdProperty, qtpId).equal(levelIdProperty, levelId).equal(enabledProperty, true).equal(questTypeProperty, questType.asParameter()).contains(questionTypeProperty, questionType.asParameter()).build().findUnique()
        result?.let { from(it) }
    }

    fun getAll(): List<R> = boxUsing {
        it.all.map { from(it) }
    }

    abstract fun mapFunction(value: E): R

    override fun from(value: E): R {
        with(value) {
            val result = mapFunction(this)
            result.delayedPlay = delayedPlay
            result.questType = questType
            result.questionTypes = value.questionTypes
            result.reward = reward
            return result
        }
    }

    data class EntityBaseData(val questType: QuestType,
                              val questionTypes: List<QuestionType>,
                              val qtpId: Long,
                              val levelId: Long,
                              val delayedPlay: Int,
                              val enabled: Boolean,
                              val reward: Int)

}

class MemberCountQuestTrainingProgramRuleRepository(boxStore: BoxStore) :
        BaseQuestTrainingProgramRuleObjectBoxSupport<MemberCountQuestTrainingProgramRuleEntity,
                MemberCountQuestTrainingRule>(boxStore) {

    override fun createBox(): Box<MemberCountQuestTrainingProgramRuleEntity> = initBox()

    override fun mapFunction(value: MemberCountQuestTrainingProgramRuleEntity) = MemberCountQuestTrainingRule(value.memberCount)

}

class SimpleTrainingProgramRuleRepository(boxStore: BoxStore) :
        BaseQuestTrainingProgramRuleObjectBoxSupport<SimpleTrainingProgramRuleEntity,
                SimpleTrainingProgramRule>(boxStore) {

    override fun createBox(): Box<SimpleTrainingProgramRuleEntity> = initBox()

    override fun mapFunction(value: SimpleTrainingProgramRuleEntity) = SimpleTrainingProgramRule()

}

class TimeQuestTrainingProgramRuleRepository(boxStore: BoxStore) :
        BaseQuestTrainingProgramRuleObjectBoxSupport<TimeQuestTrainingProgramRuleEntity, TimeQuestTrainingProgramRule>(boxStore) {

    override fun createBox(): Box<TimeQuestTrainingProgramRuleEntity> = initBox()

    override fun mapFunction(value: TimeQuestTrainingProgramRuleEntity) = TimeQuestTrainingProgramRule(value.memberCount, value.accuracy)
}


class FruitArithmeticQuestTrainingProgramRuleRepository(boxStore: BoxStore) :
        BaseQuestTrainingProgramRuleObjectBoxSupport<FruitArithmeticQuestTrainingProgramRuleEntity,
                FruitArithmeticQuestTrainingProgramRule>(boxStore) {

    override fun createBox(): Box<FruitArithmeticQuestTrainingProgramRuleEntity> = initBox()

    override fun mapFunction(value: FruitArithmeticQuestTrainingProgramRuleEntity) =
            FruitArithmeticQuestTrainingProgramRule(value.memberCount, value.answerVariants,
                    value.memberMinAndMaxValues ?: ArrayList<ArrayList<Int>>())

}

class ChoiceQuestTrainingProgramRuleRepository(boxStore: BoxStore) :
        BaseQuestTrainingProgramRuleObjectBoxSupport<ChoiceQuestTrainingProgramRuleEntity, ChoiceQuestTrainingProgramRule>(boxStore) {

    override fun createBox(): Box<ChoiceQuestTrainingProgramRuleEntity> = initBox()

    override fun mapFunction(value: ChoiceQuestTrainingProgramRuleEntity) =
            ChoiceQuestTrainingProgramRule(value.types ?: ArrayList())

}

class QuestTrainingProgramRuleRepositoryCollection(private val context: Context, boxStore: BoxStore) {

    private val memberCountQuestTrainingRuleRepository = MemberCountQuestTrainingProgramRuleRepository(boxStore)
    private val simpleTrainingProgramRuleRepository = SimpleTrainingProgramRuleRepository(boxStore)
    private val timeQuestTrainingProgramRuleRepository = TimeQuestTrainingProgramRuleRepository(boxStore)
    private val fruitArithmeticQuestTrainingProgramRuleRepository = FruitArithmeticQuestTrainingProgramRuleRepository(boxStore)
    private val choiceQuestTrainingProgramRuleRepository = ChoiceQuestTrainingProgramRuleRepository(boxStore)


    fun removeAll() {
        memberCountQuestTrainingRuleRepository.removeAll()
        simpleTrainingProgramRuleRepository.removeAll()
        timeQuestTrainingProgramRuleRepository.removeAll()
        fruitArithmeticQuestTrainingProgramRuleRepository.removeAll()
        choiceQuestTrainingProgramRuleRepository.removeAll()
    }

    fun getAll(qtpId: Long, levelId: Long): Single<List<QuestTrainingProgramRule>> =
            Single.fromCallable {
                val result: MutableList<QuestTrainingProgramRule> = ArrayList()
                result.addAll(memberCountQuestTrainingRuleRepository.get(qtpId, levelId))
                result.addAll(simpleTrainingProgramRuleRepository.get(qtpId, levelId))
                result.addAll(timeQuestTrainingProgramRuleRepository.get(qtpId, levelId))
                result.addAll(fruitArithmeticQuestTrainingProgramRuleRepository.get(qtpId, levelId))
                result.addAll(choiceQuestTrainingProgramRuleRepository.get(qtpId, levelId))
                result.toList()
            }

    fun get(qtpId: Long, levelId: Long, questType: QuestType, questionType: QuestionType): Single<Optional<QuestTrainingProgramRule>> =
            Single.just(
                    Optional(when (questType) {

                        COINS,
                        COLORS ->
                            memberCountQuestTrainingRuleRepository.getByQuestAndQuestionType(qtpId,
                                    levelId,
                                    questType,
                                    questionType)

                        TRAFFIC_LIGHT,
                        CURRENT_SEASON,
                        DIRECTION ->
                            simpleTrainingProgramRuleRepository.getByQuestAndQuestionType(qtpId,
                                    levelId,
                                    questType,
                                    questionType)

                        TIME,
                        CURRENT_TIME ->
                            timeQuestTrainingProgramRuleRepository.getByQuestAndQuestionType(qtpId,
                                    levelId,
                                    questType,
                                    questionType)

                        FRUIT_ARITHMETIC ->
                            fruitArithmeticQuestTrainingProgramRuleRepository.getByQuestAndQuestionType(qtpId,
                                    levelId,
                                    questType,
                                    questionType)

                        MISMATCH,
                        CHOICE ->
                            choiceQuestTrainingProgramRuleRepository.getByQuestAndQuestionType(qtpId,
                                    levelId,
                                    questType,
                                    questionType)

                        QuestType.SIMPLE_EXAMPLE,
                        QuestType.METRICS,
                        QuestType.PERIMETER,
                        QuestType.TEXT_CAMOUFLAGE -> TODO()
                    })
            )


    internal fun doFill(levelEntity: QuestTrainingProgramLevelEntity, json: Gson, levelJO: JsonObject): Single<Boolean> {
        val rulesJA = levelJO.get(QuestTrainingProgramRepository.RULES).asJsonArray
        rulesJA.forEach { item ->
            val ruleJO = item as JsonObject
            val questTypeNameOrSynonym = JsonHelper.readString(QuestTrainingProgramRepository.QUEST_TYPE, ruleJO)
            val questType = ktQuestTypeSupport.getByNameOrSynonym(context, questTypeNameOrSynonym)
            if (questType != null) {
                val questionTypes: MutableList<QuestionType>
                if (ruleJO.has(QuestTrainingProgramRepository.QUESTION_TYPES)) {
                    val questionTypesJArray = ruleJO.get(QuestTrainingProgramRepository.QUESTION_TYPES).asJsonArray
                    if (questionTypesJArray.size() == 1 && QuestTrainingProgramRepository.ALL.toLowerCase()
                            == questionTypesJArray.get(0).asString.toLowerCase()) {
                        questionTypes = questType.supportQuestionTypes.toMutableList()
                    } else {
                        questionTypes = ArrayList()
                        json.fromJson(questionTypesJArray, Array<String>::class.java).forEach {
                            questionTypes.add(QuestionType.valueOf(it.toUpperCase()))
                        }
                    }
                } else {
                    questionTypes = arrayListOf(questType.defaultQuestionType)
                }
                val ruleBaseData = BaseQuestTrainingProgramRuleObjectBoxSupport.EntityBaseData(questType,
                        questionTypes,
                        levelEntity.qtpId,
                        levelEntity.id,
                        JsonHelper.readBooleanAsInt(QuestTrainingProgramRepository.DELAYED_PLAY, ruleJO),
                        JsonHelper.readBoolean(QuestTrainingProgramRepository.ENABLED, ruleJO, true),
                        JsonHelper.readInt(QuestTrainingProgramRepository.REWARD, ruleJO, QuestTrainingProgramRepository.REWARD_BY_DEFAULT)
                )
                when (questType) {

                    COINS,
                    COLORS -> memberCountQuestTrainingRuleRepository.put(
                            MemberCountQuestTrainingProgramRuleEntity(JsonHelper.readInt(QuestTrainingProgramRepository.MEMBER_COUNT, ruleJO, -1)),
                            ruleBaseData)

                    TRAFFIC_LIGHT,
                    CURRENT_SEASON,
                    DIRECTION -> simpleTrainingProgramRuleRepository.put(SimpleTrainingProgramRuleEntity(), ruleBaseData)

                    TIME,
                    CURRENT_TIME -> timeQuestTrainingProgramRuleRepository.put(
                            TimeQuestTrainingProgramRuleEntity(
                                    JsonHelper.readInt(QuestTrainingProgramRepository.MEMBER_COUNT, ruleJO, -1),
                                    JsonHelper.readInt(QuestTrainingProgramRepository.ACCURACY, ruleJO, -1)),
                            ruleBaseData)

                    FRUIT_ARITHMETIC -> fruitArithmeticQuestTrainingProgramRuleRepository.put(
                            FruitArithmeticQuestTrainingProgramRuleEntity(
                                    JsonHelper.readInt(QuestTrainingProgramRepository.MEMBER_COUNT, ruleJO, -1),
                                    JsonHelper.readInt(QuestTrainingProgramRepository.ANSWER_VARIANTS, ruleJO, -1),
                                    JsonHelper.readListListOfInt(QuestTrainingProgramRepository.MEMBER_MIN_AND_MAX_VALUES, json, ruleJO)
                            ), ruleBaseData)

                    MISMATCH,
                    CHOICE -> choiceQuestTrainingProgramRuleRepository.put(
                            ChoiceQuestTrainingProgramRuleEntity(JsonHelper.readStringListWithConverter(QuestTrainingProgramRepository.TYPES, json, ruleJO) {
                                ResourceGroupCollection.valueOf(it.toUpperCase()).id
                            }), ruleBaseData)

                    QuestType.SIMPLE_EXAMPLE,
                    QuestType.METRICS,
                    QuestType.PERIMETER,
                    QuestType.TEXT_CAMOUFLAGE -> null
                }
            }
        }
        return true.toSingle()
    }
}

class QuestTrainingProgramPriorityRuleRepository(private val context: Context, boxStore: BoxStore) : ObjectBoxSupport<QuestTrainingProgramPriorityRuleEntity>(boxStore) {

    override fun createBox(): Box<QuestTrainingProgramPriorityRuleEntity> = initBox()

    fun getAll(qtpId: Long): Single<List<QuestTrainingProgramRulePriority>> =
            boxSingleUsingWithCallable {
                it.query().equal(QuestTrainingProgramPriorityRuleEntity_.qtpId, qtpId).build().find().map { Mapper.from(it) }
            }

    fun get(qtpId: Long, questType: QuestType, questionType: QuestionType): Single<Optional<QuestTrainingProgramRulePriority>> =
            boxSingleUsingWithCallable {
                val result = it.query().equal(QuestTrainingProgramPriorityRuleEntity_.qtpId,
                        qtpId).equal(QuestTrainingProgramPriorityRuleEntity_.questType,
                        questType.asParameter()).contains(QuestTrainingProgramPriorityRuleEntity_.questionTypes,
                        questionType.asParameter()).build().findUnique()
                Optional(result?.let { Mapper.from(it) })
            }

    internal fun doFill(qtpEntity: QuestTrainingProgramEntity, json: Gson, qtpJO: JsonObject): Single<Boolean> =
            boxSingleUsingWithCallable {
                if (qtpJO.has(QuestTrainingProgramRepository.PRIORITY_RULES)) {
                    val qtpRuleJA = qtpJO.get(QuestTrainingProgramRepository.PRIORITY_RULES).asJsonArray
                    qtpRuleJA.forEach { item ->
                        val questPriorityJO = item.asJsonObject
                        val questionTypesJA = if (questPriorityJO.has(QuestTrainingProgramRepository.QUESTION_TYPES))
                            questPriorityJO.get(QuestTrainingProgramRepository.QUESTION_TYPES).asJsonArray else null
                        val questType = ktQuestTypeSupport.getByNameOrSynonym(context,
                                json.fromJson(questPriorityJO.get(
                                        QuestTrainingProgramRepository.QUEST_TYPE),
                                        String::class.java))
                        if (questType != null) {
                            val questionTypes: ArrayList<QuestionType> = ArrayList()
                            if (questionTypesJA != null) {
                                if (questionTypesJA.size() == 1 &&
                                        QuestTrainingProgramRepository.ALL == questionTypesJA[0].asString.toLowerCase()) {
                                    questionTypes.addAll(questType.supportQuestionTypes.toList())
                                } else {
                                    questionTypesJA.mapTo(questionTypes) {
                                        json.fromJson(it.asJsonObject, QuestionType::class.java)
                                    }
                                }
                            } else {
                                questionTypes.addAll(questType.supportQuestionTypes.toList())
                            }
                            it.put(QuestTrainingProgramPriorityRuleEntity(0,
                                    qtpEntity.id,
                                    questType,
                                    questionTypes,
                                    questPriorityJO.get(QuestTrainingProgramRepository.START_PRIORITY).asFloat.toDouble(),
                                    questPriorityJO.get(QuestTrainingProgramRepository.WRONG_ANSWER_PRIORITY).asFloat.toDouble()
                            ))
                        }
                    }
                    true
                } else
                    false
            }

    object Mapper : IEntityFromMapper<QuestTrainingProgramPriorityRuleEntity, QuestTrainingProgramRulePriority> {

        override fun from(value: QuestTrainingProgramPriorityRuleEntity): QuestTrainingProgramRulePriority {
            with(value) {
                return QuestTrainingProgramRulePriority(
                        questType!!, questionTypes!!, startPriority, wrongAnswerPriority
                )
            }
        }
    }
}

class PupilStatisticsRepository(repository: IRepositoryHolder, boxStore: BoxStore) : ObjectBoxSupport<PupilStatisticsEntity>(boxStore),
        IPupilStatisticsRepository {

    override fun createBox(): Box<PupilStatisticsEntity> = initBox()

    private val pupilRepository = PupilRepository(repository, boxStore)

    private fun getEntity(pupilId: Long) =
            boxSingleUsingWithCallable {
                Optional(it.query().equal(PupilStatisticsEntity_.pupilId, pupilId).build().findUnique())
            }

    override fun create(pupil: Pupil): Single<Boolean> = pupilRepository.getEntityId(pupil).flatMap { pupilEntityId ->
        getEntity(pupilEntityId).flatMap { entity ->
            if (entity.isEmpty())
                boxSingleUsingWithCallable {
                    it.put(PupilStatisticsEntity(0, pupilEntityId, 0))
                    true
                }
            else
                false.toSingle()
        }
    }

    override fun get(pupil: Pupil): Single<PupilStatistics> =
            pupilRepository.getEntityId(pupil).flatMap { getEntity(it) }.map { Mapper.from(it.nonNullData) }

    override fun update(pupil: Pupil, statistics: PupilStatistics): Completable =
            pupilRepository.getEntityId(pupil).flatMap { getEntity(it) }.map { it.data }.flatMapCompletable { inEntity ->
                boxCompletableUsingFromRunnable {
                    with(Mapper.to(statistics)) {
                        id = inEntity.id
                        pupilId = inEntity.pupilId
                        it.put(this)
                    }
                }
            }

    object Mapper : IEntityMapper<PupilStatisticsEntity, PupilStatistics> {

        override fun to(value: PupilStatistics): PupilStatisticsEntity {
            with(value) {
                return PupilStatisticsEntity(
                        0, 0, score
                )
            }
        }

        override fun from(value: PupilStatisticsEntity): PupilStatistics {
            with(value) {
                return PupilStatistics(
                        score
                )
            }
        }
    }

}

class QuestStateRepository(sharedPreferences: SharedPreferences) : IQuestStateRepository {

    private fun set(value: Int): Completable = Completable.fromRunnable { store.put(value) }

    private fun get(): Single<Int> = Single.fromCallable { store.get() }

    override fun has(value: QuestState): Single<Boolean> = get().map {
        (it and value.value) != 0
    }

    override fun add(value: QuestState): Completable = get().flatMapCompletable {
        set(value.value or it)
    }

    override fun clear(): Completable = Completable.fromCallable { store.put(0) }

    override fun remove(value: QuestState): Completable = Completable.fromRunnable {
        store.put(store.get() and value.value.inv())
    }

    override fun replace(oldValue: QuestState, newValue: QuestState): Completable =
            remove(oldValue).concatWith(add(newValue))

    private val store = IntSingleKeyValueStore(NAME, sharedPreferences)

    companion object {
        const val NAME = "QuestState"
    }

}

class LockScreenRepository(sharedPreferences: SharedPreferences) : ILockScreenRepository {

    private val booleanStore: BooleanKeyValueStore = BooleanKeyValueStore(sharedPreferences)
    private val stringStore: ReactiveStringKeyStringValueStore = ReactiveStringKeyStringValueStore(sharedPreferences)

    override fun switchOn(value: Boolean) = booleanStore.put(IS_ON, value)

    override fun isSwitchedOn(): Boolean = booleanStore.get(IS_ON)

    override fun incomeCallInProcess(value: Boolean) =
            booleanStore.put(INCOME_CALL, value)

    override fun incomeCallInProcess(): Boolean =
            booleanStore.get(INCOME_CALL, false)

    override fun outgoingCallInProcess(value: Boolean) =
            booleanStore.put(OUTGOING_CALL, value)

    override fun outgoingCallInProcess(): Boolean =
            booleanStore.get(OUTGOING_CALL, false)

    override fun getLastStartType(): Single<Optional<LockScreenStartType>> = stringStore.get(START_TYPE).map {
        Optional(if (it.isEmpty()) null else LockScreenStartType.valueOf(it.nonNullData))
    }

    override fun saveStartType(value: LockScreenStartType) = stringStore.put(START_TYPE, value.name)

    companion object {

        private const val START_TYPE = "ru.nekit.android.qls.start_type"
        private const val IS_ON = "ru.nekit.android.qls.is_on"
        private const val INCOME_CALL = "ru.nekit.android.qls.income_call"
        private const val OUTGOING_CALL = "ru.nekit.android.qls.outgoing_call"

    }

}

abstract class TransitionChoreographRepository(sharedPreferences: SharedPreferences) : ITransitionChoreographRepository {

    override val introductionIsPresented: Boolean = CONST.INTRODUCTION_IS_PRESENTED_BY_DEFAULT
    override val advertIsPresented: Boolean = CONST.ADVERT_IS_PRESENTED_BY_DEFAULT
    override val advertStartValue: Int = CONST.SHOW_ADVERT_AFTER_N_RIGHT_ANSWER

    private val booleanStore: BooleanKeyValueStore = BooleanKeyValueStore(sharedPreferences)
    private val stringStore: StringKeyStringValueStore = StringKeyStringValueStore(sharedPreferences)

    override fun introductionWasShown(value: Boolean) =
            booleanStore.put(INTRODUCTION.name, value)

    override fun introductionWasShown(): Boolean =
            booleanStore.get(INTRODUCTION.name, false)

    override fun setTransition(type: Transition.Type, transition: Transition?) =
            stringStore.put(type.name, transition?.name ?: Transition.EMPTY_TRANSITION)

    override fun getTransition(type: Transition.Type): Transition? =
            Transition.getByName(stringStore.get(type.name))

    override fun advertWasShown(value: Boolean) =
            booleanStore.put(ADVERT.name, value)

    override fun advertWasShown(): Boolean = booleanStore.get(ADVERT.name)

}

class QuestHistoryCriteriaRepository : IQuestHistoryCriteriaRepository {

    override fun getQuestHistoryCriteria(reward: Reward,
                                         rewardVariant: IRewardVariant,
                                         questAndQuestionType: QuestAndQuestionType?):
            List<QuestHistoryCriteria>? =

            when (reward) {
                is Reward.UnlockKey ->
                    when (rewardVariant) {
                        ReachVariant.RightSeries -> listOf(
                                QuestHistoryCriteria(
                                        true,
                                        rewards = listOf(Reward.UnlockKey(ReachVariant.RightSeries))
                                )
                                ,
                                QuestHistoryCriteria()
                        )
                        ReachVariant.Independence -> listOf(
                                QuestHistoryCriteria(
                                        true,
                                        answerType = AnswerType.RIGHT,
                                        rewards = listOf(Reward.UnlockKey(ReachVariant.Independence)),
                                        lockScreenStartType = LockScreenStartType.ON_NOTIFICATION_CLICK
                                )
                                ,
                                QuestHistoryCriteria(
                                        answerType = AnswerType.RIGHT,
                                        lockScreenStartType = LockScreenStartType.ON_NOTIFICATION_CLICK
                                )
                        )
                        else -> null
                    }

                is Reward.Medal -> {
                    listOf(
                            QuestHistoryCriteria(
                                    limitByLastItem = true,
                                    questType = questAndQuestionType?.questType,
                                    questionType = questAndQuestionType?.questionType,
                                    rewards = listOf(Reward.Medal())
                            )
                            ,
                            QuestHistoryCriteria(
                                    questType = questAndQuestionType?.questType,
                                    questionType = questAndQuestionType?.questionType)
                    )
                }
                is Reward.Achievement ->
                    when (rewardVariant) {
                        is IRewardVariantWithQuestAndQuestionType ->
                            listOf(
                                    QuestHistoryCriteria(answerType = AnswerType.RIGHT,
                                            questType = questAndQuestionType?.questType,
                                            questionType = questAndQuestionType?.questionType)
                            )
                        else ->
                            listOf(
                                    QuestHistoryCriteria(answerType = AnswerType.RIGHT)
                            )
                    }
            }
}

object CONST {

    internal const val SHOW_ADVERT_AFTER_N_RIGHT_ANSWER = 2
    internal const val INTRODUCTION_IS_PRESENTED_BY_DEFAULT = false
    internal const val ADVERT_IS_PRESENTED_BY_DEFAULT = true
    internal const val SKIP_AFTER_RIGHT_ANSWER = true
    internal const val TIME_FOR_SKIP_AFTER_RIGHT_ANSWER: Long = 60 * 1000

}