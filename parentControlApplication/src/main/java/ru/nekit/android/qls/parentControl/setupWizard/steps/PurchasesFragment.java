package ru.nekit.android.qls.parentControl.setupWizard.steps;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Single;
import ru.nekit.android.qls.parentControl.R;
import ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard;
import ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizardFragment;

public class PurchasesFragment extends ParentControlSetupWizardFragment {


    public static PurchasesFragment getInstance() {
        return new PurchasesFragment();
    }

    @LayoutRes
    @Override
    protected int getLayoutId() {
        return R.layout.sw_magazine;
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        setNextButtonText(R.string.label_acquire_present);

    }

    @NotNull
    @Override
    protected Single<Boolean> nextAction() {
        showSetupWizardStep(ParentControlSetupWizard.WizardStep.ACQUIRE_PRESENT);
        return false;
    }

    /*public void onPurchaseButtonClicked() {
        if (mAcquireFragment == null) {
            mAcquireFragment = new AcquirePresentFragment();
        }
        if (!isAcquireFragmentShown()) {
            mAcquireFragment.show(getActivity().getSupportFragmentManager(), "DIALOG_TAG");
            if (mBillingManager != null && mBillingManager.getBillingClientResponseCode()
                    > BillingManager.BILLING_MANAGER_NOT_INITIALIZED) {
                mAcquireFragment.onManagerReady(this);
            }
        }
    }*/

    @Override
    public void onDestroy() {
        // if (mBillingManager != null) {
        //     mBillingManager.destroy();
        // }
        super.onDestroy();
    }

    void onBillingManagerSetupFinished() {
        /*if (mAcquireFragment != null) {
            mAcquireFragment.onManagerReady(this);
        }*/
    }


}