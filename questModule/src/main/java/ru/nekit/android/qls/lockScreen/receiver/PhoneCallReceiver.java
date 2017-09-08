package ru.nekit.android.qls.lockScreen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import java.util.Date;

public class PhoneCallReceiver extends BroadcastReceiver {

    public static final String INCOMING_CALL_RECEIVED = "incoming_call_received";
    public static final String INCOMING_CALL_ENDED = "incoming_call_ended";
    public static final String OUTGOING_CALL_ENDED = "outgoing_call_ended";
    public static final String OUTGOING_CALL_STARTED = "outgoing_call_started";

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.NEW_OUTGOING_CALL".equals(intent.getAction())) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (TelephonyManager.EXTRA_STATE_IDLE.equals(stateStr)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(stateStr)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(stateStr)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }
            onCallStateChanged(context, state, number);
        }
    }

    protected void onIncomingCallReceived(Context context, String number, Date start) {
        context.sendBroadcast(new Intent(INCOMING_CALL_RECEIVED));
    }

    protected void onIncomingCallAnswered(Context context, String number, Date start) {
    }

    protected void onIncomingCallEnded(Context context, String number, Date start, Date end) {
        context.sendBroadcast(new Intent(INCOMING_CALL_ENDED));
    }

    protected void onOutgoingCallStarted(Context context, String number, Date start) {
        context.sendBroadcast(new Intent(OUTGOING_CALL_STARTED));
    }

    protected void onOutgoingCallEnded(Context context, String number, Date start, Date end) {
        context.sendBroadcast(new Intent(OUTGOING_CALL_ENDED));
    }

    protected void onMissedCall(Context context, String number, Date start) {
        context.sendBroadcast(new Intent(INCOMING_CALL_ENDED));
    }

    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallReceived(context, number, callStartTime);
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                } else {
                    isIncoming = true;
                    callStartTime = new Date();
                    onIncomingCallAnswered(context, savedNumber, callStartTime);
                }

                break;

            case TelephonyManager.CALL_STATE_IDLE:
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    onMissedCall(context, savedNumber, callStartTime);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                } else {
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }
}
