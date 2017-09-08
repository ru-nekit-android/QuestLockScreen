package ru.nekit.android.qls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class EventBus {

    private Map<String, BroadcastReceiver> mReceiverMap;
    private Context mContext;

    public EventBus(@NonNull Context context) {
        mContext = context;
        mReceiverMap = new HashMap<>();
    }

    public void sendEvent(@NonNull String event) {
        mContext.sendBroadcast(new Intent(event));
    }

    public void sendEvent(@NonNull String event, @NonNull String dataName, @NonNull Parcelable data) {
        Intent intent = new Intent(event);
        intent.putExtra(dataName, data);
        mContext.sendBroadcast(intent);
    }

    public void sendEvent(@NonNull String event, @NonNull String dataName, @NonNull Parcelable[] data) {
        Intent intent = new Intent(event);
        intent.putExtra(dataName, data);
        mContext.sendBroadcast(intent);
    }

    public void sendEvent(@NonNull String event, @NonNull String dataName, @NonNull String data) {
        Intent intent = new Intent(event);
        intent.putExtra(dataName, data);
        mContext.sendBroadcast(intent);
    }

    public void sendEvent(@NonNull String event, @NonNull String dataName, long data) {
        Intent intent = new Intent(event);
        intent.putExtra(dataName, data);
        mContext.sendBroadcast(intent);
    }

    public void sendEvent(@NonNull String event, @NonNull String dataName, boolean data) {
        Intent intent = new Intent(event);
        intent.putExtra(dataName, data);
        mContext.sendBroadcast(intent);
    }

    public void sendEvent(@NonNull Intent intent) {
        mContext.sendBroadcast(intent);
    }

    public void handleEvents(@NonNull final IEventHandler eventHandler,
                             @NonNull String... events) {
        if (!mReceiverMap.containsKey(eventHandler.getEventBusName())) {
            mReceiverMap.put(eventHandler.getEventBusName(), new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    eventHandler.onEvent(intent);
                }
            });
            IntentFilter intentFilter = new IntentFilter();
            for (String event : events) {
                intentFilter.addAction(event);
            }
            mContext.registerReceiver(mReceiverMap.get(eventHandler.getEventBusName()), intentFilter);
        }
    }

    public void stopHandleEvents(@NonNull final IEventHandler eventHandler) {

        mContext.unregisterReceiver(mReceiverMap.remove(eventHandler.getEventBusName()));
    }

    public interface IEventHandler {

        void onEvent(@NonNull Intent intent);

        @NonNull
        String getEventBusName();
    }
}
