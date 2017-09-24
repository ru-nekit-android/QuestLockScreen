package ru.nekit.android.qls.setupWizard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import ru.nekit.android.qls.SettingsStorage;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilManager;
import ru.nekit.android.qls.session.Session;
import ru.nekit.android.qls.session.SessionType;

public abstract class BaseSetupWizard {

    public static final int UNLOCK_SECRET_MIN_SIZE = 4;
    private static final String BIND_CODE_PATTERN = "%s%s%s";
    private static final String BIND_CODE_SEPARATOR = ":";
    @NonNull
    protected final Context mContext;
    @NonNull
    protected final PupilManager mPupilManager;
    @NonNull
    protected final SettingsStorage mSettingsStorage;
    protected ISetupStep mCurrentSetupStep;
    private ISetupStep mExpectedSetupStep;

    public BaseSetupWizard(@NonNull Context context) {
        mContext = context;
        mSettingsStorage = new SettingsStorage();
        mPupilManager = new PupilManager();
    }

    final ISetupStep getExpectedSetupStep() {
        return mExpectedSetupStep;
    }

    ISetupStep getNextStep() {
        return redirectToLoginIfNeed(calculateNextStep());
    }

    ISetupStep redirectToLoginIfNeed(@NonNull ISetupStep step) {
        if (needLogin(step)) {
            if (mExpectedSetupStep != null) {
                step = mExpectedSetupStep;
                mExpectedSetupStep = null;
            }
            return step;
        }
        if (step.needLogin()) {
            mExpectedSetupStep = step;
        }
        return BaseSetupStep.ENTER_UNLOCK_SECRET;
    }

    protected boolean needLogin(ISetupStep step) {
        return step.needLogin()
                || !Session.isValid(mContext, SessionType.SETUP_WIZARD);
    }

    @NonNull
    protected abstract ISetupStep calculateNextStep();

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

    public String createBindCode() {
        Pupil pupil = mPupilManager.getCurrentPupil();
        assert pupil != null;
        return String.format(BIND_CODE_PATTERN, pupil.getUuid(), BIND_CODE_SEPARATOR, pupil.name);
    }

    protected Pupil createPupilFromBindCode(@NonNull String bindCode) {
        String[] values = TextUtils.split(bindCode, BIND_CODE_SEPARATOR);
        Pupil pupil = new Pupil(values[0]);
        pupil.name = values[1];
        return pupil;
    }
}