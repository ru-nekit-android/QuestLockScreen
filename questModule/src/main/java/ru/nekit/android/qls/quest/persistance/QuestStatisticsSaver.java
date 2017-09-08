package ru.nekit.android.qls.quest.persistance;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.PreferencesUtil;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.quest.statistics.PupilStatistics;
import ru.nekit.android.qls.utils.AbstractStateSaver;

public class QuestStatisticsSaver extends AbstractStateSaver<PupilStatistics> {

    private static final String SOURCE_NAME = "quest.statistics";

    private String name;

    public QuestStatisticsSaver(@NonNull Context context) {
        this(context, SOURCE_NAME);
    }

    private QuestStatisticsSaver(@NonNull Context context, @NonNull String name) {
        super(context);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected String getUUID() {
        return PreferencesUtil.getString(Pupil.NAME_CURRENT);
    }

}