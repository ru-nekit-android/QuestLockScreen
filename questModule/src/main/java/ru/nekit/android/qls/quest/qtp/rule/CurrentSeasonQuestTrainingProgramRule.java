package ru.nekit.android.qls.quest.qtp.rule;


import android.os.Parcel;

import java.util.Calendar;

import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceGroup;
import ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceItem;
import ru.nekit.android.qls.quest.types.shared.QuestVisualRepresentationList;

public class CurrentSeasonQuestTrainingProgramRule extends ChoiceQuestTrainingProgramRule {

    public static final Creator<CurrentSeasonQuestTrainingProgramRule> CREATOR
            = new Creator<CurrentSeasonQuestTrainingProgramRule>() {
        @Override
        public CurrentSeasonQuestTrainingProgramRule createFromParcel(Parcel in) {
            return new CurrentSeasonQuestTrainingProgramRule(in);
        }

        @Override
        public CurrentSeasonQuestTrainingProgramRule[] newArray(int size) {
            return new CurrentSeasonQuestTrainingProgramRule[size];
        }
    };

    public CurrentSeasonQuestTrainingProgramRule() {

    }

    public CurrentSeasonQuestTrainingProgramRule(Parcel in) {
        super(in);
    }

    @Override
    QuestVisualResourceGroup getActualGroup() {
        return QuestVisualResourceGroup.SEASONS;
    }

    int getUnknownIndex(QuestVisualRepresentationList questVisualRepresentationList) {
        Calendar calendar = Calendar.getInstance();
        QuestVisualResourceItem questVisualResourceItem = null;
        int currentMonth = calendar.get(Calendar.MONTH);
        if (currentMonth == 11 || currentMonth <= 1) {
            questVisualResourceItem = QuestVisualResourceItem.WINTER;
        }
        if (currentMonth > 1 || currentMonth <= 4) {
            questVisualResourceItem = QuestVisualResourceItem.SPRING;
        }
        if (currentMonth > 4 || currentMonth <= 7) {
            questVisualResourceItem = QuestVisualResourceItem.SUMMER;
        }
        if (currentMonth > 7 || currentMonth <= 10) {
            questVisualResourceItem = QuestVisualResourceItem.FALL;
        }
        return questVisualRepresentationList.getIdsList().indexOf(questVisualResourceItem.getId());
    }

    QuestType getActualQuestType() {
        return QuestType.CURRENT_SEASON;
    }
}
