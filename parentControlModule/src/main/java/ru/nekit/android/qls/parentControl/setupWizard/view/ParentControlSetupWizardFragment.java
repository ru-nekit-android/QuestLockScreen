package ru.nekit.android.qls.parentControl.setupWizard.view;

import ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard;
import ru.nekit.android.qls.setupWizard.BaseSetupWizardFragment;


public abstract class ParentControlSetupWizardFragment extends BaseSetupWizardFragment {

    @Override
    protected ParentControlSetupWizard getSetupWizard() {
        return (ParentControlSetupWizard) super.getSetupWizard();
    }

}
