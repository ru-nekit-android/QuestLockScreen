package ru.nekit.android.qls.dependences

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat
import com.anadeainc.rxbus.BusProvider
import com.anadeainc.rxbus.RxBus
import io.objectbox.BoxStore
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.nekit.android.data.Counter
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.event.IEventListener
import ru.nekit.android.domain.event.IEventSender
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.repository.ICounter
import ru.nekit.android.eventBus.EventListener
import ru.nekit.android.eventBus.RxEventBus
import ru.nekit.android.qls.Logger
import ru.nekit.android.qls.data.entity.MyObjectBox
import ru.nekit.android.qls.data.repository.*
import ru.nekit.android.qls.data.repository.store.QuestStore
import ru.nekit.android.qls.domain.model.PeriodTime
import ru.nekit.android.qls.domain.model.StatisticsPeriodType
import ru.nekit.android.qls.domain.providers.*
import ru.nekit.android.qls.domain.repository.*
import ru.nekit.android.qls.domain.useCases.*
import ru.nekit.android.qls.lockScreen.LockScreen
import ru.nekit.android.qls.setupWizard.QuestSetupWizard
import ru.nekit.android.qls.setupWizard.billing.Billing
import ru.nekit.android.qls.setupWizard.remote.RemoteQuestTrainingProgramDataSource
import ru.nekit.android.qls.setupWizard.remote.SettingsRemoteDataSource
import ru.nekit.android.utils.ScreenHost
import ru.nekit.android.utils.TimeUtils
import java.util.*

@SuppressLint("Registered")
open class DependenciesProvider : Application() {

    protected lateinit var boxStore: BoxStore
    private lateinit var mRewardRepository: IRewardRepository
    private lateinit var mPupilRepository: IPupilRepository
    private lateinit var mCurrentPupilRepository: ICurrentPupilRepository
    private lateinit var mQuestStatisticsReportRepository: IQuestStatisticsReportRepository
    private lateinit var mPhoneContactRepository: IPhoneContactRepository
    private lateinit var mUnlockSecretRepository: IUnlockSecretRepository
    private lateinit var mSessionRepository: ISessionRepository
    private lateinit var mQuestTrainingProgramRepository: IQuestTrainingProgramRepository
    private lateinit var mPupilStatisticsRepository: IPupilStatisticsRepository
    private lateinit var mQuestStateRepository: IQuestStateRepository
    private lateinit var mQuestResourceRepository: IQuestResourceRepository
    private lateinit var mLockScreenRepository: ILockScreenRepository
    private lateinit var mTransitionChoreographRepository: ITransitionChoreographRepository
    private lateinit var mSettingsRepository: ISettingsRepository
    private lateinit var mQuestStore: QuestStore
    private lateinit var mQuestHistoryRepository: IQuestHistoryRepository
    private lateinit var mQuestRepository: IQuestRepository
    private lateinit var mQuestHistoryCriteriaRepository: QuestHistoryCriteriaRepository
    private lateinit var mEmergencyPhoneRepository: EmergencyPhoneRepository
    private lateinit var mSKUDetailsRepository: ISKUDetailsRepository
    private lateinit var mSKUPurchaseRepository: ISKUPurchaseRepository
    private lateinit var mSetupWizard: QuestSetupWizard
    private lateinit var mBilling: Billing
    private lateinit var mLogger: ILogger
    private lateinit var mRemoteSettings: SettingsRemoteDataSource
    private lateinit var mLocalQuestTrainingProgramSource: IQuestTrainingProgramDataSource
    private lateinit var mRemoteQuestTrainingProgramSource: IQuestTrainingProgramDataSource

    private var rxEventBus: RxEventBus = RxEventBus.getInstance(RxBus())
    private val eventSender: IEventSender = object : IEventSender {
        override fun send(event: IEvent) = rxEventBus.post(event)
    }
    private val eventListener: IEventListener = EventListener(rxEventBus)

    val defaultSchedulerProvider: ISchedulerProvider = object : ISchedulerProvider {
        override fun computation(): Scheduler = Schedulers.computation()

        override fun ui(): Scheduler = AndroidSchedulers.mainThread()
    }

    val setupWizard: QuestSetupWizard
        get() = mSetupWizard

    fun getLogger(): ILogger = mLogger

