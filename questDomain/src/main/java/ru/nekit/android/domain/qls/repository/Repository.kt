package ru.nekit.android.domain.qls.repository

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.domain.qls.model.ktPupil
import ru.nekit.android.domain.qls.model.ktQuestStatisticsReport
import ru.nekit.android.domain.repository.IReactiveCRUD
import ru.nekit.android.domain.shared.model.ktQuestType
import ru.nekit.android.domain.shared.model.ktQuestionType
import ru.nekit.android.domain.shared.repository.ISetupWizardBaseSettingsRepository

interface IRepository

interface IQuestStatisticsReportRepository {

    fun updateOrCreate(report: ktQuestStatisticsReport): Completable

    fun fetchOrCreate(pupil: ktPupil, questType: ktQuestType, questionType: ktQuestionType): Single<ktQuestStatisticsReport>

    fun fetchAsyncBy(pupil: ktPupil, questType: ktQuestType, questionType: ktQuestionType): Single<Optional<ktQuestStatisticsReport>>

    fun fetchBy(pupil: ktPupil, questType: ktQuestType, questionType: ktQuestionType): Single<Optional<ktQuestStatisticsReport>>

    fun fetchAll(pupil: ktPupil): Single<List<ktQuestStatisticsReport>>

    fun listenForChanges(pupil: ktPupil, questType: ktQuestType, questionType: ktQuestionType): Flowable<Optional<ktQuestStatisticsReport>>
}

interface IPupilRepository : IReactiveCRUD<ktPupil, String> {

    fun getCurrentPupil(): Single<Optional<ktPupil>>

    fun setCurrentPupil(pupil: ktPupil): Completable

}

interface ICurrentPupilRepository {

    fun getCurrentUuid(): Single<Optional<String>>

    fun setCurrentUuid(pupilUuid: String): Completable

}

interface IQuestSettingRepository : ISetupWizardBaseSettingsRepository

