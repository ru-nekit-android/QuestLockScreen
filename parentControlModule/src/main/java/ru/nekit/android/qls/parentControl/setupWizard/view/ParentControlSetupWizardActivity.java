package ru.nekit.android.qls.parentControl.setupWizard.view;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard;
import ru.nekit.android.qls.setupWizard.BaseSetupWizard;
import ru.nekit.android.qls.setupWizard.BaseSetupWizardActivity;
import ru.nekit.android.qls.setupWizard.ISetupStep;

import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.Step.BIND_PUPIL;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.Step.CAMERA_PERMISSION;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.Step.PUPIL_INFORMATION;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.Step.PUPIL_LIST;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.Step.START;

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
        if (START.equals(step)) {
            replaceFragment(StartSetupWizardFragment.getInstance());
        } else if (CAMERA_PERMISSION.equals(step)) {
            replaceFragment(CameraPermissionFragment.getInstance());
        } else if (PUPIL_LIST.equals(step)) {
            replaceFragment(PupilListFragment.getInstance());
        } else if (BIND_PUPIL.equals(step)) {
            replaceFragment(BindPupilFragment.getInstance());
        } else if (PUPIL_INFORMATION.equals(step)) {
            replaceFragment(PupilInformationFragment.getInstance(params));
        }
    }
}