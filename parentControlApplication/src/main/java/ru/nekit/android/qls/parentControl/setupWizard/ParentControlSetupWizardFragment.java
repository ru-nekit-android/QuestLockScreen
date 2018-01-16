package ru.nekit.android.qls.parentControl.setupWizard;

import ru.nekit.android.qls.setupWizard.BaseSetupWizardFragment;

public abstract class ParentControlSetupWizardFragment extends BaseSetupWizardFragment {

    @Override
    protected ParentControlSetupWizard getSetupWizard() {
        return (ParentControlSetupWizard) super.getSetupWizard();
    }

}