    val repositoryHolder: IRepositoryHolder = object : IRepositoryHolder {

        override fun getEmergencyPhoneRepository(): IEmergencyPhoneRepository = mEmergencyPhoneRepository
        override fun getUnlockSecretRepository(): IUnlockSecretRepository = mUnlockSecretRepository
        override fun getCurrentPupilRepository(): ICurrentPupilRepository = mCurrentPupilRepository
        override fun getPhoneContactRepository(): IPhoneContactRepository = mPhoneContactRepository
        override fun getSessionRepository(): ISessionRepository = mSessionRepository
        override fun getRewardRepository(): IRewardRepository = mRewardRepository
        override fun getPupilRepository(): IPupilRepository = mPupilRepository
        override fun getQuestTrainingProgramRepository(): IQuestTrainingProgramRepository = mQuestTrainingProgramRepository
        override fun getPupilStatisticsRepository(): IPupilStatisticsRepository = mPupilStatisticsRepository
        override fun getQuestStateRepository(): IQuestStateRepository = mQuestStateRepository
        override fun getLockScreenRepository(): ILockScreenRepository = mLockScreenRepository
        override fun getTransitionChoreographRepository(): ITransitionChoreographRepository = mTransitionChoreographRepository
        override fun getQuestStatisticsReportRepository(): IQuestStatisticsReportRepository = mQuestStatisticsReportRepository
        override fun getQuestHistoryRepository(): IQuestHistoryRepository = mQuestHistoryRepository
        override fun getQuestHistoryCriteriaRepository(): IQuestHistoryCriteriaRepository = mQuestHistoryCriteriaRepository
        override fun getSKUDetailsRepository(): ISKUDetailsRepository = mSKUDetailsRepository
        override fun getSKUPurchaseRepository(): ISKUPurchaseRepository = mSKUPurchaseRepository
        override fun getQuestResourceRepository() = mQuestResourceRepository
        override fun getQuestRepository(): IQuestRepository = mQuestRepository
        override fun getSettingsRepository(): ISettingsRepository = mSettingsRepository
        override fun getQuestTrainingProgramDataSource(type: DataSourceType): IQuestTrainingProgramDataSource =
                when (type) {
                    DataSourceType.LOCAL -> mLocalQuestTrainingProgramSource
                    DataSourceType.REMOTE -> mRemoteQuestTrainingProgramSource
                }
    }

    private fun createDependencies() {
        boxStore = MyObjectBox.builder().androidContext(this).build()
        mCurrentPupilRepository = CurrentPupilRepository(getSharedPreferences())
        mRewardRepository = RewardRepository(getSharedPreferences("rewards"))
        mUnlockSecretRepository = UnlockSecretRepository(getSharedPreferences())
        mSessionRepository = SessionRepository(getSharedPreferences())
        mPupilRepository = PupilRepository(repositoryHolder, boxStore)
        mQuestTrainingProgramRepository = QuestTrainingProgramRepository(this, boxStore)
        mPhoneContactRepository = PhoneContactRepository(repositoryHolder, boxStore)
        mQuestStatisticsReportRepository = QuestStatisticsReportRepository(repositoryHolder, boxStore)
        mEmergencyPhoneRepository = EmergencyPhoneRepository(this)
        mPupilStatisticsRepository = PupilStatisticsRepository(repositoryHolder, boxStore)
        mQuestStateRepository = QuestStateRepository(getSharedPreferences())
        mTransitionChoreographRepository = object : TransitionChoreographRepository(getSharedPreferences()) {
            override val advertCounter: ICounter = Counter(getSharedPreferences(), "advert")
            override val questSeriesCounter: ICounter = Counter(getSharedPreferences(), "questSeries")
        }
        mSettingsRepository = SettingsRepository(resources, getSharedPreferences("settings"))
        mLockScreenRepository = LockScreenRepository(getSharedPreferences(), boxStore)
        mQuestHistoryRepository = QuestHistoryRepository(repositoryHolder, boxStore)
        mQuestStore = QuestStore(getSharedPreferences())
        rxEventBus = RxEventBus.getInstance(BusProvider.getInstance())
        mQuestResourceRepository = QuestResourceRepository(this)
        mQuestRepository = QuestRepository(repositoryHolder, mQuestStore, boxStore)
        mQuestHistoryCriteriaRepository = QuestHistoryCriteriaRepository()
        mSKUDetailsRepository = SKUDetailsRepository(boxStore)
        mSKUPurchaseRepository = SKUPurchaseRepository(boxStore)
        mSetupWizard = QuestSetupWizard.getInstance(this)
        mBilling = Billing(this)
        mLogger = Logger()
        mRemoteSettings = SettingsRemoteDataSource.getInstance()
        mLocalQuestTrainingProgramSource = LocalQuestTrainingProgramDataSource(this)
        mRemoteQuestTrainingProgramSource = RemoteQuestTrainingProgramDataSource()
    }

    fun getSharedPreferences(name: String = "my"): SharedPreferences = getSharedPreferences(name, Context.MODE_PRIVATE)

