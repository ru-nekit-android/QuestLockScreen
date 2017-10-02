package ru.nekit.android.qls.lockScreen.service;

import android.Manifest;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.InternalCommand;
import ru.nekit.android.qls.MessageChannel;
import ru.nekit.android.qls.MessageGateway;
import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.LockScreen;
import ru.nekit.android.qls.lockScreen.LockScreen.LockScreenStartType;
import ru.nekit.android.qls.lockScreen.LockScreenMediator;
import ru.nekit.android.qls.lockScreen.startLimiter.StartLimiter;
import ru.nekit.android.qls.lockScreen.startLimiter.StartLimiterStatistics;
import ru.nekit.android.qls.pupil.PhoneContact;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilManager;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.TypedMessage;
import ru.nekit.android.qls.quest.history.QuestHistoryItem;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgram;
import ru.nekit.android.qls.setupWizard.QuestSetupWizard;
import ru.nekit.android.qls.utils.PhoneManager;
import ru.nekit.android.qls.utils.TimeUtils;

import static android.content.Intent.ACTION_SCREEN_OFF;
import static android.content.Intent.ACTION_SCREEN_ON;
import static android.content.IntentFilter.SYSTEM_HIGH_PRIORITY;
import static ru.nekit.android.qls.PreferencesUtil.getBoolean;
import static ru.nekit.android.qls.PreferencesUtil.setBoolean;
import static ru.nekit.android.qls.lockScreen.LockScreen.LockScreenStartType.EXPLICIT;
import static ru.nekit.android.qls.lockScreen.LockScreen.LockScreenStartType.ON_BOOT_COMPLETE;
import static ru.nekit.android.qls.lockScreen.LockScreen.LockScreenStartType.ON_DESTROY;
import static ru.nekit.android.qls.lockScreen.LockScreen.LockScreenStartType.ON_INCOME_CALL_COMPLETE;
import static ru.nekit.android.qls.lockScreen.LockScreen.LockScreenStartType.ON_NOTIFICATION_CLICK;
import static ru.nekit.android.qls.lockScreen.LockScreen.LockScreenStartType.ON_OUTGOING_CALL_COMPLETE;
import static ru.nekit.android.qls.lockScreen.LockScreen.LockScreenStartType.ON_SCREEN_OFF;
import static ru.nekit.android.qls.lockScreen.LockScreen.LockScreenStartType.SETUP_WIZARD;
import static ru.nekit.android.qls.lockScreen.LockScreen.LockScreenStartType.SILENCE;
import static ru.nekit.android.qls.lockScreen.receiver.PhoneCallReceiver.INCOMING_CALL_ENDED;
import static ru.nekit.android.qls.lockScreen.receiver.PhoneCallReceiver.INCOMING_CALL_RECEIVED;
import static ru.nekit.android.qls.lockScreen.receiver.PhoneCallReceiver.OUTGOING_CALL_ENDED;
import static ru.nekit.android.qls.pupil.PupilManager.EVENT_SET_CURRENT_PUPIL;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_CREATE;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_RIGHT_ANSWER;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_TIC_TAC;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_WRONG_ANSWER;

