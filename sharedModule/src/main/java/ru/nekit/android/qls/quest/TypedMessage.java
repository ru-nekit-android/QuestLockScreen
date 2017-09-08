package ru.nekit.android.qls.quest;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class TypedMessage {

    public static final String NAME_TYPE = "messageType";
    public static final String NAME_DATA = "data";

    @SerializedName(NAME_TYPE)
    public String messageType;

    @SerializedName(NAME_DATA)
    public Object data;

    public TypedMessage(@NonNull String messageType, @NonNull Object data) {
        this.messageType = messageType;
        this.data = data;
    }

}