    protected fun createAndInjectDependencies() {
        createDependencies()
        injectDependenciesForUseCase(LockScreenUseCases)
        injectDependenciesForUseCase(SetupWizardUseCases)
        injectDependenciesForUseCase(UnlockSecretUseCases)
        injectDependenciesForUseCase(SessionUseCases)
        injectDependenciesForUseCase(PupilUseCases)
        injectDependenciesForUseCase(QuestStatisticsAndHistoryUseCases)
        injectDependenciesForUseCase(PhoneContactsUseCases)
        injectDependenciesForUseCase(TransitionChoreographUseCases)
        injectDependenciesForUseCase(AdsUseCases)
        injectDependenciesForUseCase(QuestUseCases)
        injectDependenciesForUseCase(AccessUseCases)
        injectDependenciesForUseCase(TrialAccessUseCases)
        injectDependenciesForUseCase(PremiumAccessUseCases)
        injectDependenciesForUseCase(SKUUseCases)
        injectDependenciesForUseCase(QuestTrainingProgramUseCases)
        injectDependencies(mRemoteSettings)
        injectDependencies(getRemoteSettings())
        injectDependencies(LockScreen.getInstance())
    }

    fun getTimeProvider() = object : ITimeProvider {

        override fun getPeriodTime(periodTime: PeriodTime): Long {
            return when (periodTime) {
                PeriodTime.P1Y -> 365 * 24 * 60 * 60 * 1000L
                PeriodTime.P1M -> 31 * 24 * 60 * 60 * 1000L
                PeriodTime.P1W -> 7 * 24 * 60 * 60 * 1000L
                PeriodTime.P3M -> getPeriodTime(PeriodTime.P1M) * 3
                PeriodTime.P6M -> getPeriodTime(PeriodTime.P1M) * 6
                else -> 0
            }
        }

        override fun getPeriodIntervalForPeriod(statisticsPeriodTypePair: Pair<StatisticsPeriodType, StatisticsPeriodType>): List<Pair<Long, Long>> {

            return when (statisticsPeriodTypePair.first) {
                StatisticsPeriodType.MONTHLY -> when (statisticsPeriodTypePair.second) {
                    StatisticsPeriodType.WEEKLY -> TimeUtils.weekPeriodsForMonth
                    else -> TODO()
                }
                else -> TODO()
            }

        }

        override fun getTimestampBy(statisticsPeriodType: StatisticsPeriodType): Long = when (statisticsPeriodType) {
            StatisticsPeriodType.DAILY -> TimeUtils.timestampForStartOfDay
            StatisticsPeriodType.WEEKLY -> TimeUtils.timestampForStartOfWeek
            StatisticsPeriodType.MONTHLY -> TimeUtils.timestampForStartOfMonth
            StatisticsPeriodType.HOURLY -> getCurrentTime() - getCurrentTime() % 60 * 60 * 1000
        }

        override fun getCurrentTime(): Long = TimeUtils.currentTime

    }

    private val uuidProvider = object : IUUIDProvider {
        override fun generateUuid(): String = UUID.randomUUID().toString()
    }

    private fun getScreenProvider(): IScreenProvider = object : IScreenProvider {

        override fun screenIsOn(): Boolean = ScreenHost.isScreenOn(this@DependenciesProvider)

    }

    fun getEventSender(): IEventSender = eventSender
    fun getEventListener(): IEventListener = eventListener
    fun getBilling(): Billing = mBilling

    private fun injectDependenciesForUseCase(value: IUseCaseSupport): IUseCaseSupport {
        injectDependencies(value)
        value.apply {
            logger = mLogger
            when (value) {
                is SKUUseCases -> value.billing = mBilling
                is PupilUseCases -> value.uuidProvider = uuidProvider
                is SetupWizardUseCases -> value.phoneProvider = phoneProvider
            }
        }
        return value
    }

    private val phoneProvider: IPhoneProvider = object : IPhoneProvider {

        override fun callPhonePermissionIsGranted(): Boolean {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                    ContextCompat.checkSelfPermission(this@DependenciesProvider,
                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
        }

    }

    fun <T : IDependenciesHolder> injectDependencies(value: T): T {
        value.apply {
            repositoryHolder = this@DependenciesProvider.repositoryHolder
            schedulerProvider = defaultSchedulerProvider
            timeProvider = getTimeProvider()
            eventSender = getEventSender()
            eventListener = getEventListener()
            screenProvider = getScreenProvider()
            if (this is ContextDependenciesHolder) context = this@DependenciesProvider
        }

        return value
    }

    fun getRemoteSettings() = mRemoteSettings
}