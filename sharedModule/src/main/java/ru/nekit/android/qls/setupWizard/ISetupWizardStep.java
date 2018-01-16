package ru.nekit.android.qls.setupWizard;


public interface ISetupWizardStep {

    int getFlags();

    boolean needLogin();

    boolean needInternetConnection();

}