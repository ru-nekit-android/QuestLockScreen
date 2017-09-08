package ru.nekit.android.qls;

import android.support.annotation.NonNull;

public class SettingsStorage extends PreferencesUtil {

    private static final String RIGHT_ANSWER_CONGRATULATION_IS_PRESENTED =
            "right_answer_congratulation_is_presented";
    private static final String INTRODUCTION_IS_PRESENTED = "introduction_is_presented";
    private static final String ADVERT_IS_PRESENTED = "advert_is_presented";
    private static final String QUEST_SERIES_LENGTH = "quest_series_length";
    private static final String SETUP_IS_STARTED = "setup_wizard.is_started";
    private static final String SETUP_IS_COMPLETED = "setup_wizard.is_completed";

    public boolean introductionIsPresented() {
        return getBoolean(INTRODUCTION_IS_PRESENTED, CONST.INTRODUCTION_IS_PRESENTED_BY_DEFAULT);
    }

    private String createParameter(@NonNull String prefix, @NonNull String name) {
        return String.format("%s_%s", prefix, name);
    }

    public boolean setupWizardIsStarted(@NonNull String prefix) {
        return getBoolean(createParameter(prefix, SETUP_IS_STARTED));
    }

    public boolean setupWizardIsCompleted(@NonNull String prefix) {
        return getBoolean(createParameter(prefix, SETUP_IS_COMPLETED));
    }

    public void completeSetupWizard(@NonNull String prefix) {
        completeSetupWizard(prefix, true);
    }

    public void completeSetupWizard(@NonNull String prefix, boolean value) {
        setBoolean(createParameter(prefix, SETUP_IS_COMPLETED), value);
    }

    public void startSetupWizard(@NonNull String prefix) {
        startSetupWizard(prefix, true);
    }

    public void startSetupWizard(@NonNull String prefix, boolean value) {
        setBoolean(createParameter(prefix, SETUP_IS_STARTED), value);
    }

    public boolean advertIsPresented() {
        return getBoolean(ADVERT_IS_PRESENTED,
                CONST.ADVERT_IS_PRESENTED_BY_DEFAULT);
    }

    public void setIntroductionIsPresented(boolean value) {
        setBoolean(INTRODUCTION_IS_PRESENTED, value);
    }

    public void setRightAnswerCongratulationIsPresented(boolean value) {
        setBoolean(RIGHT_ANSWER_CONGRATULATION_IS_PRESENTED, value);
    }

    public boolean rightAnswerCongratulationIsPresented() {
        return getBoolean(RIGHT_ANSWER_CONGRATULATION_IS_PRESENTED,
                CONST.RIGHT_ANSWER_CONGRATULATION_IS_PRESENTED_BY_DEFAULT);
    }

    public int getQuestSeriesLength() {
        return Math.max(CONST.QUEST_SERIES_LENGTH_BY_DEFAULT,
                getInt(QUEST_SERIES_LENGTH));
    }

    public void setQuestSeriesLength(int value) {
        setInt(QUEST_SERIES_LENGTH, value);
    }

    public float getQuestTrainingProgramVersion() {
        return getFloat(QuestTrainingProgramConst.NAME_VERSION,
                QuestTrainingProgramConst.DEFAULT_VERSION);
    }

    public void setQuestTrainingProgramVersion(float version) {
        setFloat(QuestTrainingProgramConst.NAME_VERSION, version);
    }
}