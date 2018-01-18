package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.List;

import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.resources.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resources.collections.SimpleQuestVisualQuestResourceCollection;
import ru.nekit.android.qls.quest.resources.collections.VisualQuestResourceGroupCollection;

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
    VisualQuestResourceGroupCollection getTargetGroup() {
        return VisualQuestResourceGroupCollection.SEASONS;
    }

    int getUnknownIndex(@NonNull QuestResourceLibrary questResourceLibrary,
                        @NonNull List<Integer> questVisualRepresentationList) {
        Calendar calendar = Calendar.getInstance();
        SimpleQuestVisualQuestResourceCollection questVisualResourceItem = null;
        int currentMonth = calendar.get(Calendar.MONTH);
        if (currentMonth == 11 || currentMonth <= 1) {
            questVisualResourceItem = SimpleQuestVisualQuestResourceCollection.WINTER;
        }
        if (currentMonth > 1 && currentMonth <= 4) {
            questVisualResourceItem = SimpleQuestVisualQuestResourceCollection.SPRING;
        }
        if (currentMonth > 4 && currentMonth <= 7) {
            questVisualResourceItem = SimpleQuestVisualQuestResourceCollection.SUMMER;
        }
        if (currentMonth > 7 && currentMonth <= 10) {
            questVisualResourceItem = SimpleQuestVisualQuestResourceCollection.FALL;
        }
        return questVisualRepresentationList.indexOf(
                questResourceLibrary.getQuestVisualResourceId(questVisualResourceItem));
    }

    @Override
    public QuestType getQuestType() {
        return QuestType.CURRENT_SEASON;
    }
}