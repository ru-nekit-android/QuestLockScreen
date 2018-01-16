package ru.nekit.android.qls.setupWizard;


public enum BaseSetupWizardStep implements ISetupWizardStep {

    UNLOCK_SECRET(0),
    SETUP_INTERNET_CONNECTION(0);

    private int mFlags;

    BaseSetupWizardStep(int flags) {
        mFlags = flags;
    }

    @Override
    public int getFlags() {
        return mFlags;
    }

    @Override
    public boolean needLogin() {
        return false;
    }

    @Override
    public boolean needInternetConnection() {
        return false;
    }
}