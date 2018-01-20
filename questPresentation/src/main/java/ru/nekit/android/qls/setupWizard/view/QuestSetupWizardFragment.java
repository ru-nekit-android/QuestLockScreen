package ru.nekit.android.qls.setupWizard.view;

import ru.nekit.android.qls.setupWizard.BaseSetupWizardFragment;
import ru.nekit.android.qls.setupWizard.QuestSetupWizard;


public abstract class QuestSetupWizardFragment extends BaseSetupWizardFragment {

    @Override
    protected QuestSetupWizard getSetupWizard() {
        return (QuestSetupWizard) super.getSetupWizard();
    }

}
