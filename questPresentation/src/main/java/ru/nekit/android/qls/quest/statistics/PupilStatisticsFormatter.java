package ru.nekit.android.qls.quest.statistics;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgram;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgramLevel;

public class PupilStatisticsFormatter {

    private String questStatisticsFormat(@NonNull BaseStatistics questStatistics) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        final Date date = new Date();
        date.setTime(questStatistics.worseAnswerTime);
        final String worstAnswerTimeString = dateFormat.format(date);
        date.setTime(questStatistics.bestAnswerTime == Long.MAX_VALUE ? 0 : questStatistics.bestAnswerTime);
        final String bestAnswerTimeTimeString = dateFormat.format(date);
        return String.format(
                "Правильные/Неправильные ответы: %s/%s\n" +
                        "Серия правильных ответов: %s\n" +
                        "Худшее/лучшее время ответа: %s/%s\n",
                questStatistics.rightAnswerCount,
                questStatistics.wrongAnswerCount,
                questStatistics.rightAnswerSeries,
                worstAnswerTimeString,
                bestAnswerTimeTimeString
        );
    }

    public String format(@NonNull Context context, @NonNull Pupil pupil, @Nullable PupilStatistics pupilStatistics) throws IOException {
        QuestTrainingProgram questTrainingProgram = new QuestTrainingProgram().buildForPupil(context, pupil);
        if (pupilStatistics == null) {
            return "Нет статистики";
        }
        QuestTrainingProgramLevel level = questTrainingProgram.getCurrentLevel(pupilStatistics);
        QuestTrainingProgramLevel lastLevel = questTrainingProgram.getLastLevel();
        List<String> stringList = new ArrayList<>();
        for (QuestStatistics item : pupilStatistics.questStatistics) {
            stringList.add(String.format("Статистика для %s (%s):\n%s",
                    item.questType.getString(context),
                    item.questionType.getString(context),
                    questStatisticsFormat(item)
            ));
        }
        return String.format(
                "Статистика для ученика: %s\nУровень: %s/%s (%s/%s)\n%s\n%s",
                pupil.toString(),
                level.getIndex() + 1,
                lastLevel.getIndex() + 1,
                pupilStatistics.score,
                questTrainingProgram.getLevelAllPoints(pupilStatistics),
                questStatisticsFormat(pupilStatistics),
                TextUtils.join("\n", stringList)
        );
    }
}
