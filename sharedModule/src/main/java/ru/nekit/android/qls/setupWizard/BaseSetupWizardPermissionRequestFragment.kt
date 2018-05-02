package ru.nekit.android.qls.setupWizard

import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.view.View
import ru.nekit.android.shared.R
import java.util.*

abstract class BaseSetupWizardPermissionRequestFragment : BaseSetupWizardFragment() {

    protected abstract val permissionRequestCode: Int

    protected abstract val permissionList: Array<String>

    override fun onSetupStart(view: View) {
        setAltButtonText(R.string.label_ask_for_permission)
        update(false)
    }

    private fun update(granted: Boolean) {
        setNextButtonVisibility(granted)
        setAltButtonVisibility(!granted)
    }

    protected fun requestPermission() {
        val checkPermissions = permissionList
        val callPermissions: ArrayList<String> = checkPermissions.filterTo(ArrayList()) {
            ContextCompat.checkSelfPermission(context!!, it) != PackageManager.PERMISSION_GRANTED
        }
        if (callPermissions.size > 0) {
            requestPermissions(Array(callPermissions.size, { callPermissions[it] }),
                    permissionRequestCode)
        }
    }

    override fun altAction() = requestPermission()

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == permissionRequestCode) {
            var permissionWasGranted = true
            for (value in grantResults) {
                permissionWasGranted = permissionWasGranted && value == PackageManager.PERMISSION_GRANTED
            }
            onPermissionResult(permissionWasGranted)
            update(permissionWasGranted)
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    protected open fun onPermissionResult(grantResults: Boolean) {

    }
}