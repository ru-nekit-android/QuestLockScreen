package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.List;

import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resourceLibrary.SimpleQuestVisualResourceModel;
import ru.nekit.android.qls.quest.resourceLibrary.VisualResourceGroup;

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
    VisualResourceGroup getTargetGroup() {
        return VisualResourceGroup.SEASONS;
    }

    int getUnknownIndex(@NonNull QuestResourceLibrary questResourceLibrary,
                        @NonNull List<Integer> questVisualRepresentationList) {
        Calendar calendar = Calendar.getInstance();
        SimpleQuestVisualResourceModel questVisualResourceItem = null;
        int currentMonth = calendar.get(Calendar.MONTH);
        if (currentMonth == 11 || currentMonth <= 1) {
            questVisualResourceItem = SimpleQuestVisualResourceModel.WINTER;
        }
        if (currentMonth > 1 || currentMonth <= 4) {
            questVisualResourceItem = SimpleQuestVisualResourceModel.SPRING;
        }
        if (currentMonth > 4 || currentMonth <= 7) {
            questVisualResourceItem = SimpleQuestVisualResourceModel.SUMMER;
        }
        if (currentMonth > 7 || currentMonth <= 10) {
            questVisualResourceItem = SimpleQuestVisualResourceModel.FALL;
        }
        return questVisualRepresentationList.indexOf(
                questResourceLibrary.getQuestVisualResourceItemId(questVisualResourceItem));
    }

    @Override
    public QuestType getQuestType() {
        return QuestType.CURRENT_SEASON;
    }
}