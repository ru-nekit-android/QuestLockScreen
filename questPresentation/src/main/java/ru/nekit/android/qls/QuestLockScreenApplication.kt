package ru.nekit.android.qls

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.multidex.MultiDex
import android.util.Log
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
import ru.nekit.android.qls.data.entity.*
import ru.nekit.android.qls.data.providers.IRepositorySupport
import ru.nekit.android.qls.data.repository.*
import ru.nekit.android.qls.data.repository.store.QuestStore
import ru.nekit.android.qls.domain.model.StatisticsPeriodType
import ru.nekit.android.qls.domain.model.StatisticsPeriodType.*
import ru.nekit.android.qls.domain.providers.*
import ru.nekit.android.qls.domain.repository.*
import ru.nekit.android.qls.domain.useCases.*
import ru.nekit.android.qls.domain.useCases.PeriodTime.*
import ru.nekit.android.qls.lockScreen.LockScreen.startForSetupWizard
import ru.nekit.android.qls.setupWizard.QuestSetupWizard
import ru.nekit.android.qls.setupWizard.billing.Billing
import ru.nekit.android.qls.setupWizard.view.QuestSetupWizardActivity
import ru.nekit.android.utils.ScreenHost
import ru.nekit.android.utils.TimeUtils
import java.util.*

class QuestLockScreenApplication : Application(), IRepositoryHolder {

    private lateinit var boxStore: BoxStore
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
    private lateinit var mQuestSetupWizardSettingRepository: IQuestSetupWizardSettingRepository
    private lateinit var mQuestStore: QuestStore
    private lateinit var mQuestHistoryRepository: IQuestHistoryRepository
    private lateinit var mQuestRepository: IQuestRepository
    private lateinit var mQuestHistoryCriteriaRepository: QuestHistoryCriteriaRepository
    private lateinit var mEmergencyPhoneRepository: EmergencyPhoneRepository
    private lateinit var mSKUDetailsRepository: ISKUDetailsRepository
    private lateinit var mSKUPurchaseRepository: ISKUPurchaseRepository
    private lateinit var mSetupWizard: QuestSetupWizard
    private lateinit var billing: Billing
    private lateinit var mLog: ILog

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

    private fun injectDependenciesForUseCase(value: IUseCaseSupport) {
        injectDependencies(value)
        value.apply {
            log = mLog
            when (value) {
                is SKUUseCases -> value.billing = billing
                is PupilUseCases -> value.uuidProvider = getUuidProvider()
            }
        }
    }

    fun injectDependencies(value: IDependenceProvider) {
        value.apply {
            repository = this@QuestLockScreenApplication
            schedulerProvider = defaultSchedulerProvider
            timeProvider = getTimeProvider()
            eventSender = getEventSender()
            eventListener = getEventListener()
            screenProvider = getScreenProvider()
        }
    }

    fun injectDependenciesForRepository(value: IRepositorySupport) {

    }

    private fun initDependencies() {
        boxStore = MyObjectBox.builder().androidContext(this).build()
        mCurrentPupilRepository = CurrentPupilRepository(getSharedPreferences())
        mRewardRepository = RewardRepository(getSharedPreferences("rewards"))
        mUnlockSecretRepository = UnlockSecretRepository(getSharedPreferences())
        mSessionRepository = SessionRepository(getSharedPreferences())
        mPupilRepository = PupilRepository(this, boxStore)
        mQuestTrainingProgramRepository = QuestTrainingProgramRepository(this, boxStore)
        mPhoneContactRepository = PhoneContactRepository(this, boxStore)
        mQuestStatisticsReportRepository = QuestStatisticsReportRepository(this, boxStore)
        mEmergencyPhoneRepository = EmergencyPhoneRepository(this)
        mPupilStatisticsRepository = PupilStatisticsRepository(this, boxStore)
        mQuestStateRepository = QuestStateRepository(getSharedPreferences())
        mTransitionChoreographRepository = object : TransitionChoreographRepository(getSharedPreferences()) {
            override val advertCounter: ICounter = Counter(getSharedPreferences(), "advert")
            override val questSeriesCounter: ICounter = Counter(getSharedPreferences(), "questSeries")
        }
        mQuestSetupWizardSettingRepository = object : QuestSetupWizardSettingRepository(getSharedPreferences()) {
            override val delayedPlayDelay: Long
                get() = resources.getInteger(R.integer.quest_delayed_start_animation_duration).toLong()
        }
        mLockScreenRepository = LockScreenRepository(getSharedPreferences(), boxStore)
        mQuestHistoryRepository = QuestHistoryRepository(this, boxStore)
        mQuestStore = QuestStore(getSharedPreferences())
        rxEventBus = RxEventBus.getInstance(BusProvider.getInstance())
        mQuestResourceRepository = QuestResourceRepository(this)
        mQuestRepository = QuestRepository(this, mQuestStore, boxStore)
        mQuestHistoryCriteriaRepository = QuestHistoryCriteriaRepository()
        mSKUDetailsRepository = SKUDetailsRepository(boxStore)
        mSKUPurchaseRepository = SKUPurchaseRepository(boxStore)
        mSetupWizard = QuestSetupWizard.getInstance(this)
        billing = Billing(this)
        mLog = object : ILog {
            override fun d(tag: String, message: String) {
                Log.d(tag, message)
            }

        }
    }

