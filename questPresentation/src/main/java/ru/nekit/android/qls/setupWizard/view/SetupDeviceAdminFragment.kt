package ru.nekit.android.qls.setupWizard.view

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.support.annotation.LayoutRes
import android.view.View
import ru.nekit.android.qls.R
import ru.nekit.android.qls.setupWizard.QuestSetupWizard

class SetupDeviceAdminFragment : QuestSetupWizardFragment() {

    override fun onSetupStart(view: View) {
        setAltButtonText(R.string.label_device_admin)
        setNextButtonText(R.string.label_next)
        setNextButtonVisibility(false)
        update(false)
    }

    private fun update(adminIsSet: Boolean) {
        setAltButtonVisibility(!adminIsSet)
        setNextButtonVisibility(adminIsSet)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_FOR_ADMIN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                update(true)
            }
        }
    }

    override fun altAction() {
        autoDispose {
            QuestSetupWizard.QuestSetupWizardStep.DEVICE_ADMIN.stepIsComplete(setupWizard).subscribe { it ->
                if (!it) {
                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                            setupWizard.deviceAdminComponent)
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            context!!.getString(R.string.text_device_admin_request_explanation))
                    startActivityForResult(intent, REQUEST_CODE_FOR_ADMIN_REQUEST)
                }
            }
        }
    }

    @LayoutRes
    override fun getLayoutId(): Int {
        return R.layout.sw_setup_admin
    }

    companion object {

        private val REQUEST_CODE_FOR_ADMIN_REQUEST = 1

        val instance: SetupDeviceAdminFragment
            get() = SetupDeviceAdminFragment()
    }

}