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

class OverlayPermissionFragment : QuestSetupWizardFragment() {

    override fun onSetupStart(view: View) {
        setAltButtonText(R.string.label_ask_for_overlay_permission)
        updateView(false)
    }

    @LayoutRes
    override fun getLayoutId(): Int = R.layout.sw_overlay_permission

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun altAction() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context!!.packageName))
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            autoDispose {
                QuestSetupWizard.QuestSetupWizardStep.OVERLAY_PERMISSION.stepIsComplete(setupWizard).subscribe { isComplete ->
                    if (isComplete)
                        showNextSetupWizardStep()
                }
            }
        }
    }

    private fun updateView(overlayIsEnabled: Boolean) {
        setNextButtonVisibility(overlayIsEnabled)
        setAltButtonVisibility(!overlayIsEnabled)
    }

    companion object {

        private const val REQUEST_CODE = 1

        val instance: OverlayPermissionFragment
            get() = OverlayPermissionFragment()
    }
}