package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;

public class CurrentTimeQuestTrainingProgramRule extends TimeQuestTrainingProgramRule {

    public static final Creator<CurrentTimeQuestTrainingProgramRule> CREATOR
            = new Creator<CurrentTimeQuestTrainingProgramRule>() {
        @Override
        public CurrentTimeQuestTrainingProgramRule createFromParcel(Parcel in) {
            return new CurrentTimeQuestTrainingProgramRule(in);
        }

        @Override
        public CurrentTimeQuestTrainingProgramRule[] newArray(int size) {
            return new CurrentTimeQuestTrainingProgramRule[size];
        }
    };

    public CurrentTimeQuestTrainingProgramRule() {
        accuracy = 0;
    }

    private CurrentTimeQuestTrainingProgramRule(Parcel in) {
        super(in);
    }
}
