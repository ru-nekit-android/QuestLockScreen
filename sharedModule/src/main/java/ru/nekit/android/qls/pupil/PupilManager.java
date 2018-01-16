package ru.nekit.android.qls.pupil;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.PreferencesUtil;
import ru.nekit.android.qls.quest.persistance.PupilSaver;

public class PupilManager {

    public static final String EVENT_SET_CURRENT_PUPIL = "pupil.event_set_current";
    private static final String NAME_PUPIL_UUID_LIST = "pupil.uuid_list";

    @NonNull
    private PupilSaver mPupilSaver;

    public PupilManager() {
        mPupilSaver = new PupilSaver();
    }

    public void setAsCurrent(@NonNull Pupil pupil) {
        mPupilSaver.setAsCurrent(pupil);
    }

    private void sendSetCurrentPupilEvent(@NonNull EventBus eventBus, @NonNull Pupil pupil) {
        Intent intent = new Intent(EVENT_SET_CURRENT_PUPIL);
        intent.putExtra(Pupil.NAME_CURRENT, pupil.getUuid());
        eventBus.sendEvent(intent);
    }

    private String getCurrentPupilUuid() {
        return mPupilSaver.getCurrentPupilUuid();
    }

    @Nullable
    public Pupil getCurrentPupil() {
        return mPupilSaver.restore(getCurrentPupilUuid());
    }

    @NonNull
    public List<Pupil> getPupilList() {
        List<Pupil> pupilList = new ArrayList<>();
        List<String> pupilUuidList = getPupilUuidList();
        for (String pupilUuid : pupilUuidList) {
            Pupil pupilItem = mPupilSaver.restore(pupilUuid);
            pupilList.add(pupilItem);
        }
        return pupilList;
    }

    public Pupil getPupilByUuid(@NonNull String value) {
        if (pupilIsExist(value)) {
            return mPupilSaver.restore(value);
        }
        return null;
    }

    private boolean pupilIsExist(@NonNull String value) {
        List<String> pupilUuidList = getPupilUuidList();
        for (String pupilUuid : pupilUuidList) {
            if (pupilUuid.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private List<String> getPupilUuidList() {
        List<String> pupilUuidList = new ArrayList<>();
        String pupilJsonString = PreferencesUtil.getString(NAME_PUPIL_UUID_LIST);
        if (pupilJsonString != null && !"".equals(pupilJsonString)) {
            JsonArray pupilJsonArray = new JsonParser().parse(pupilJsonString).getAsJsonArray();
            for (JsonElement pupilUuid : pupilJsonArray) {
                pupilUuidList.add(pupilUuid.getAsString());
            }
        }
        return pupilUuidList;
    }

    public boolean addIfAble(@NonNull String pupilUuid) {
        return addIfAble(new Pupil(pupilUuid), true);
    }

    public boolean addIfAble(@NonNull Pupil pupil) {
        return addIfAble(pupil, true);
    }

    public boolean addIfAbleAndNotify(@NonNull Pupil pupil, boolean asCurrent,
                                      @NonNull EventBus eventBus) {
        boolean able = addIfAble(pupil, asCurrent);
        sendSetCurrentPupilEvent(eventBus, pupil);
        return able;
    }

    private boolean addIfAble(@NonNull Pupil pupil, boolean asCurrent) {
        boolean pupilIsExist = pupilIsExist(pupil.getUuid());
        List<String> pupilUuidList = getPupilUuidList();
        if (!pupilIsExist) {
            pupilUuidList.add(pupil.getUuid());
            updatePupilList(pupilUuidList);
            mPupilSaver.save(pupil, asCurrent);
        }
        return !pupilIsExist;
    }

    private void updatePupilList(List<String> pupilUuidList) {
        JsonArray pupilJsonArray = new JsonArray();
        for (String pupilUuidItem : pupilUuidList) {
            pupilJsonArray.add(pupilUuidItem);
        }
        PreferencesUtil.setString(NAME_PUPIL_UUID_LIST, pupilJsonArray.toString());
    }

    public void update(Pupil pupil) {
        mPupilSaver.save(pupil, pupil.getUuid().equals(getCurrentPupilUuid()));
    }

    //for test
    public void removeAll() {
        mPupilSaver.reset();
        PreferencesUtil.setString(NAME_PUPIL_UUID_LIST, null);
    }

    public void remove(String pupilUuid) {
        mPupilSaver.removeByUuid(pupilUuid);
        List<String> pupilUuidList = getPupilUuidList();
        pupilUuidList.remove(pupilUuid);
        updatePupilList(pupilUuidList);
    }
}