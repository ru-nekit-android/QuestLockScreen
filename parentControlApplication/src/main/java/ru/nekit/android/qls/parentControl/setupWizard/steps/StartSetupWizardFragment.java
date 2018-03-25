package ru.nekit.android.qls.parentControl.setupWizard.steps;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.parentControl.R;
import ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizardFragment;

public class StartSetupWizardFragment extends ParentControlSetupWizardFragment {

    public static StartSetupWizardFragment getInstance() {
        return new StartSetupWizardFragment();
    }

    @LayoutRes
    @Override
    protected int getLayoutId() {
        return R.layout.sw_setup_start;
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        setNextButtonText(getSetupWizard().setupIsStart() ?
                R.string.label_setup_wizard_continue : R.string.label_start_setup_wizard);
    }
}
