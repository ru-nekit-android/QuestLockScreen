package ru.nekit.android.qls.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.ParameterizedType;

import ru.nekit.android.qls.PreferencesUtil;

public abstract class AbstractStateSaver<T> {

    private Gson gson;

    public AbstractStateSaver(@NonNull Context context) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    private static String HAS_SAVED_STATE(String name, String uid) {
        return String.format("%s:%s.has_saved_state", name, uid);
    }

    private static String SAVED_STATE(String name, String uid) {
        return String.format("%s:%s.saved_state", name, uid);
    }

    public abstract String getName();

    protected abstract String getUUID();

    public void save(@NonNull T item) {
        save(item, getUUID());
    }

    protected void saveString(@NonNull String value) {
        saveStringInternal(value, getUUID());
    }

    public void save(@NonNull T item, String uuid) {
        saveInternal(item, uuid);
    }

    private void saveInternal(@NonNull T item, String uuid) {
        saveStringInternal(gson.toJson(item, item.getClass()), uuid);
    }

    private void saveStringInternal(@Nullable String value, String uuid) {
        PreferencesUtil.setBoolean(HAS_SAVED_STATE(getName(), uuid), true);
        PreferencesUtil.setString(SAVED_STATE(getName(), uuid), value);
    }

    public void removeByUuid(String uuid) {
        PreferencesUtil.remove(HAS_SAVED_STATE(getName(), uuid));
        PreferencesUtil.remove(SAVED_STATE(getName(), uuid));
    }

    public T restore(@NonNull String uuid) {
        Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return gson.fromJson(restoreString(uuid), clazz);
    }

    protected String restoreString() {
        return restoreString(getUUID());
    }

    private String restoreString(@NonNull String uuid) {
        return PreferencesUtil.getString(SAVED_STATE(getName(), uuid));
    }

    public T restore() {
        return restore(getUUID());
    }

    private void reset(@NonNull String uuid) {
        PreferencesUtil.setBoolean(HAS_SAVED_STATE(getName(), uuid), false);
    }

    public void reset() {
        reset(getUUID());
    }

    public boolean hasSavedState(@NonNull String uuid) {
        return PreferencesUtil.getBoolean(HAS_SAVED_STATE(getName(), uuid));
    }

    public boolean hasSavedState() {
        return !getUUID().equals("") && PreferencesUtil.getBoolean(HAS_SAVED_STATE(getName(), getUUID()));
    }

}