public class LockScreenService extends Service implements MessageGateway.MessageListener,
        EventBus.IEventHandler {

    public static final String PUPIL_BIND_OK = "pupil_bind_ok";
    public static final String EVENT_OUTGOING_CALL = "eventOutgoingCall";

    public static final String ACTION_HIDE_LOCK_SCREEN_VIEW = "action_hide_lock_screen_view";

    private static final String NAME_RESTORE_AFTER_INCOMING_CALL_ENDED = "restore_after_incoming_call_end";
    private static final String NAME_RESTORE_AFTER_OUTGOING_CALL_ENDED = "restore_after_outgoing_call_end";

    private static final int EVENT_ID = 1;

    private MessageGateway mMessageGateway;
    private EventBus mEventBus;

    private KeyguardManager mKeyManager;
    private KeyguardManager.KeyguardLock mKeyLock;
    private NotificationManager mNotificationManager;
    private PupilManager mPupilManager;

    private Handler mWorkHandler;
    private Runnable mWorkRunnable;
    private StartLimiterStatistics mStartLimiterStatistics;

    private LockScreenStartType mStartType;
    private int mStartId;

    private QuestContext mQuestContext;
    private LockScreenMediator mLockScreenMediator;

    private EventBus.IEventHandler screenEventHandler = new EventBus.IEventHandler() {
        @Override
        public void onEvent(@NonNull Intent intent) {
            mStartLimiterStatistics.updateScreenOnLifeTime();
            mQuestContext.stopQuest();
            createLockScreenView(ON_SCREEN_OFF);
        }

        @NonNull
        @Override
        public String getEventBusName() {
            return ACTION_SCREEN_OFF;
        }
    };

    @Override
    public void onMessageReceive(@NonNull TypedMessage typedMessage,
                                 @NonNull MessageChannel messageChannel) {

        switch (typedMessage.messageType) {

            case InternalCommand.NAME:

                InternalCommand messageCommand = (InternalCommand) typedMessage.data;

                switch (messageCommand.command) {

                    case InternalCommand.OPEN:

                        break;

                    case InternalCommand.BIND_PUPIL_REQUEST:

                        Pupil pupil = mPupilManager.getPupilByUuid(messageCommand.dataAsString());
                        if (pupil != null) {
                            mMessageGateway.send(InternalCommand.NAME,
                                    new InternalCommand(InternalCommand.BIND_PUPIL_RESPONSE,
                                            pupil));
                        }

                        break;

                    case InternalCommand.BIND_PUPIL_OK:

                        mEventBus.sendEvent(PUPIL_BIND_OK);

                        break;

                }

                break;

        }
    }

    private void executeOnWorkHandler(Runnable task) {
        destroyWorkHandler();
        (mWorkHandler = new Handler()).post(mWorkRunnable = task);
    }

    private void initMessageChannel() {
        Pupil pupil = mPupilManager.getCurrentPupil();
        if (pupil != null && mMessageGateway == null) {
            mMessageGateway = new MessageGateway(new MessageChannel(pupil));
            mMessageGateway.listen(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mStartId = startId;
        mStartType = LockScreenStartType.fromOrdinal(intent.getIntExtra(LockScreenStartType.NAME, 0));
        Context context = getApplicationContext();
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        if (mPupilManager == null) {
            mPupilManager = new PupilManager();
        }
        if (mEventBus == null) {
            mEventBus = new EventBus(this);
            mEventBus.handleEvents(this,
                    ACTION_SCREEN_ON,
                    EVENT_SET_CURRENT_PUPIL,
                    ACTION_HIDE_LOCK_SCREEN_VIEW,
                    INCOMING_CALL_RECEIVED,
                    INCOMING_CALL_ENDED,
                    OUTGOING_CALL_ENDED,
                    EVENT_TIC_TAC,
                    EVENT_OUTGOING_CALL,
                    EVENT_RIGHT_ANSWER,
                    EVENT_WRONG_ANSWER,
                    EVENT_QUEST_CREATE);
            mEventBus.handleEvent(screenEventHandler, ACTION_SCREEN_OFF, SYSTEM_HIGH_PRIORITY);
        }
        if (mStartLimiterStatistics == null) {
            mStartLimiterStatistics = new StartLimiterStatistics(context);
        }
        boolean setupWizardIsComplete = QuestSetupWizard.setupIsComplete();
        if (mStartType == ON_BOOT_COMPLETE) {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
            if (!setupWizardIsComplete) {
                startForeground(buildNotificationForSetupWizard(R.string.notification_setup_wizard));
            }
        }
        if (mStartType == SETUP_WIZARD) {
            if (setupWizardIsComplete) {
                startForeground(buildNotificationForCurrentPupil(R.string.let_play));
            } else {
                startForeground(buildNotificationForSetupWizard(R.string.notification_setup_wizard));
            }
        } else if (mStartType == SILENCE) {
            executeOnWorkHandler(new Runnable() {
                @Override
                public void run() {
                    startForeground(buildNotificationForCurrentPupil(R.string.silence_mode));
                }
            });
        } else {
            if (mQuestContext == null) {
                mQuestContext = new QuestContext(context, mEventBus, R.style.MainTheme);
            }
            initMessageChannel();
            if (setupWizardIsComplete) {
                createLockScreenView(mStartType);
            }
        }
        return START_REDELIVER_INTENT;
    }

    private void startForeground(Notification notification) {
        startForeground(EVENT_ID, notification);
    }

    private void destroyWorkHandler() {
        if (mWorkHandler != null) {
            mWorkHandler.removeCallbacks(mWorkRunnable);
            mWorkHandler = null;
            mWorkRunnable = null;
        }
    }

    private void setLockGuard() {
        initKeyguardService();
        setStandardKeyguardState(LockScreen.isStandardKeyguardState(getApplicationContext()));
    }

    private void initKeyguardService() {
        if (null != mKeyManager) {
            mKeyManager = null;
        }
        mKeyManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (null != mKeyManager) {
            if (null != mKeyLock) {
                mKeyLock = null;
            }
            mKeyLock = mKeyManager.newKeyguardLock(Context.KEYGUARD_SERVICE);
        }
    }

    private void setStandardKeyguardState(boolean isStart) {
        if (mKeyLock != null) {
            if (isStart) {
                mKeyLock.reenableKeyguard();
            } else {
                mKeyLock.disableKeyguard();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        destroyLockScreenView();
        mEventBus.stopHandleEvents(this);
        mEventBus.stopHandleEvents(screenEventHandler);
        setStandardKeyguardState(false);
        mNotificationManager.cancelAll();
        stopForeground(true);
        stopSelf(mStartId);
        if (LockScreen.isOn()) {
            createLockScreenView(ON_DESTROY);
        }
    }

    //exclude SILENCE START LOCK SCREEN MODE
    private void createLockScreenView(@NonNull LockScreenStartType startType) {
        final Context context = getApplicationContext();
        if (startType == EXPLICIT || startType == ON_NOTIFICATION_CLICK) {
            mStartType = startType;
        }
        if (mStartType != SETUP_WIZARD) {
            setLockGuard();
            if (PhoneManager.isPhoneIdle(context)) {
                if (StartLimiter.isLimit(mStartLimiterStatistics, startType)) {
                    //do nothing
                } else {
                    if (mLockScreenMediator == null) {
                        executeOnWorkHandler(new Runnable() {
                            @Override
                            public void run() {
                                mEventBus.sendEvent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                                mLockScreenMediator = new LockScreenMediator(mQuestContext);
                                if (QuestSetupWizard.allPermissionsIsGranted(context)) {
                                    mQuestContext.setQuestTrainingProgram(
                                            QuestTrainingProgram.buildForCurrentPupil(context));
                                    mLockScreenMediator.attachView();
                                    startForeground(buildNotificationForCurrentPupil(R.string.let_play));
                                } else {
                                    QuestSetupWizard.start(context);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    private void destroyLockScreenView() {
        destroyWorkHandler();
        if (mLockScreenMediator != null) {
            mLockScreenMediator.deactivate();
            mLockScreenMediator.detachView();
            mLockScreenMediator = null;
        }
        mQuestContext.destroy();
    }

    @Nullable
    private Notification buildNotificationForCurrentPupil(@StringRes int textResId) {
        Pupil pupil = mPupilManager.getCurrentPupil();
        if (pupil != null) {
            return createNotificationBuilder(pupil.name, getString(textResId), getContentIntentForPupil()).build();
        }
        return null;
    }

    @Nullable
    private Notification buildNotificationForSetupWizard(@StringRes int textResId) {
        Notification.Builder builder =
                createNotificationBuilder(getString(R.string.title_notification_setup_wizard),
                        getString(textResId), getContentIntentForSetupWizard());
        builder.setAutoCancel(true);
        return builder.build();
    }

    private PendingIntent getContentIntentForPupil() {
        Context context = getApplicationContext();
        return PendingIntent.getService(context, 0,
                LockScreen.getActivationIntent(context, ON_NOTIFICATION_CLICK),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getContentIntentForSetupWizard() {
        Context context = getApplicationContext();
        return PendingIntent.getActivity(context, 0,
                QuestSetupWizard.getStartIntent(context, false),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Notification.Builder createNotificationBuilder(@NonNull String title,
                                                           @NonNull String text,
                                                           @NonNull PendingIntent contentIntent) {
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentIntent(contentIntent)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setWhen(TimeUtils.getCurrentTime());
        return builder;
    }

    @Override
    public void onEvent(@NonNull Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {

                case ACTION_SCREEN_ON:

                    mStartLimiterStatistics.updateScreenOffLifeTime();

                    break;

                case EVENT_TIC_TAC:

                    //updateNotification("Информация", String.format("Уровень: %s", mQuestTrainingProgram.getCurrentLevel(mPupilStatistics).getIndex() + 1));

                    break;

                case EVENT_OUTGOING_CALL:

                    setBoolean(NAME_RESTORE_AFTER_OUTGOING_CALL_ENDED, true);
                    PhoneContact phoneContact = intent.getParcelableExtra(PhoneContact.NAME);
                    String uri = "tel:" + phoneContact.phoneNumber;
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                    callIntent.setData(Uri.parse(uri));
                    if (PhoneManager.callPhonePermissionIsGranted(getBaseContext())) {
                        if (ActivityCompat.checkSelfPermission(LockScreenService.this,
                                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            startActivity(callIntent);
                        }
                    }
                    destroyLockScreenView();

                    break;

                case EVENT_RIGHT_ANSWER:
                case EVENT_WRONG_ANSWER:

                    QuestHistoryItem questHistoryItem =
                            intent.getParcelableExtra(QuestHistoryItem.NAME);
                    mMessageGateway.send(QuestHistoryItem.NAME, questHistoryItem);

                    if (EVENT_RIGHT_ANSWER.equals(action)) {
                        mStartLimiterStatistics.updateRightAnswerCount();
                    }

                    break;

                case EVENT_QUEST_CREATE:

                    if (!mQuestContext.questHasState(QuestContext.QuestState.RESTORED)) {
                        mMessageGateway.send(InternalCommand.NAME, new InternalCommand(action));
                    }

                    break;

                case EVENT_SET_CURRENT_PUPIL:

                    initMessageChannel();

                    break;

                case INCOMING_CALL_RECEIVED:

                    setBoolean(NAME_RESTORE_AFTER_INCOMING_CALL_ENDED, mLockScreenMediator != null);
                    destroyLockScreenView();

                    break;

                case INCOMING_CALL_ENDED:

                    if (getBoolean(NAME_RESTORE_AFTER_INCOMING_CALL_ENDED)) {
                        setBoolean(NAME_RESTORE_AFTER_INCOMING_CALL_ENDED, false);
                        createLockScreenView(ON_INCOME_CALL_COMPLETE);
                    }

                    break;

                case OUTGOING_CALL_ENDED:

                    if (getBoolean(NAME_RESTORE_AFTER_OUTGOING_CALL_ENDED)) {
                        setBoolean(NAME_RESTORE_AFTER_OUTGOING_CALL_ENDED, false);
                        if (mLockScreenMediator == null) {
                            createLockScreenView(ON_OUTGOING_CALL_COMPLETE);
                        }
                    }

                    break;

                case ACTION_HIDE_LOCK_SCREEN_VIEW:

                    destroyLockScreenView();

                    break;

            }
        }
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return getClass().getName();
    }
}