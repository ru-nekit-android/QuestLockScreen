package ru.nekit.android.qls.data.repository.store;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.ParameterizedType;

import ru.nekit.android.data.StringKeyBooleanValueStore;
import ru.nekit.android.data.StringKeyStringValueStore;
import ru.nekit.android.domain.repository.IStringKeyValueStore;

//TODO: TO KOTLIN
public abstract class AbstractJsonStateStore<T> {

    private Gson json;
    private IStringKeyValueStore<Boolean> booleanStore;
    private IStringKeyValueStore<String> stringStore;

    public AbstractJsonStateStore(SharedPreferences sharedPreferences) {
        json = new GsonBuilder().create();
        booleanStore = new StringKeyBooleanValueStore(sharedPreferences);
        stringStore = new StringKeyStringValueStore(sharedPreferences);
    }

    private static String HAS_SAVED_STATE(String name, String uid) {
        return String.format("%s:%s.has_saved_state", name, uid);
    }

    private static String SAVED_STATE(String name, String uid) {
        return String.format("%s:%s.saved_state", name, uid);
    }

    public abstract String getName();


    public void save(@NonNull T item, String uuid) {
        saveInternal(item, uuid);
    }

    private void saveInternal(@NonNull T item, String uuid) {
        saveString(json.toJson(item, item.getClass()), uuid);
    }

    protected void saveString(@Nullable String value, String uuid) {
        booleanStore.set(HAS_SAVED_STATE(getName(), uuid), true);
        stringStore.set(SAVED_STATE(getName(), uuid), value);
    }

    public void removeByUuid(String uuid) {
        booleanStore.remove(HAS_SAVED_STATE(getName(), uuid));
        stringStore.remove(SAVED_STATE(getName(), uuid));
    }

    public T restore(@NonNull String uuid) {
        Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return json.fromJson(restoreString(uuid), clazz);
    }

    public String restoreString(@NonNull String uuid) {
        return stringStore.get(SAVED_STATE(getName(), uuid));
    }

    public void clear(@NonNull String uuid) {
        booleanStore.set(HAS_SAVED_STATE(getName(), uuid), false);
    }

    public boolean hasSaved(@NonNull String uuid) {
        return booleanStore.get(HAS_SAVED_STATE(getName(), uuid));
    }

}