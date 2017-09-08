package ru.nekit.android.qls.setupWizard;


public interface ISetupStep {

    int getFlags();

    boolean needLogin();

}
