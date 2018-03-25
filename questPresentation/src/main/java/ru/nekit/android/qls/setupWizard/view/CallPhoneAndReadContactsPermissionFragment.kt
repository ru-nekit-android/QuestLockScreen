package ru.nekit.android.qls.setupWizard.view

import android.Manifest

import ru.nekit.android.qls.R
import ru.nekit.android.qls.setupWizard.BaseSetupWizardPermissionRequestFragment

class CallPhoneAndReadContactsPermissionFragment : BaseSetupWizardPermissionRequestFragment() {

    override val permissionRequestCode: Int
        get() = 1

    override val permissionList: Array<String>
        get() = arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS)

    override fun getLayoutId(): Int {
        return R.layout.sw_call_phone_and_read_contacts_permisssion
    }

    companion object {

        val instance: CallPhoneAndReadContactsPermissionFragment
            get() = CallPhoneAndReadContactsPermissionFragment()
    }
}