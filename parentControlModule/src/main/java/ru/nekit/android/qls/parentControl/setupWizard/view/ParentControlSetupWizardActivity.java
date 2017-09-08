package ru.nekit.android.qls.parentControl.setupWizard.view;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard;
import ru.nekit.android.qls.setupWizard.BaseSetupWizard;
import ru.nekit.android.qls.setupWizard.BaseSetupWizardActivity;
import ru.nekit.android.qls.setupWizard.ISetupStep;

public class ParentControlSetupWizardActivity extends BaseSetupWizardActivity {

    @Override
    public ParentControlSetupWizard getSetupWizard() {
        return (ParentControlSetupWizard) super.getSetupWizard();
    }

    @Override
    protected BaseSetupWizard createSetupWizard(@NonNull Context context) {
        return new ParentControlSetupWizard(context);
    }

    @Override
    public void showSetupWizardStep(@NonNull ISetupStep step, Object... params) {
        getSetupWizard().setCurrentSetupStep(step);
        if (ParentControlSetupWizard.Step.START.equals(step)) {
            replaceFragment(StartSetupWizardFragment.getInstance());
        } else if (ParentControlSetupWizard.Step.CAMERA_PERMISSION.equals(step)) {
            replaceFragment(CameraPermissionFragment.getInstance());
        } else if (ParentControlSetupWizard.Step.PUPIL_LIST.equals(step)) {
            replaceFragment(PupilListFragment.getInstance());
        } else if (ParentControlSetupWizard.Step.BIND_PUPIL.equals(step)) {
            replaceFragment(BindPupilFragment.getInstance());
        } else if (ParentControlSetupWizard.Step.PUPIL_INFORMATION.equals(step)) {
            replaceFragment(PupilInformationFragment.getInstance(params));
        }
    }
}