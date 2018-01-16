package ru.nekit.android.qls.data.repository

import android.content.SharedPreferences
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.query.Query
import io.objectbox.rx.RxQuery
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.nekit.android.data.ObjectBoxSupport
import ru.nekit.android.data.StringKeyValueStore
import ru.nekit.android.data.shared.SetupWizardBaseSettingsRepository
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.domain.qls.model.ktPupil
import ru.nekit.android.domain.qls.model.ktQuestStatisticsReport
import ru.nekit.android.domain.qls.repository.ICurrentPupilRepository
import ru.nekit.android.domain.qls.repository.IPupilRepository
import ru.nekit.android.domain.qls.repository.IQuestStatisticsReportRepository
import ru.nekit.android.domain.repository.EntityMapper
import ru.nekit.android.domain.shared.model.ktQuestType
import ru.nekit.android.domain.shared.model.ktQuestionType
import ru.nekit.android.qls.data.entity.*

class PupilRepository(boxStore: BoxStore, private val currentPupilRepository: ICurrentPupilRepository) :
        ObjectBoxSupport<ktPupilEntity>(boxStore),
        IPupilRepository {

    override fun createBox(): Box<ktPupilEntity> = initBox()

    override fun getCurrentPupil(): Single<Optional<ktPupil>> =
            currentPupilRepository.getCurrentUuid().flatMap {
                read(it.data!!)
            }

    override fun setCurrentPupil(pupil: ktPupil): Completable =
            currentPupilRepository.setCurrentUuid(pupil.uuid)

    override fun create(value: ktPupil): Completable =
            boxCompletableUsing { box ->
                read(value.uuid).flatMapCompletable {
                    Completable.fromCallable {
                        box.put(Mapper.to(value))
                    }
                }
            }

    override fun read(value: String): Single<Optional<ktPupil>> =
            readInternal(value).map { Optional(if (it.data == null) null else from(it.data!!)) }

    private fun readInternal(value: String): Single<Optional<ktPupilEntity>> =
            boxSingleUsingWithCallable {
                Optional(it.queryByUuid(value).findFirst())
            }

    @Throws(PupilIsNotExist::class)
    override fun update(value: ktPupil): Completable =
            boxCompletableUsing { box ->
                readInternal(value.uuid).flatMapCompletable { entity ->
                    if (entity.data == null) {
                        Completable.error(PupilIsNotExist())
                    } else
                        Completable.fromCallable {
                            box.put(Mapper.to(value).also {
                                with(entity.data!!) {
                                    //non updatable
                                    it.uuid = uuid
                                    it.id = id
                                }
                            })
                        }
                }
            }

    override fun delete(value: ktPupil): Completable =
            boxCompletableUsingFromCallable {
                it.queryByUuid(value.uuid).remove()
            }

    private fun Box<ktPupilEntity>.queryByUuid(uuid: String): Query<ktPupilEntity> = queryBy(ktPupilEntity_.uuid, uuid)

    companion object Mapper : EntityMapper<ktPupilEntity, ktPupil> {
        override fun from(value: ktPupilEntity): ktPupil {
            with(value) {
                return ktPupil(
                        uuid!!,
                        name!!,
                        sex!!,
                        complexity!!,
                        avatar!!
                )
            }
        }

        override fun to(value: ktPupil): ktPupilEntity {
            with(value) {
                return ktPupilEntity(
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

    class PupilIsNotExist : Throwable("Pupil in not exist")
}

open class CurrentPupilRepository(sharedPreferences: SharedPreferences) : StringKeyValueStore(sharedPreferences),
        ICurrentPupilRepository {

    companion object NAMES {
        private val CURRENT: String = "current_pupil"
    }

    override fun setCurrentUuid(pupilUuid: String): Completable = Completable.fromCallable {
        put(CURRENT, pupilUuid)
    }


    override fun getCurrentUuid(): Single<Optional<String>> = Single.just(Optional(get(CURRENT)))

}


class ObjectBoxQuestStatisticsReportRepository(boxStore: BoxStore) :
        ObjectBoxSupport<ktObjectBoxQuestStatisticsReportEntity>(boxStore),
        IQuestStatisticsReportRepository {

    override fun createBox(): Box<ktObjectBoxQuestStatisticsReportEntity> = initBox()

    override fun fetchOrCreate(pupil: ktPupil, questType: ktQuestType, questionType: ktQuestionType): Single<ktQuestStatisticsReport> =
            internalFetchBy(pupil.uuid, questType, questionType).flatMap { entity ->
                Single.just(
                        if (entity.data == null)
                            ktQuestStatisticsReport(
                                    pupil.uuid,
                                    questType,
                                    questionType,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0)
                        else
                            from(entity.data!!)
                )
            }

    override fun updateOrCreate(report: ktQuestStatisticsReport): Completable =
            internalFetchBy(report.pupilUuid, report.questType, report.questionType).flatMapCompletable { entity ->
                boxCompletableUsing {
                    Completable.fromCallable {
                        it.put(if (entity.data == null) {
                            Mapper.to(report)
                        } else {
                            with(entity.data!!) {
                                rightAnswerCount = report.rightAnswerCount
                                rightAnswerSeriesCounter = report.rightAnswerSeriesCounter
                                wrongAnswerCount = report.wrongAnswerCount
                                bestAnswerTime = report.bestAnswerTime
                                worseAnswerTime = report.worseAnswerTime
                                rightAnswerSummandTime = report.rightAnswerSummandTime
                            }
                            entity.data!!
                        })
                    }
                }
            }

    private fun Box<ktObjectBoxQuestStatisticsReportEntity>.queryBy(pupilUuid: String,
                                                                    questType: ktQuestType? = null,
                                                                    questionType: ktQuestionType? = null): Query<ktObjectBoxQuestStatisticsReportEntity> =
            query().equal(ktObjectBoxQuestStatisticsReportEntity_.pupilUuid, pupilUuid).also {
                if (questType != null)
                    it.equal(ktObjectBoxQuestStatisticsReportEntity_.questType, questType.asParameter())
            }.also {
                if (questionType != null)
                    it.equal(ktObjectBoxQuestStatisticsReportEntity_.questionType, questionType.asParameter())
            }.build()

    override fun fetchAsyncBy(pupil: ktPupil, questType: ktQuestType, questionType: ktQuestionType): Single<Optional<ktQuestStatisticsReport>> =
            boxSingleUsing {
                RxQuery.flowableOneByOne(it.queryBy(pupil.uuid, questType, questionType)).map { Optional(from(it)) }.first(Optional())
            }

    override fun listenForChanges(pupil: ktPupil, questType: ktQuestType, questionType: ktQuestionType): Flowable<Optional<ktQuestStatisticsReport>> {
        throw UnsupportedOperationException("not supported")
    }

    private fun internalFetchBy(pupilUuid: String, questType: ktQuestType, questionType: ktQuestionType): Single<Optional<ktObjectBoxQuestStatisticsReportEntity>> =
            boxSingleUsing {
                Single.just(Optional(it.queryBy(pupilUuid, questType, questionType).findFirst()))
            }

    override fun fetchBy(pupil: ktPupil, questType: ktQuestType, questionType: ktQuestionType): Single<Optional<ktQuestStatisticsReport>> =
            internalFetchBy(pupil.uuid, questType, questionType).map { Optional(if (it.data == null) null else from(it.data!!)) }

    override fun fetchAll(pupil: ktPupil): Single<List<ktQuestStatisticsReport>> {
        val entityBox = createBox()
        return RxQuery.flowableOneByOne(entityBox.queryBy(pupil.uuid))
                .map { from(it) }
                .toList().doOnEvent { _, _ -> entityBox.closeThreadResources() }
    }

    companion object Mapper : EntityMapper<ktObjectBoxQuestStatisticsReportEntity, ktQuestStatisticsReport> {

        override fun to(value: ktQuestStatisticsReport): ktObjectBoxQuestStatisticsReportEntity {
            with(value) {
                return ktObjectBoxQuestStatisticsReportEntity(
                        0,
                        pupilUuid,
                        questType,
                        questionType,
                        rightAnswerCount,
                        rightAnswerSeriesCounter,
                        wrongAnswerCount,
                        bestAnswerTime,
                        worseAnswerTime,
                        rightAnswerSummandTime
                )
            }
        }

        override fun from(value: ktObjectBoxQuestStatisticsReportEntity): ktQuestStatisticsReport {
            with(value) {
                return ktQuestStatisticsReport(
                        pupilUuid!!,
                        questType!!,
                        questionType!!,
                        rightAnswerCount,
                        rightAnswerSeriesCounter,
                        wrongAnswerCount,
                        bestAnswerTime,
                        worseAnswerTime,
                        rightAnswerSummandTime
                )
            }
        }
    }
}

class QuestSetupWizardSettingsRepository(sharedPreferences: SharedPreferences) : SetupWizardBaseSettingsRepository(sharedPreferences) {

}

/*
class RealmQuestStatisticsReportRepository : IQuestStatisticsReportRepository {

    override fun fetchOrCreate(pupil: ktPupil, questType: ktQuestType, questionType: ktQuestionType): Single<ktQuestStatisticsReport> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listenForChanges(
            pupil: ktPupil,
            questType: ktQuestType,
            questionType: ktQuestionType
    ): Flowable<Optional<ktQuestStatisticsReport>> {
        return Flowable.using(
                { Realm.getDefaultInstance() },
                { realm ->
                    realm.where(ktQuestStatisticsReportEntity::class.java).equalTo(ktQuestStatisticsReportEntity.KeyFormatter.KEY,
                            ktQuestStatisticsReportEntity.KeyFormatter.format(pupil.uuid, questType, questionType))
                            .findFirstAsync()
                            .asFlowable<ktQuestStatisticsReportEntity>()
                            .flatMap {
                                Flowable.just(Optional(
                                        if (it.isValid && it.isLoaded)
                                            Mapper.from(realm.copyFromRealm(it))
                                        else
                                            null
                                ))
                            }
                },
                {
                    it.close()
                })
    }

    override fun fetchAll(pupil: ktPupil): Single<List<ktQuestStatisticsReport>> {
        return ktQuestStatisticsReportEntity().queryAsSingle { it ->
            it.equalTo(ktQuestStatisticsReportEntity.KeyFormatter.PUPIL_UUID, pupil.uuid)
        }.toFlowable().flatMap { Flowable.fromIterable(it) }.map { Mapper.from(it) }.toList()
    }

    override fun fetchAsyncBy(
            pupil: ktPupil,
            questType: ktQuestType,
            questionType: ktQuestionType
    ): Single<Optional<ktQuestStatisticsReport>> {
        return ktQuestStatisticsReportEntity().queryAsSingle { query ->
            query.equalTo(ktQuestStatisticsReportEntity.KeyFormatter.KEY,
                    ktQuestStatisticsReportEntity.KeyFormatter.format(pupil.uuid, questType, questionType))
        }.map { it -> Optional(if (it.isEmpty()) null else Mapper.from(it[0])) }
    }

    override fun updateOrCreate(report: ktQuestStatisticsReport): Completable {
        return Completable.fromCallable {
            Mapper.to(report).createOrUpdate()
        }
    }

    override fun fetchBy(
            pupil: ktPupil,
            questType: ktQuestType,
            questionType: ktQuestionType
    ): Single<Optional<ktQuestStatisticsReport>> {
        val realm = Realm.getDefaultInstance()
        return Single.fromCallable {
            val entity = realm.where(ktQuestStatisticsReportEntity::class.java).equalTo(ktQuestStatisticsReportEntity.KeyFormatter.KEY,
                    ktQuestStatisticsReportEntity.KeyFormatter.format(pupil.uuid, questType, questionType))
                    .findFirst()
            Optional(if (entity == null) null else Mapper.from(entity))
        }.doOnEvent { _, _ ->
            realm.close()
        }
    }

    companion object Mapper : EntityMapper<ktQuestStatisticsReportEntity, ktQuestStatisticsReport> {
        override fun from(value: ktQuestStatisticsReportEntity): ktQuestStatisticsReport {
            with(value) {
                return ktQuestStatisticsReport(
                        pupilUuid!!,
                        questType,
                        questionType,
                        rightAnswerCount!!,
                        rightAnswerSeriesCounter!!,
                        wrongAnswerCount!!,
                        bestAnswerTime!!,
                        worseAnswerTime!!,
                        rightAnswerSummandTime!!
                )
            }
        }

        override fun to(value: ktQuestStatisticsReport): ktQuestStatisticsReportEntity {
            with(value) {
                return ktQuestStatisticsReportEntity(
                        pupilUuid,
                        questType,
                        questionType,
                        rightAnswerCount,
                        rightAnswerSeriesCounter,
                        wrongAnswerCount,
                        bestAnswerTime,
                        worseAnswerTime,
                        rightAnswerSummandTime
                )
            }
        }
    }
}*/