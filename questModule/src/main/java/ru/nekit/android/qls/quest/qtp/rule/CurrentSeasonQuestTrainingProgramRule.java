package ru.nekit.android.qls.quest.qtp.rule;


import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Calendar;

import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceGroup;
import ru.nekit.android.qls.quest.resourceLibrary.SimpleQuestVisualResourceItem;
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

    int getUnknownIndex(@NonNull QuestResourceLibrary questResourceLibrary,
                        @NonNull QuestVisualRepresentationList questVisualRepresentationList) {
        Calendar calendar = Calendar.getInstance();
        SimpleQuestVisualResourceItem questVisualResourceItem = null;
        int currentMonth = calendar.get(Calendar.MONTH);
        if (currentMonth == 11 || currentMonth <= 1) {
            questVisualResourceItem = SimpleQuestVisualResourceItem.WINTER;
        }
        if (currentMonth > 1 || currentMonth <= 4) {
            questVisualResourceItem = SimpleQuestVisualResourceItem.SPRING;
        }
        if (currentMonth > 4 || currentMonth <= 7) {
            questVisualResourceItem = SimpleQuestVisualResourceItem.SUMMER;
        }
        if (currentMonth > 7 || currentMonth <= 10) {
            questVisualResourceItem = SimpleQuestVisualResourceItem.FALL;
        }
        return questVisualRepresentationList.getIdsList().indexOf(
                questResourceLibrary.getQuestVisualResourceItemId(questVisualResourceItem));
    }

    QuestType getActualQuestType() {
        return QuestType.CURRENT_SEASON;
    }
}