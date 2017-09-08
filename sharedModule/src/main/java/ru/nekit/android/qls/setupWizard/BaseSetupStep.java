package ru.nekit.android.qls.setupWizard;


public enum BaseSetupStep implements ISetupStep {

    ENTER_UNLOCK_SECRET(0);

    private int mFlags;

    BaseSetupStep(int flags) {
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
}
