package ru.nekit.android.qls.parentControl.setupWizard.steps;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import ru.nekit.android.qls.parentControl.R;
import ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard;
import ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizardFragment;
import ru.nekit.android.qls.setupWizard.BaseSetupWizardFragment;

public class BindPupilFragment
        extends ParentControlSetupWizardFragment
        implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    public static BaseSetupWizardFragment getInstance() {
        return new BindPupilFragment();
    }

    @Override
    public void handleResult(Result rawResult) {
        if (!getSetupWizard().bindPupilIfAble(rawResult.getText())) {
            Toast.makeText(getContext(), R.string.pupil_already_bound, Toast.LENGTH_LONG).show();
        }
        showSetupWizardStep(ParentControlSetupWizard.WizardStep.PUPIL_LIST);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected boolean addToBackStack() {
        return true;
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        mScannerView = (ZXingScannerView) view.findViewById(R.id.view_zxing);
        setAltButtonVisibility(true);
        setNextButtonVisibility(false);
        setAltButtonText(R.string.cancel);
        mScannerView.setVisibility(View.VISIBLE);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @LayoutRes
    @Override
    protected int getLayoutId() {
        return R.layout.sw_bind_pupil;
    }

    @Override
    protected void altAction() {
        showSetupWizardStep(ParentControlSetupWizard.WizardStep.PUPIL_LIST);
    }

    /*@Override
    public int[] getContainerMargins(@NonNull Resources resources) {
        return new int[]{0, 0, 0, 0};
    }

    @Override
    protected void onAnimationEnd() {
    }*/
}