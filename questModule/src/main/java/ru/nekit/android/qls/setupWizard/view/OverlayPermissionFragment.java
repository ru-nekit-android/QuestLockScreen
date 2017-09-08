package ru.nekit.android.qls.setupWizard.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.R;

public class OverlayPermissionFragment extends QuestSetupWizardFragment {

    private static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1;

    public static OverlayPermissionFragment getInstance() {
        return new OverlayPermissionFragment();
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        setAltButtonText(R.string.label_ask_for_overlay_permission);
        updateView(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_overlay_permission;
    }

    // @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void altButtonAction() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getContext().getPackageName()));
        startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
                if (Settings.canDrawOverlays(getContext())) {
                    updateView(true);
                }
            }
        }
    }

    private void updateView(boolean overlayIsEnabled) {
        setNextButtonVisibility(overlayIsEnabled);
        setAltButtonVisibility(!overlayIsEnabled);
    }
}