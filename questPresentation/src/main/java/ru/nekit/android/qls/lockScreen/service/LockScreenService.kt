package ru.nekit.android.qls.lockScreen.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.support.annotation.StringRes
import android.support.v4.app.NotificationCompat
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.event.IEventListener
import ru.nekit.android.domain.interactor.use
import ru.nekit.android.domain.interactor.useCompletableUseCaseFromRunnable
import ru.nekit.android.qls.QuestLockScreenApplication
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.LockScreenStartType
import ru.nekit.android.qls.domain.model.LockScreenStartType.*
import ru.nekit.android.qls.domain.useCases.GetCurrentPupilUseCase
import ru.nekit.android.qls.domain.useCases.GetPhoneContactByIdUseCase
import ru.nekit.android.qls.domain.useCases.LockScreenUseCases
import ru.nekit.android.qls.lockScreen.LockScreen
import ru.nekit.android.qls.lockScreen.mediator.LockScreenContentMediator
import ru.nekit.android.qls.lockScreen.receiver.PhoneCallReceiver
import ru.nekit.android.qls.lockScreen.receiver.PhoneCallReceiver.PhoneEvent.*
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.setupWizard.QuestSetupWizard
import ru.nekit.android.qls.utils.PhoneUtils
import ru.nekit.android.utils.TimeUtils

class LockScreenService : Service() {

    private val eventListener: IEventListener
        get() = questApplication.getEventListener()

    private var lockScreenContentMediator: LockScreenContentMediator? = null

