package ru.nekit.android.qls;


import ru.nekit.android.qls.pupil.Pupil;

public class MessageChannel {

    private String mName;

    public MessageChannel(Pupil pupil) {
        this(pupil.getUuid());
    }

    public MessageChannel(String pupilUuid) {
        mName = pupilUuid;
    }

    public String getName() {
        return mName;
    }

}