    private fun injectDependencies() {
        injectDependenciesForUseCase(LockScreenUseCases)
        injectDependenciesForUseCase(UnlockSecretUseCases)
        injectDependenciesForUseCase(SessionUseCases)
        injectDependenciesForUseCase(PupilUseCases)
        injectDependenciesForUseCase(QuestStatisticsAndHistoryUseCases)
        injectDependenciesForUseCase(SettingsUseCases)
        injectDependenciesForUseCase(PhoneContactsUseCases)
        injectDependenciesForUseCase(TransitionChoreographUseCases)
        injectDependenciesForUseCase(AdsUseCases)
        injectDependenciesForUseCase(QuestUseCases)
        injectDependenciesForUseCase(AccessUseCases)
        injectDependenciesForUseCase(TrialAccessUseCases)
        injectDependenciesForUseCase(PremiumAccessUseCases)
        injectDependenciesForUseCase(SKUUseCases)
    }

    override fun onCreate() {
        super.onCreate()

        initDependencies()
        injectDependencies()
        ///
        val clear = false
        if (clear) {
            boxStore.boxFor(QuestStatisticsReportEntity::class.java).removeAll()
            boxStore.boxFor(QuestHistoryEntity::class.java).removeAll()
            boxStore.boxFor(SKUDetailsEntity::class.java).removeAll()
            boxStore.boxFor(SKUPurchaseEntity::class.java).removeAll()
            mRewardRepository.clear()
        }
        ///
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {}

            override fun onActivityResumed(activity: Activity?) {}

            override fun onActivityStarted(activity: Activity?) {}

            override fun onActivityDestroyed(activity: Activity?) {}
            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}
            override fun onActivityStopped(activity: Activity?) {}
            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                if (activity is QuestSetupWizardActivity)
                    startForSetupWizard(this@QuestLockScreenApplication)
                setupWizard.activity = activity!!
            }
        })
    }

    fun getTimeProvider() = object : ITimeProvider {

        override fun getPeriodTime(periodTime: PeriodTime): Long {
            return when (periodTime) {
                P1Y -> 365 * 24 * 60 * 60 * 1000L
                P1M -> 31 * 24 * 60 * 60 * 1000L
                P1W -> 7 * 24 * 60 * 60 * 1000L
                P3M -> getPeriodTime(P1M) * 3
                P6M -> getPeriodTime(P1M) * 6
                else -> 0
            }
        }

        override fun getPeriodIntervalForPeriod(statisticsPeriodTypePair: Pair<StatisticsPeriodType, StatisticsPeriodType>): List<Pair<Long, Long>> {

            return when (statisticsPeriodTypePair.first) {
                MONTHLY -> when (statisticsPeriodTypePair.second) {
                    WEEKLY -> TimeUtils.weekPeriodsForMonth
                    else -> TODO()
                }
                else -> TODO()
            }

        }

        override fun getTimestampBy(statisticsPeriodType: StatisticsPeriodType): Long = when (statisticsPeriodType) {
            DAILY -> TimeUtils.timestampForStartOfDay
            WEEKLY -> TimeUtils.timestampForStartOfWeek
            MONTHLY -> TimeUtils.timestampForStartOfMonth
            HOURLY -> getCurrentTime() - getCurrentTime() % 60 * 60 * 1000
        }

        override fun getCurrentTime(): Long = TimeUtils.currentTime

    }

    fun getUuidProvider() = object : IUUIDProvider {
        override fun generateUuid(): String = UUID.randomUUID().toString()
    }

    override fun getQuestSetupWizardSettingRepository(): IQuestSetupWizardSettingRepository = mQuestSetupWizardSettingRepository

    private fun getScreenProvider(): IScreenProvider = object : IScreenProvider {

        override fun screenIsOn(): Boolean = ScreenHost.isScreenOn(this@QuestLockScreenApplication)

    }

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

    fun getSharedPreferences(name: String = "my"): SharedPreferences = getSharedPreferences(name, Context.MODE_PRIVATE)
    fun getEventSender(): IEventSender = eventSender
    fun getEventListener(): IEventListener = eventListener
    fun getBilling(): Billing = billing
    override fun getQuestResourceRepository() = mQuestResourceRepository
    override fun getQuestRepository(): IQuestRepository = mQuestRepository

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}