package ru.nekit.android.qls.setupWizard.view

import android.Manifest
import android.view.View

import ru.nekit.android.qls.R
import ru.nekit.android.qls.setupWizard.BaseSetupWizardPermissionRequestFragment
import ru.nekit.android.utils.ParameterlessSingletonHolder

class CallPhoneAndReadContactsPermissionFragment : BaseSetupWizardPermissionRequestFragment() {

    override val permissionRequestCode: Int
        get() = 1

    override fun onSetupStart(view: View) {
        super.onSetupStart(view)
        title = R.string.title_call_phone_permission_request
    }

    override val permissionList: Array<String>
        get() = arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS)

    override fun getLayoutId() = R.layout.sw_call_phone_and_read_contacts_permisssion

    companion object : ParameterlessSingletonHolder<CallPhoneAndReadContactsPermissionFragment>(::CallPhoneAndReadContactsPermissionFragment)

}