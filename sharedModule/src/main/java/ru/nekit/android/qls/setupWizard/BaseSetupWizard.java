package ru.nekit.android.qls.setupWizard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.SettingsStorage;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilManager;
import ru.nekit.android.qls.session.Session;
import ru.nekit.android.qls.session.SessionType;

public abstract class BaseSetupWizard {

    public static final int UNLOCK_SECRET_MIN_SIZE = 4;
    private static final String BIND_CODE_PATTERN = "QLS:%s:%s";
    @NonNull
    protected final Context mContext;
    @NonNull
    protected final PupilManager mPupilManager;
    @NonNull
    protected final SettingsStorage mSettingsStorage;
    @NonNull
    private final List<ISetupWizardStep> mStepStack;
    protected ISetupWizardStep mCurrentStep;

    public BaseSetupWizard(@NonNull Context context) {
        mContext = context;
        mStepStack = new ArrayList<>();
        mSettingsStorage = new SettingsStorage();
        mPupilManager = new PupilManager();
    }

    ISetupWizardStep getNextStep() {
        ISetupWizardStep calculatedStep = calculateNextStep();
        if (needLogin(calculatedStep)) {
            mStepStack.clear();
            mStepStack.add(BaseSetupWizardStep.UNLOCK_SECRET);
        }
        if (calculatedStep.needInternetConnection()) {
            if (!internetIsConnected()) {
                mStepStack.add(BaseSetupWizardStep.SETUP_INTERNET_CONNECTION);
            }
        }
        int size = mStepStack.size();
        if (size == 0 || (size > 0 && mStepStack.get(size - 1) != calculatedStep)) {
            mStepStack.add(calculatedStep);
        }
        return mStepStack.remove(0);
    }

    private boolean internetIsConnected() {
        return true;
    }

    @NonNull
    protected abstract ISetupWizardStep calculateNextStep();

    public void setIntroductionIsPresented(boolean value) {
        mSettingsStorage.setIntroductionIsPresented(value);
    }

    protected boolean setupIsCompleted() {
        return mSettingsStorage.setupWizardIsCompleted(getName());
    }

    public boolean setupIsStarted() {
        return mSettingsStorage.setupWizardIsStarted(getName());
    }

    protected abstract String getName();

    public int getQuestSeriesLength() {
        return mSettingsStorage.getQuestSeriesLength();
    }

    public void setQuestSeriesLength(int value) {
        mSettingsStorage.setQuestSeriesLength(value);
    }

    protected void startSetupWizard() {
        mSettingsStorage.startSetupWizard(getName());
    }

    protected void startSetupWizard(boolean value) {
        mSettingsStorage.startSetupWizard(getName(), value);
    }

    protected void completeSetupWizard() {
        mSettingsStorage.completeSetupWizard(getName());
    }

    protected void completeSetupWizard(boolean value) {
        mSettingsStorage.completeSetupWizard(getName(), value);
    }

    @NonNull
    public String createBindCode() {
        Pupil pupil = mPupilManager.getCurrentPupil();
        assert pupil != null;
        return String.format(BIND_CODE_PATTERN, pupil.getUuid(), pupil.name);
    }

    @NonNull
    protected Pupil createPupilFromBindCode(@NonNull String bindCode) {
        String[] values = TextUtils.split(bindCode, ":");
        Pupil pupil = new Pupil(values[1]);
        pupil.name = values[2];
        return pupil;
    }

    boolean needLogin(@NonNull ISetupWizardStep step) {
        return step.needLogin() && !Session.isValid(SessionType.SETUP_WIZARD);
    }

    ISetupWizardStep getCurrentStep() {
        return mCurrentStep;
    }
}