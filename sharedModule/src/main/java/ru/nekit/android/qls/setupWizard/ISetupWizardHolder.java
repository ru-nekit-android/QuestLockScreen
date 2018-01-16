package ru.nekit.android.qls.setupWizard;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

interface ISetupWizardHolder {

    BaseSetupWizard getSetupWizard();

    void showSetupWizardStep(ISetupWizardStep step, Object... params);

    Button getNextButton();

    Button getAltButton();

    boolean nextAction();

    void setUnconditionedNextAction(boolean value);

    void altAction();

    ViewGroup getToolContainer();

    View getView();
}
