package ru.nekit.android.qls.quest.statistics;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.quest.persistance.QuestStatisticsSaver;

public class PupilStatisticsManager {

    private QuestStatisticsSaver mQuestStatisticsSaver;

    public PupilStatisticsManager(@NonNull Context context) {
        mQuestStatisticsSaver = new QuestStatisticsSaver(context);
    }

    public PupilStatistics getPupilStatistics(@NonNull Pupil pupil) {
        return mQuestStatisticsSaver.restore(pupil.getUuid());
    }

    public PupilStatistics getPupilStatistics() {
        return mQuestStatisticsSaver.restore();
    }
}
