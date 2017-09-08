package ru.nekit.android.qls.parentControl;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.InternalCommand;
import ru.nekit.android.qls.MessageChannel;
import ru.nekit.android.qls.MessageGateway;
import ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilManager;
import ru.nekit.android.qls.quest.TypedMessage;
import ru.nekit.android.qls.utils.TimeUtils;

public class ParentControlService extends Service
        implements MessageGateway.MessageListener {

    public static final String ACTION_BIND_PUPIL = "action_bind_pupil";
    public static final String EVENT_BIND_PUPIL = "event_bind_pupil";

    public static final String EVENT_WAIT_FOR_BINDING_PUPIL = "action_wait_for_binding_pupil";
    public static final String ERROR_PUPIL_IS_BIND_ALREADY = "error_pupil_is_bind_already";
    public static final String NAME_PUPIL_UUID = "action_pupil_uuid";

    private static final int EVENT_ID = 1;

    private List<MessageGateway> mMessageGatewayList;
    private NotificationManager mNotificationManager;
    private PupilManager mPupilManager;
    private EventBus mEventBus;
    private int mStartId;

    private BroadcastReceiver mActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {

                    case ACTION_BIND_PUPIL:

                        String pupilUuid = intent.getStringExtra(NAME_PUPIL_UUID);
                        Pupil pupil = mPupilManager.getPupilByUuid(pupilUuid);
                        bindPupilIfAble(pupil);

                        break;

                }
            }
        }
    };

    private Handler mPeriodicCallHandler;
    private Runnable mPeriodicCallRunnable = new Runnable() {
        @Override
        public void run() {
            pupilsBind();
            mPeriodicCallHandler.postDelayed(mPeriodicCallRunnable, 15 * 1000);
        }
    };

    public static void start(@NonNull Context context) {
        context.startService(getStartIntent(context));
    }

    public static Intent getStartIntent(@NonNull Context context) {
        return new Intent(context, ParentControlService.class);
    }

    private void bindPupilIfAble(@NonNull Pupil pupil) {
        if (pupil.isBind) {
            mEventBus.sendEvent(ERROR_PUPIL_IS_BIND_ALREADY,
                   NAME_PUPIL_UUID, pupil.getUuid());
        } else {
            MessageChannel messageChannel = new MessageChannel(pupil);
            MessageGateway messageGateway = getMessageGateway(messageChannel);
            if (messageGateway == null) {
                messageGateway = createMessageGateway(messageChannel);
            }
            messageGateway.send(InternalCommand.NAME,
                    new InternalCommand(InternalCommand.BIND_PUPIL_REQUEST, pupil.getUuid()));
            mEventBus.sendEvent(EVENT_WAIT_FOR_BINDING_PUPIL, NAME_PUPIL_UUID, pupil.getUuid());
        }
    }

    private void startForeground(Notification notification) {
        startForeground(EVENT_ID, notification);
    }

    private void startPeriodicCall() {
        mPeriodicCallHandler.removeCallbacks(mPeriodicCallRunnable);
        mPeriodicCallHandler.post(mPeriodicCallRunnable);
    }

    public void onCreate() {
        super.onCreate();
        mEventBus = new EventBus(this);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mMessageGatewayList = new ArrayList<>();
        mPupilManager = new PupilManager(this);
        IntentFilter actionIntentFilter = new IntentFilter();
        actionIntentFilter.addAction(ACTION_BIND_PUPIL);
        registerReceiver(mActionReceiver, actionIntentFilter);
        startForeground(buildNotificationForParentControl(R.string.text_notification));
        mPeriodicCallHandler = new Handler();
        createMessageGatewayForAllPupils();
        startPeriodicCall();
    }

    private void pupilsBind() {
        List<Pupil> pupilList = mPupilManager.getPupilList();
        for (Pupil pupil : pupilList) {
            if (!pupil.isBind) {
                bindPupilIfAble(pupil);
            }
        }
    }

    private void createMessageGatewayForAllPupils() {
        List<Pupil> pupilList = mPupilManager.getPupilList();
        for (Pupil pupil : pupilList) {
            if (pupil.isBind) {
                createMessageGateway(pupil);
            }
        }
    }

    @Nullable
    private Notification buildNotificationForParentControl(@StringRes int textResId) {
        Notification.Builder builder =
                createNotificationBuilder(getString(R.string.title_notification),
                        getString(textResId), getContentIntent());
        return builder.build();
    }

    private PendingIntent getContentIntent() {
        return PendingIntent.getActivity(getApplicationContext(), 0,
                getStartParentControlIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getStartParentControlIntent() {
        return ParentControlSetupWizard.getStartIntent(getApplicationContext(), false);
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

    private MessageGateway createMessageGateway(@NonNull Pupil pupil) {
        return createMessageGateway(new MessageChannel(pupil));
    }

    private MessageGateway createMessageGateway(@NonNull MessageChannel messageChannel) {
        MessageGateway messageGateway = new MessageGateway(messageChannel);
        mMessageGatewayList.add(messageGateway);
        messageGateway.listen(this);
        return messageGateway;
    }

    @Nullable
    private MessageGateway getMessageGateway(@NonNull MessageChannel messageChannel) {
        for (MessageGateway messageGateway : mMessageGatewayList) {
            if (messageGateway.getChannel().getName().equals(messageChannel.getName())) {
                return messageGateway;
            }
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
        mStartId = startId;
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        for (MessageGateway messageGateway : mMessageGatewayList) {
            messageGateway.destroy();
        }
        mMessageGatewayList.clear();
        mNotificationManager.cancelAll();
        stopForeground(true);
        stopSelf(mStartId);
    }

    @Override
    public void onMessageReceive(@NonNull TypedMessage typedMessage,
                                 @NonNull MessageChannel messageChannel) {

        switch (typedMessage.messageType) {

            case InternalCommand.NAME:

                InternalCommand internalCommand = (InternalCommand) typedMessage.data;

                switch (internalCommand.command) {

                    case InternalCommand.BIND_PUPIL_RESPONSE:

                        //getMessageGateway(messageChannel).send(InternalCommand.NAME,
                        //        new InternalCommand(InternalCommand.BIND_PUPIL_OK));
                        Pupil pupil = internalCommand.dataAsPupil();
                        pupil.isBind = true;
                        mPupilManager.update(pupil);
                        mEventBus.sendEvent(EVENT_BIND_PUPIL, NAME_PUPIL_UUID, pupil.getUuid());

                        break;

                }

                break;

        }

    }
}