package ru.nekit.android.qls.parentControl.setupWizard.view;

import android.Manifest;

import ru.nekit.android.qls.parentControl.R;
import ru.nekit.android.qls.setupWizard.BaseSetupWizardPermissionRequestFragment;

public class CameraPermissionFragment extends BaseSetupWizardPermissionRequestFragment {

    public static CameraPermissionFragment getInstance() {
        return new CameraPermissionFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_camera_permisssion;
    }

    @Override
    protected int getPermissionRequestCode() {
        return 1;
    }

    @Override
    protected String[] getPermissionList() {
        return new String[]{Manifest.permission.CAMERA};
    }
}