    private val screenOffEventHandler = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            questContext?.stopQuest()
            if (lockScreenContentMediator == null) {
                showLockScreen(ON_SCREEN_OFF)
            }
        }
    }

    private val notificationManager: NotificationManager
        get() = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private lateinit var startType: LockScreenStartType
    private var questContext: QuestContext? = null
    private var startId: Int = 0
    private val setupWizard: QuestSetupWizard
        get() = QuestSetupWizard.getInstance(questApplication)
    private val questApplication: QuestLockScreenApplication
        get() = application as QuestLockScreenApplication
    private val contentIntentForPupil: PendingIntent
        get() = getService(applicationContext, 0,
                LockScreen.getStartIntent(applicationContext, ON_NOTIFICATION_CLICK),
                FLAG_UPDATE_CURRENT)
    private val contentIntentForSetupWizard: PendingIntent
        get() = getActivity(applicationContext, 0,
                setupWizard.getStartIntent(false),
                FLAG_UPDATE_CURRENT)

    override fun onCreate() {
        super.onCreate()
        registerReceiver(screenOffEventHandler, IntentFilter(ACTION_SCREEN_OFF).also {
            it.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        })
        //event listener for service
        eventListener.listen(this, PhoneCallReceiver.PhoneEvent::class.java) {
            when (it) {
                INCOMING_CALL_START -> {
                    if (lockScreenContentMediator != null)
                        LockScreenUseCases.startIncomingCall {
                            hideLockScreen()
                        }
                }
                INCOMING_CALL_COMPLETE ->
                    if (lockScreenContentMediator == null)
                        LockScreenUseCases.stopIncomingCall {
                            showLockScreen(ON_INCOME_CALL_COMPLETE)
                        }
                OUTGOING_CALL_COMPLETE -> {
                    LockScreenUseCases.stopOutgoingCall {
                        showLockScreen(ON_OUTGOING_CALL_COMPLETE)
                    }
                }
                else -> {
                }
            }
        }
        eventListener.listen(this, OutgoingCall::class.java) {
            if (setupWizard.callPhonePermissionIsGranted()) {
                LockScreenUseCases.startOutgoingCall {
                    GetPhoneContactByIdUseCase(questApplication,
                            questApplication.getDefaultSchedulerProvider()).use(it.contactId) {
                        if (it.isNotEmpty()) {
                            startActivity(Intent(ACTION_CALL).apply {
                                flags = FLAG_ACTIVITY_NEW_TASK or
                                        FLAG_ACTIVITY_NO_USER_ACTION
                                data = Uri.parse("tel:" + it.nonNullData.phoneNumber)
                            })
                            hideLockScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        //Remember - intent can be null!!!
        intent?.let {
            this.startId = startId
            startType = LockScreenStartType.getById(it.getIntExtra(LockScreenStartType.NAME, 0))
            val setupWizardIsComplete = setupWizard.setupIsComplete()
            when (startType) {

                ON_BOOT_COMPLETE, SETUP_WIZARD -> {

                    if (setupWizardIsComplete)
                        buildNotificationForCurrentPupil(R.string.notification_let_play) { notification ->
                            startForeground(notification)
                        }
                    else
                        buildNotificationForSetupWizard(R.string.notification_setup_wizard) { notification ->
                            startForeground(notification)
                        }
                }

                SILENCE ->

                    buildNotificationForCurrentPupil(R.string.silence_mode) { notification ->
                        startForeground(notification)
                    }

                else -> {
                    if (setupWizardIsComplete) {
                        showLockScreen(startType)
                    }
                }

            }
        }

        return Service.START_REDELIVER_INTENT
    }

    private fun startForeground(notification: Notification?) {
        startForeground(EVENT_ID, notification)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        questContext?.destroy()
        hideLockScreen()
        unregisterReceiver(screenOffEventHandler)
        notificationManager.cancelAll()
        stopForeground(true)
        eventListener.stopListen(this)
        LockScreen.activeIfOn(this, ON_DESTROY)
        stopSelf(startId)
    }

    //exclude SILENCE START LOCK SCREEN MODE
    private fun showLockScreen(startType: LockScreenStartType) {
        LockScreenUseCases.showLockScreen(startType) {
            if (PhoneUtils.isPhoneIdle(application)
                    && !PhoneUtils.pinOrPukCodeRequired(application))
                if (setupWizard.allPermissionsIsGranted()) {
                    sendBroadcast(Intent(ACTION_CLOSE_SYSTEM_DIALOGS))
                    QuestContext(questApplication, R.style.MainTheme).let {
                        questContext = it
                        questApplication.injectDependencies(it)
                        eventListener.listen(it, LockScreenUseCases.LockScreenHideEvent::class.java) {
                            hideLockScreen()
                        }
                        it.createQuestTrainingProgram {
                            LockScreenContentMediator(it).apply {
                                lockScreenContentMediator = this
                                attachView()
                            }
                        }
                        buildNotificationForCurrentPupil(R.string.notification_let_play) { notification ->
                            startForeground(notification)
                    }
                }
                } else setupWizard.start()
        }
    }

    private fun hideLockScreen() {
        questContext?.apply {
            eventListener.stopListen(this)
            destroy()
        }
        lockScreenContentMediator?.apply {
            deactivate()
            detachView()
        }
        questContext = null
        lockScreenContentMediator = null
    }

    private fun buildNotificationForCurrentPupil(@StringRes textResId: Int, body: (Notification) -> Unit) =
            GetCurrentPupilUseCase(questApplication, questApplication.getDefaultSchedulerProvider()).use {
                body(createNotificationBuilder(it.nonNullData.name!!, getString(textResId), contentIntentForPupil).build())
            }

    private fun buildNotificationForSetupWizard(@StringRes textResId: Int, body: (Notification) -> Unit): Unit =
            useCompletableUseCaseFromRunnable(questApplication.getDefaultSchedulerProvider(), {}) {
                body(createNotificationBuilder(getString(R.string.title_notification_setup_wizard),
                        getString(textResId), contentIntentForSetupWizard).setAutoCancel(true).build())
            }

    private fun createNotificationBuilder(title: String,
                                          text: String,
                                          contentIntent: PendingIntent): NotificationCompat.Builder =
            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationCompat.Builder(this,
                        NotificationChannelBuilder(notificationManager).create(
                                getString(R.string.app_name)))
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
            } else {
                @Suppress("DEPRECATION")
                NotificationCompat.Builder(this).setContentIntent(contentIntent)
            }).setOngoing(true)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setWhen(TimeUtils.currentTime)

    override fun onTaskRemoved(rootIntent: Intent) {
        LockScreen.activeIfOn(this, ON_DESTROY)
    }

    companion object {

        private const val EVENT_ID = 1
    }
}

data class OutgoingCall(val contactId: Long = 0) : IEvent {
    override val eventName: String = javaClass.name
}
