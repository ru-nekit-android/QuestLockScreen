package ru.nekit.android.qls.setupWizard.view;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.R;

public class SetupDeviceAdminFragment extends QuestSetupWizardFragment {

    private static final int REQUEST_CODE_FOR_ADMIN_REQUEST = 1;

    public static SetupDeviceAdminFragment getInstance() {
        return new SetupDeviceAdminFragment();
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        setAltButtonText(R.string.label_device_admin);
        setNextButtonText(R.string.label_next);
        setNextButtonVisibility(false);
        update(false);
    }

    private void update(boolean adminIsSet) {
        setAltButtonVisibility(!adminIsSet);
        setNextButtonVisibility(adminIsSet);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FOR_ADMIN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                update(true);
            }
        }
    }

    @Override
    protected void altAction() {
        boolean adminIsSet = getSetupWizard().deviceAdminIsActive();
        if (!adminIsSet) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, getSetupWizard().getDeviceAdminComponent());
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getContext().getString(R.string.text_device_admin_request_explanation));
            startActivityForResult(intent, REQUEST_CODE_FOR_ADMIN_REQUEST);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_setup_admin;
    }

}