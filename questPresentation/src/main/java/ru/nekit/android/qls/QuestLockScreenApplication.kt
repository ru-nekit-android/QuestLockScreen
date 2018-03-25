package ru.nekit.android.qls

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.support.multidex.MultiDex
import com.anadeainc.rxbus.BusProvider
import com.anadeainc.rxbus.RxBus
import io.objectbox.BoxStore
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.nekit.android.data.Counter
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.executor.ISchedulerProvider
import ru.nekit.android.domain.repository.ICounter
import ru.nekit.android.qls.data.entity.MyObjectBox
import ru.nekit.android.qls.data.entity.QuestHistoryEntity
import ru.nekit.android.qls.data.entity.QuestStatisticsReportEntity
import ru.nekit.android.qls.data.repository.*
import ru.nekit.android.qls.data.repository.store.QuestStore
import ru.nekit.android.qls.domain.model.StatisticPeriodType
import ru.nekit.android.qls.domain.model.StatisticPeriodType.*
import ru.nekit.android.qls.domain.providers.IEventSender
import ru.nekit.android.qls.domain.providers.IScreenProvider
import ru.nekit.android.qls.domain.providers.ITimeProvider
import ru.nekit.android.qls.domain.repository.*
import ru.nekit.android.qls.domain.useCases.IUUIDProvider
import ru.nekit.android.qls.eventBus.EventListener
import ru.nekit.android.qls.eventBus.IEventListener
import ru.nekit.android.qls.eventBus.RxEventBus
import ru.nekit.android.qls.quest.resources.QuestResourceRepository
import ru.nekit.android.qls.utils.ScreenHost
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

    //event bus support
    private var rxEventBus: RxEventBus = RxEventBus.getInstance(RxBus())
    private val eventSender: IEventSender = object : IEventSender {
        override fun send(event: IEvent) = rxEventBus.post(event)
    }
    private val eventListener: IEventListener = EventListener(rxEventBus)

    private val mSchedulerProvider: ISchedulerProvider = object : ISchedulerProvider {
        override fun computation(): Scheduler = Schedulers.computation()
        override fun ui(): Scheduler = AndroidSchedulers.mainThread()
    }

    override fun getQuestParams(): IQuestParams = object : IQuestParams {

        override val delayedPlayDelay: Long
            get() = resources.getInteger(R.integer.quest_delayed_start_animation_duration).toLong()
    }

    override fun onCreate() {
        super.onCreate()
        boxStore = MyObjectBox.builder().androidContext(this).build()
        mCurrentPupilRepository = CurrentPupilRepository(getSharedPreferences())
        mRewardRepository = RewardRepository(getSharedPreferences("rewards"))
        mUnlockSecretRepository = UnlockSecretRepository(getSharedPreferences())
        mSessionRepository = SessionRepository(getSharedPreferences())
        mPupilRepository = PupilRepository(this, boxStore)
        mQuestTrainingProgramRepository = QuestTrainingProgramRepository(this, boxStore)
        mPhoneContactRepository = PhoneContactRepository(this, boxStore)
        mQuestStatisticsReportRepository = QuestStatisticsReportRepository(this, boxStore)
        mPupilStatisticsRepository = PupilStatisticsRepository(this, boxStore)
        mQuestStateRepository = QuestStateRepository(getSharedPreferences())
        mTransitionChoreographRepository = object : TransitionChoreographRepository(getSharedPreferences()) {
            override val advertCounter: ICounter = Counter(getSharedPreferences(), "advert")
            override val questSeriesCounter: ICounter = Counter(getSharedPreferences(), "questSeries")
        }
        mQuestSetupWizardSettingRepository = QuestSetupWizardSettingRepository(getSharedPreferences())
        mLockScreenRepository = LockScreenRepository(getSharedPreferences())
        mQuestHistoryRepository = QuestHistoryRepository(this, boxStore)
        mQuestStore = QuestStore(getSharedPreferences())
        rxEventBus = RxEventBus.getInstance(BusProvider.getInstance())
        mQuestResourceRepository = QuestResourceRepository(this)
        mQuestRepository = QuestRepository(this, mQuestStore, boxStore)
        mQuestHistoryCriteriaRepository = QuestHistoryCriteriaRepository()

        val clear = false
        if (clear) {
            boxStore.boxFor(QuestStatisticsReportEntity::class.java).removeAll()
            boxStore.boxFor(QuestHistoryEntity::class.java).removeAll()
            mRewardRepository.clear()
        }
    }

    fun getTimeProvider() = object : ITimeProvider {
        override fun getTimestampBy(statisticPeriodType: StatisticPeriodType): Long = when (statisticPeriodType) {
            DAILY -> TimeUtils.timestampForStartOfDay
            WEEKLY -> TimeUtils.timestampForStartOfWeek
            MONTHLY -> TimeUtils.timestampForStartOfMonth
        }

        override fun getCurrentTime(): Long = TimeUtils.currentTime

    }

    fun getUuidProvider() = object : IUUIDProvider {
        override fun provideUuid(): String = UUID.randomUUID().toString()
    }

    override fun getQuestSetupWizardSettingRepository(): IQuestSetupWizardSettingRepository = mQuestSetupWizardSettingRepository

    fun getScreenProvider(): IScreenProvider = object : IScreenProvider {

        override fun screenIsOn(): Boolean = ScreenHost.isScreenOn(this@QuestLockScreenApplication)

    }

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

    fun getSharedPreferences(name: String = "my"): SharedPreferences = getSharedPreferences(name, Context.MODE_PRIVATE)
    fun getDefaultSchedulerProvider(): ISchedulerProvider = mSchedulerProvider
    fun getEventSender(): IEventSender = eventSender
    fun getEventListener(): IEventListener = eventListener
    override fun getQuestResourceRepository() = mQuestResourceRepository
    override fun getQuestRepository(): IQuestRepository = mQuestRepository

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

/*val kodein = Kodein {
    constant("SharedPreferencesName") with "my"
    constant("SharedPreferencesMode") with Context.MODE_PRIVATE
    bind<Context>() with singleton { this@QuestLockScreenApplication }
    bind<Printer>() with factory { value: Int -> Printer(value) }
    bind<SharedPreferences>() with singleton { getSharedPreferences(instance("SharedPreferencesName"), instance("SharedPreferencesMode")) }
    bind<SharedPreferences>() with multiton { name: String -> getSharedPreferences(name, instance("SharedPreferencesMode")) }
    bind<IRepositoryHolder>() with singleton { this@QuestLockScreenApplication }
    bind<ISchedulerProvider>("defaultScheduler") with singleton {
        object : ISchedulerProvider {
            override fun computation(): Scheduler = Schedulers.io()
            override fun ui(): Scheduler = AndroidSchedulers.mainThread()
        }
    }
    bind<ISchedulerProvider>() with singleton {
        instance<ISchedulerProvider>("defaultScheduler")
    }
    bind<QuestTrainingProgramLevel>() with provider {
        GetCurrentQuestTrainingProgramLevelUseCase(instance(), instance()).build().blockingGet()
    }
}*/

}

