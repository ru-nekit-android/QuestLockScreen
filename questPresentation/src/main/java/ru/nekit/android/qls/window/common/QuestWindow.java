package ru.nekit.android.qls.window.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.window.Window;
import ru.nekit.android.qls.window.WindowContentViewHolder;
import ru.nekit.android.qls.window.WindowListener;

public class QuestWindow extends Window {

    public static final String VALUE_WINDOW_NAME = "ru.nekit.android.qls.value_window_name";
    public static final String EVENT_WINDOW_OPEN = "ru.nekit.android.qls.event_window_open";
    public static final String EVENT_WINDOW_OPENED = "ru.nekit.android.qls.event_window_opened";
    public static final String EVENT_WINDOW_CLOSE = "ru.nekit.android.qls.event_window_close";
    public static final String EVENT_WINDOW_CLOSED = "ru.nekit.android.qls.event_window_closed";

    private static WindowListener mWindowListener = new WindowListener() {

        @Override
        public void onWindowOpen(@NonNull Window window) {
            sendEvent(window.getName(), EVENT_WINDOW_OPEN);
        }

        @Override
        public void onWindowOpened(@NonNull Window window) {
            sendEvent(window.getName(), EVENT_WINDOW_OPENED);
        }

        @Override
        public void onWindowClose(@NonNull Window window) {
            sendEvent(window.getName(), EVENT_WINDOW_CLOSE);
        }

        @Override
        public void onWindowClosed(@NonNull Window window) {
            sendEvent(window.getName(), EVENT_WINDOW_CLOSED);
        }
    };

    public QuestWindow(@NonNull QuestContext context,
                       @Nullable String name,
                       @Nullable WindowContentViewHolder content,
                       @StyleRes int styleResId) {
        super(context, name, mWindowListener, content, styleResId);
    }

    private static void sendEvent(@NonNull String windowName, @NonNull String eventName) {
        QuestContext.getInstance().getEventBus().sendEvent(eventName, VALUE_WINDOW_NAME, windowName);
    }

}