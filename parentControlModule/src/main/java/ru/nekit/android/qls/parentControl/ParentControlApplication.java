package ru.nekit.android.qls.parentControl;

import android.app.Application;

import ru.nekit.android.qls.PreferencesUtil;

public class ParentControlApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesUtil.init(this);
        ParentControlService.start(this);
        //new PupilManager(this).removeAll();
    }
}