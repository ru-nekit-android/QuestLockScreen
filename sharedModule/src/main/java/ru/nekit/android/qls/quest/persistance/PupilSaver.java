package ru.nekit.android.qls.quest.persistance;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.PreferencesUtil;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.utils.AbstractStateSaver;

public class PupilSaver extends AbstractStateSaver<Pupil> {

    @Override
    public String getName() {
        return "pupil";
    }

    @Override
    protected String getUUID() {
        return PreferencesUtil.getString(Pupil.NAME_CURRENT);
    }

    public void save(@NonNull Pupil pupil, boolean setAsCurrent) {
        if (setAsCurrent) {
            setAsCurrent(pupil);
        }
        super.save(pupil);
    }

    public void setAsCurrent(@NonNull Pupil pupil) {
        PreferencesUtil.setString(Pupil.NAME_CURRENT, pupil.getUuid());
    }

    public String getCurrentPupilUuid() {
        return getUUID();
    }
}