package ru.nekit.android.qls.setupWizard;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

interface ISetupWizardHolder {

    BaseSetupWizard getSetupWizard();

    void showSetupWizardStep(ISetupStep step, Object... params);

    Button getNextButton();

    Button getAltButton();

    boolean nextButtonAction();

    void altButtonAction();

    ViewGroup getToolContainer();

    View getView();
}
