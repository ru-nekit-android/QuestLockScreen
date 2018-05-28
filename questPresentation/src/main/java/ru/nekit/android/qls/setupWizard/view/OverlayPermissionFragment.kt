package ru.nekit.android.qls.setupWizard.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.annotation.LayoutRes
import android.support.annotation.RequiresApi
import android.view.View
import ru.nekit.android.qls.R
import ru.nekit.android.qls.setupWizard.QuestSetupWizard
import ru.nekit.android.utils.ParameterlessSingletonHolder

class OverlayPermissionFragment : QuestSetupWizardFragment() {

    @LayoutRes
    override fun getLayoutId(): Int = R.layout.sw_overlay_permission

    override fun onSetupStart(view: View) {
        title = R.string.title_overlay_permission_request
        altButtonText(R.string.label_overlay_permission_request)
        updateView(false)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun altAction() {
        startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context!!.packageName)), REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            autoDispose {
                QuestSetupWizard.QuestSetupWizardStep.OVERLAY_PERMISSION.stepIsComplete(setupWizard).subscribe { isComplete ->
                    if (isComplete)
                        goNext()
                }
            }
        }
    }

    private fun updateView(overlayIsEnabled: Boolean) {
        nextButtonVisibility(overlayIsEnabled)
        altButtonVisibility(!overlayIsEnabled)
    }

    companion object : ParameterlessSingletonHolder<OverlayPermissionFragment>(::OverlayPermissionFragment) {
        private const val REQUEST_CODE = 1
    }
}