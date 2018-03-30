package ru.nekit.android.qls.lockScreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager.*
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.domain.event.IEventSender
import ru.nekit.android.qls.QuestLockScreenApplication
import java.util.*

class PhoneCallReceiver : BroadcastReceiver() {

    private fun eventSender(context: Context): IEventSender =
            (context.applicationContext as QuestLockScreenApplication).getEventSender()


    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras!!
        if (Intent.ACTION_NEW_OUTGOING_CALL == intent.action) {
            savedNumber = extras.getString(Intent.EXTRA_PHONE_NUMBER)
        } else {
            val stateString = extras.getString(EXTRA_STATE)
            val number = extras.getString(EXTRA_INCOMING_NUMBER)
            var state = 0
            when (stateString) {
                EXTRA_STATE_IDLE -> state = CALL_STATE_IDLE
                EXTRA_STATE_OFFHOOK -> state = CALL_STATE_OFFHOOK
                EXTRA_STATE_RINGING -> state = CALL_STATE_RINGING
            }
            onCallStateChanged(context, state, number)
        }
    }

    private fun onIncomingCallReceived(context: Context, number: String?, start: Date?) {
        eventSender(context).send(PhoneEvent.INCOMING_CALL_START)
    }

    private fun onIncomingCallAnswered(context: Context, number: String?, start: Date?) {}

    private fun onIncomingCallEnded(context: Context, number: String?, start: Date?, end: Date?) {
        eventSender(context).send(PhoneEvent.INCOMING_CALL_COMPLETE)
    }

    private fun onOutgoingCallStarted(context: Context, number: String?, start: Date?) {
        eventSender(context).send(PhoneEvent.OUTGOING_CALL_START)
    }

    private fun onOutgoingCallEnded(context: Context, number: String?, start: Date?, end: Date?) {
        eventSender(context).send(PhoneEvent.OUTGOING_CALL_COMPLETE)
    }

    private fun onMissedCall(context: Context, number: String?, start: Date?) {
        eventSender(context).send(PhoneEvent.INCOMING_CALL_COMPLETE)
    }

    fun onCallStateChanged(context: Context, state: Int, number: String?) {
        if (lastState == state) {
            return
        }
        when (state) {
            CALL_STATE_RINGING -> {
                isIncoming = true
                callStartTime = Date()
                savedNumber = number
                onIncomingCallReceived(context, number, callStartTime)
            }

            CALL_STATE_OFFHOOK -> if (lastState != CALL_STATE_RINGING) {
                isIncoming = false
                callStartTime = Date()
                onOutgoingCallStarted(context, savedNumber, callStartTime)
            } else {
                isIncoming = true
                callStartTime = Date()
                onIncomingCallAnswered(context, savedNumber, callStartTime)
            }

            CALL_STATE_IDLE -> when {
                lastState == CALL_STATE_RINGING -> onMissedCall(context, savedNumber, callStartTime)
                isIncoming -> onIncomingCallEnded(context, savedNumber, callStartTime, Date())
                else -> onOutgoingCallEnded(context, savedNumber, callStartTime, Date())
            }
        }
        lastState = state
    }

    companion object {


        private var lastState = CALL_STATE_IDLE
        private var callStartTime: Date? = null
        private var isIncoming: Boolean = false
        private var savedNumber: String? = null
    }

    enum class PhoneEvent : IEvent {

        INCOMING_CALL_START,
        INCOMING_CALL_COMPLETE,
        OUTGOING_CALL_START,
        OUTGOING_CALL_COMPLETE;

        override val eventName: String = "${javaClass.name}::$name"

    }
}
