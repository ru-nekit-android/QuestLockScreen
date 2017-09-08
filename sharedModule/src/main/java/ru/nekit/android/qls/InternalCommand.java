package ru.nekit.android.qls;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.nekit.android.qls.pupil.Pupil;

public class InternalCommand {

    public static final String NAME_DATA = "data";
    public static final String NAME_COMMAND = "command";
    public static final String NAME_DATA_CLASS = "className";

    public static final String NAME = "command";
    public static final String OPEN = "open";

    public static final String BIND_PUPIL_REQUEST = "bind_pupil_request";
    public static final String BIND_PUPIL_RESPONSE = "bind_pupil_response";
    public static final String BIND_PUPIL_OK = "bind_pupil_ok";

    public static final String PING = "ping";
    public static final String PONG = "pong";

    public String command;
    public Object data;
    private String className;

    public InternalCommand(@NonNull String command, @Nullable Object data) {
        this.command = command;
        this.data = data;
        if (data != null) {
            className = data.getClass().getName();
        }
    }

    public InternalCommand(@NonNull String command) {
        this(command, null);
    }

    public String dataAsString() {
        return (String) data;
    }

    public Pupil dataAsPupil() {
        return (Pupil) data;
    }
}