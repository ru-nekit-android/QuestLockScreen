package ru.nekit.android.qls.pupil;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.nekit.android.qls.quest.qtp.QuestTrainingProgramComplexity;

public class Pupil {

    public static final String NAME_CURRENT = "pupil.current";

    public String name;
    public PupilSex sex;
    public QuestTrainingProgramComplexity complexity;
    public String avatar;
    public boolean isBind;
    private String uuid;
    private List<PhoneContact> phoneContacts;

    public Pupil(@NonNull String uuid) {
        this.uuid = uuid;
    }

    //for test
    public Pupil(@NonNull String uuid, @NonNull String name, boolean isBind) {
        this.uuid = uuid;
        this.name = name;
        this.isBind = isBind;
    }

    public Pupil() {
        this(UUID.randomUUID().toString());
    }

    public String getUuid() {
        return uuid;
    }

    public String toString() {
        return name;
    }

    @NonNull
    public List<PhoneContact> getPhoneContacts() {
        if (phoneContacts == null) {
            phoneContacts = new ArrayList<>();
        }
        return phoneContacts;
    }

    public void setPhoneContacts(@NonNull List<PhoneContact> value) {
        phoneContacts = value;
    }
}
