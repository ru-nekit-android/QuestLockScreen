package ru.nekit.android.qls.setupWizard;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.shared.R;

public abstract class BaseSetupWizardPermissionRequestFragment extends BaseSetupWizardFragment {

    protected abstract int getPermissionRequestCode();

    protected abstract String[] getPermissionList();

    @Override
    protected void onSetupStart(@NonNull View view) {
        setAltButtonText(R.string.label_ask_for_permission);
        update(false);
    }

    protected void update(boolean granted) {
        setNextButtonVisibility(granted);
        setAltButtonVisibility(!granted);
    }

    private void requestPermission() {
        String[] checkPermissions = getPermissionList();
        List<String> callPermissions = new ArrayList<>();
        for (String permission : checkPermissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                callPermissions.add(permission);
            }
        }
        if (callPermissions.size() > 0) {
            requestPermissions(callPermissions.toArray(new String[callPermissions.size()]),
                    getPermissionRequestCode());
        }
    }

    @Override
    protected void altButtonAction() {
        requestPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == getPermissionRequestCode()) {
            boolean permissionWasGranted = true;
            for (int value : grantResults) {
                permissionWasGranted = permissionWasGranted && value ==
                        PackageManager.PERMISSION_GRANTED;
            }
            update(permissionWasGranted);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}