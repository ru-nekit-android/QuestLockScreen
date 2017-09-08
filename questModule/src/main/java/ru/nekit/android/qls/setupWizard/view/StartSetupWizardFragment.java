package ru.nekit.android.qls.setupWizard.view;

import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.R;

public class StartSetupWizardFragment extends QuestSetupWizardFragment {

    public static StartSetupWizardFragment getInstance() {
        return new StartSetupWizardFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_setup_start;
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        setNextButtonText(getSetupWizard().setupIsStarted() ?
                R.string.label_setup_wizard_continue : R.string.label_start_setup_wizard);
    }
}
