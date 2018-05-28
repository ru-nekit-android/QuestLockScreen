package ru.nekit.android.qls.setupWizard.view

import android.app.Activity
import android.app.admin.DevicePolicyManager.*
import android.content.Intent
import android.support.annotation.LayoutRes
import android.view.View
import ru.nekit.android.qls.R
import ru.nekit.android.qls.R.string.*
import ru.nekit.android.utils.ParameterlessSingletonHolder

class DeviceAdminFragment : QuestSetupWizardFragment() {

    @LayoutRes
    override fun getLayoutId() = R.layout.sw_device_admin

    override fun onSetupStart(view: View) {
        title = title_device_admin_request
        altButtonText(label_device_admin)
        update(false)
    }

    private fun update(isSet: Boolean) {
        altButtonVisibility(!isSet)
        nextButtonVisibility(isSet)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_FOR_ADMIN_REQUEST && resultCode == Activity.RESULT_OK)
            goNext()
    }

    override fun altAction() {
        autoDispose {
            setupWizard.deviceAdminIsSet { isSet ->
                if (!isSet)
                    startActivityForResult(Intent(ACTION_ADD_DEVICE_ADMIN).apply {
                        putExtra(EXTRA_DEVICE_ADMIN, setupWizard.deviceAdminComponent)
                        putExtra(EXTRA_ADD_EXPLANATION, getString(text_device_admin_request_explanation))
                    }, REQUEST_CODE_FOR_ADMIN_REQUEST)
            }
        }
    }

    companion object : ParameterlessSingletonHolder<DeviceAdminFragment>(::DeviceAdminFragment) {

        private const val REQUEST_CODE_FOR_ADMIN_REQUEST = 1

    }

}