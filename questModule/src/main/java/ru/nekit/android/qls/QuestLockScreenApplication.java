package ru.nekit.android.qls;

import android.app.Application;

import ru.nekit.android.qls.lockScreen.LockScreen;

public class QuestLockScreenApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesUtil.init(this);
        LockScreen.activateForSetupWizard(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}