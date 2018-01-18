package ru.nekit.android.qls.quest.qtp;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.Quest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.qtp.rule.AbstractQuestTrainingProgramRule;

public class AppropriateQuestTrainingProgramRuleWrapper {

    static int BASE_CHANCE = 100;

    @NonNull
    public QuestType questType;
    @NonNull
    public QuestionType questionType;
    int lowerValue, upperValue;
    double chanceValue;
    @NonNull
    private AbstractQuestTrainingProgramRule qtpRule;

    AppropriateQuestTrainingProgramRuleWrapper(@NonNull AbstractQuestTrainingProgramRule qtpRule,
                                               @NonNull QuestType questType,
                                               @NonNull QuestionType questionType,
                                               double startPriority) {
        this.questType = questType;
        this.questionType = questionType;
        this.qtpRule = qtpRule;
        this.chanceValue = startPriority * BASE_CHANCE;
    }

    public Quest makeQuest(@NonNull QuestContext context) {
        return qtpRule.makeQuest(context, questionType);
    }

    @NonNull
    public AbstractQuestTrainingProgramRule getQtpRule() {
        return qtpRule;
    }
}