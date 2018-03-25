package ru.nekit.android.qls.quest.statistics;

public class PupilStatisticsFormatter {

    //TODO: IMPLEMENT IF NEED
    /*private String questStatisticsFormat(@NonNull BaseStatistics questStatisticsReport) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        final Date date = new Date();
        date.setTime(questStatisticsReport.worseAnswerTime);
        final String worstAnswerTimeString = dateFormat.format(date);
        date.setTime(questStatisticsReport.bestAnswerTime == Long.MAX_VALUE ? 0 : questStatisticsReport.bestAnswerTime);
        final String bestAnswerTimeTimeString = dateFormat.format(date);
        return String.format(
                "Правильные/Неправильные ответы: %s/%s\n" +
                        "Серия правильных ответов: %s\n" +
                        "Худшее/лучшее время ответа: %s/%s\n",
                questStatisticsReport.rightAnswerCount,
                questStatisticsReport.wrongAnswerCount,
                questStatisticsReport.rightAnswerSeries,
                worstAnswerTimeString,
                bestAnswerTimeTimeString
        );
    }

    public String format(@NonNull Context context, @NonNull Pupil pupil, @Nullable PupilStatistics listenPupilStatistics) throws IOException {
        QuestTrainingProgram questTrainingProgram = new QuestTrainingProgram().buildForPupil(context, pupil);
        if (listenPupilStatistics == null) {
            return "Нет статистики";
        }
        QuestTrainingProgramLevel level = questTrainingProgram.getCurrentLevel(listenPupilStatistics);
        QuestTrainingProgramLevel lastLevel = questTrainingProgram.getLastLevel();
        List<String> stringList = new ArrayList<>();
        for (QuestStatistics item : listenPupilStatistics.questStatisticsReport) {
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
                listenPupilStatistics.score,
                questTrainingProgram.getLevelAllPoints(listenPupilStatistics),
                questStatisticsFormat(listenPupilStatistics),
                TextUtils.join("\n", stringList)
        );
    }*/
}
