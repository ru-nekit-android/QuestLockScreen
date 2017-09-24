package ru.nekit.android.qls.quest.statistics;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.quest.persistance.QuestStatisticsSaver;

public class PupilStatisticsManager {

    private QuestStatisticsSaver mQuestStatisticsSaver;

    public PupilStatisticsManager() {
        mQuestStatisticsSaver = new QuestStatisticsSaver();
    }

    public PupilStatistics getPupilStatistics(@NonNull Pupil pupil) {
        return mQuestStatisticsSaver.restore(pupil.getUuid());
    }

    public PupilStatistics getPupilStatistics() {
        return mQuestStatisticsSaver.restore();
    }
}
