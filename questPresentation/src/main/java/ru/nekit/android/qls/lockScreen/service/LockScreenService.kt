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
import ru.nekit.android.qls.R.string.*
import ru.nekit.android.qls.domain.model.LockScreenStartType
import ru.nekit.android.qls.domain.model.LockScreenStartType.*
import ru.nekit.android.qls.domain.useCases.GetPhoneContactByIdUseCase
import ru.nekit.android.qls.domain.useCases.LockScreenUseCases
import ru.nekit.android.qls.domain.useCases.PupilUseCases
import ru.nekit.android.qls.lockScreen.LockScreen
import ru.nekit.android.qls.lockScreen.mediator.LockScreenContentMediator
import ru.nekit.android.qls.lockScreen.receiver.PhoneCallReceiver
import ru.nekit.android.qls.lockScreen.receiver.PhoneCallReceiver.PhoneEvent.*
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.setupWizard.QuestSetupWizard
import ru.nekit.android.utils.PhoneUtils
import ru.nekit.android.utils.TimeUtils

class LockScreenService : Service() {

    private val questApplication: QuestLockScreenApplication
        get() = application as QuestLockScreenApplication
    private val eventListener: IEventListener
        get() = questApplication.getEventListener()
    private val notificationManager: NotificationManager
        get() = questApplication.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val setupWizard: QuestSetupWizard
        get() = questApplication.setupWizard
    private val contentIntentLockScreenSwitchOn: PendingIntent
        get() = getService(questApplication, 0,
                LockScreen.getStartIntent(questApplication, ON_NOTIFICATION_CLICK),
                FLAG_UPDATE_CURRENT)
    private val contentIntentForSetupWizard: PendingIntent
        get() = getActivity(questApplication, 0,
                setupWizard.getStartIntent(false),
                FLAG_UPDATE_CURRENT)
    private lateinit var startType: LockScreenStartType
    private var questContext: QuestContext? = null
    private var startId: Int = 0
    private var lockScreenContentMediator: LockScreenContentMediator? = null

    private val screenOffEventHandler = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            questContext?.stopQuest()
            tryToShowLockScreen(ON_SCREEN_OFF)
        }
    }

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
                            tryToShowLockScreen(ON_INCOME_CALL_COMPLETE)
                        }
                OUTGOING_CALL_COMPLETE -> {
                    LockScreenUseCases.stopOutgoingCall {
                        tryToShowLockScreen(ON_OUTGOING_CALL_COMPLETE)
                    }
                }
                else -> {
                }
            }
        }
        eventListener.listen(this, OutgoingCallAction::class.java) {
            if (setupWizard.callPhonePermissionIsGranted()) {
                LockScreenUseCases.startOutgoingCall {
                    GetPhoneContactByIdUseCase(questApplication,
                            questApplication.defaultSchedulerProvider).use(it.contactId) {
                        if (it.isNotEmpty()) {
                            startActivity(Intent(ACTION_CALL).apply {
                                flags = FLAG_ACTIVITY_NEW_TASK or
                                        FLAG_ACTIVITY_NO_USER_ACTION
                                val phoneContact = it.nonNullData
                                val phoneNumber = phoneContact.phoneNumber
                                data = Uri.parse("tel:$phoneNumber")
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
        if (intent != null) {
            this.startId = startId
            startType = LockScreenStartType.getById(intent.getIntExtra(LockScreenStartType.NAME, 0))
            LockScreenUseCases.switchOnIfNeed(startType) { isOn ->
                when (startType) {
                    ON_BOOT_COMPLETE ->
                        if (setupWizard.setupIsComplete())
                            buildNotificationForLockScreenSwitchOn(if (isOn) notification_let_play
                            else notification_let_try_to_play) { notification ->
                                startForeground(notification)
                            }
                        else
                            buildNotificationForSetupWizard(notification_setup_wizard) { notification ->
                                startForeground(notification)
                            }
                    SETUP_WIZARD_IN_PROCESS -> {
                        buildNotificationForSetupWizard(notification_setup_wizard) { notification ->
                            startForeground(notification)
                        }
                    }
                    LET_TRY_IT -> {
                        buildNotificationForLockScreenSwitchOn(notification_let_try_to_play) { notification ->
                            startForeground(notification)
                        }
                    }
                    else -> tryToShowLockScreen(startType)
                }
            }
        }
        return Service.START_REDELIVER_INTENT
    }

    private fun tryToShowLockScreen(startType: LockScreenStartType) {
        if (setupWizard.setupIsComplete()) {
            if (lockScreenContentMediator == null) {
                LockScreenUseCases.tryToShowLockScreen(startType) {
                    if (PhoneUtils.isPhoneIdle(application)
                            && !PhoneUtils.pinOrPukCodeRequired(application)) {
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
                            buildNotificationForLockScreenSwitchOn(notification_let_play) { notification ->
                                startForeground(notification)
                            }
                        }
                    }
                }
            }
        } else setupWizard.start()
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
        LockScreen.startOnDestroy(this)
        stopSelf(startId)
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

    private fun buildNotificationForLockScreenSwitchOn(@StringRes textResId: Int, body: (Notification) -> Unit) =
            PupilUseCases.useCurrentPupil { it ->
                body(createNotificationBuilder(it.name!!, getString(textResId), contentIntentLockScreenSwitchOn).build())
            }

    private fun buildNotificationForSetupWizard(@StringRes textResId: Int, body: (Notification) -> Unit): Unit =
            useCompletableUseCaseFromRunnable(questApplication.defaultSchedulerProvider, {}) {
                body(createNotificationBuilder(getString(title_notification_setup_wizard),
                        getString(textResId), contentIntentForSetupWizard).setAutoCancel(true).build())
            }

    private fun createNotificationBuilder(title: String,
                                          text: String,
                                          contentIntent: PendingIntent): NotificationCompat.Builder =
            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationCompat.Builder(this,
                        NotificationChannelBuilder(notificationManager).create(
                                getString(app_name)))
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
            } else {
                @Suppress("DEPRECATION")
                NotificationCompat.Builder(this).setContentIntent(contentIntent)
            }).setOngoing(true)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setWhen(TimeUtils.currentTime)

    override fun onTaskRemoved(intent: Intent) {
        LockScreen.hide {
            LockScreen.startOnDestroy(this)
        }
    }

    companion object {

        private const val EVENT_ID = 1
    }
}

data class OutgoingCallAction(val contactId: Long = 0) : IEvent {
    override val eventName: String = javaClass.name
}
