package ru.nekit.android.qls.parentControl.setupWizard;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;

import java.util.List;

import ru.nekit.android.qls.parentControl.billing.BillingManager;
import ru.nekit.android.qls.parentControl.setupWizard.steps.AcquirePresentFragment;
import ru.nekit.android.qls.parentControl.setupWizard.steps.BindPupilFragment;
import ru.nekit.android.qls.parentControl.setupWizard.steps.CameraPermissionFragment;
import ru.nekit.android.qls.parentControl.setupWizard.steps.PupilInformationFragment;
import ru.nekit.android.qls.parentControl.setupWizard.steps.PupilListFragment;
import ru.nekit.android.qls.parentControl.setupWizard.steps.PurchasesFragment;
import ru.nekit.android.qls.parentControl.setupWizard.steps.StartSetupWizardFragment;
import ru.nekit.android.qls.setupWizard.BaseSetupWizard;
import ru.nekit.android.qls.setupWizard.BaseSetupWizardActivity;
import ru.nekit.android.qls.setupWizard.ISetupWizardStep;

import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.WizardStep.ACQUIRE_PRESENT;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.WizardStep.BIND_PUPIL;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.WizardStep.CAMERA_PERMISSION;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.WizardStep.PUPIL_INFORMATION;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.WizardStep.PUPIL_LIST;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.WizardStep.PURCHASES;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.WizardStep.START;

public class ParentControlSetupWizardActivity extends BaseSetupWizardActivity {

    private BillingManager mBillingManager;
    private UpdateListener mUpdateListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mBillingManager = new BillingManager(this, mUpdateListener);
    }

    @Override
    public ParentControlSetupWizard getSetupWizard() {
        return (ParentControlSetupWizard) super.getSetupWizard();
    }

    @Override
    protected BaseSetupWizard createSetupWizard(@NonNull Context context) {
        return new ParentControlSetupWizard(context);
    }

    @Override
    public void showSetupWizardStep(@NonNull ISetupWizardStep step, Object... params) {
        getSetupWizard().setCurrentSetupStep(step);
        if (START.equals(step)) {
            replaceFragment(StartSetupWizardFragment.getInstance());
        } else if (CAMERA_PERMISSION.equals(step)) {
            replaceFragment(CameraPermissionFragment.getInstance());
        } else if (PUPIL_LIST.equals(step)) {
            replaceFragment(PupilListFragment.getInstance());
        } else if (BIND_PUPIL.equals(step)) {
            replaceFragment(BindPupilFragment.getInstance());
        } else if (PURCHASES.equals(step)) {
            replaceFragment(PurchasesFragment.getInstance());
        } else if (PUPIL_INFORMATION.equals(step)) {
            replaceFragment(PupilInformationFragment.getInstance(params));
        } else if (ACQUIRE_PRESENT.equals(step)) {
            replaceFragment(AcquirePresentFragment.getInstance());
        }
    }

    private class UpdateListener implements BillingManager.BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {

        }

        @Override
        public void onConsumeFinished(String token, @BillingClient.BillingResponse int result) {
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchaseList) {

        }
    }
}