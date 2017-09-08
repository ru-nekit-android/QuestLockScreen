package ru.nekit.android.qls.setupWizard.view;

import android.Manifest;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.setupWizard.BaseSetupWizardPermissionRequestFragment;

public class CallPhoneAndReadContactsPermissionFragment
        extends BaseSetupWizardPermissionRequestFragment {

    public static CallPhoneAndReadContactsPermissionFragment getInstance() {
        return new CallPhoneAndReadContactsPermissionFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_call_phone_and_read_contacts_permisssion;
    }

    @Override
    protected int getPermissionRequestCode() {
        return 1;
    }

    @Override
    protected String[] getPermissionList() {
        return new String[]{Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CONTACTS};
    }
}