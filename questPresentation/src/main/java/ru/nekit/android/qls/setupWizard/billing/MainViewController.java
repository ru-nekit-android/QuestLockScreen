/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.nekit.android.qls.setupWizard.billing;

import android.util.Log;

import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.Purchase;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ru.nekit.android.qls.setupWizard.view.QuestSetupWizardActivity;

/**
 * Handles control logic of the BaseGamePlayActivity
 */
public class MainViewController {
    private static final String TAG = "ru.nekit.android";

    private final UpdateListener mUpdateListener;
    private QuestSetupWizardActivity mActivity;

    private boolean mAnnualSubscription;

    public MainViewController(QuestSetupWizardActivity activity) {
        mUpdateListener = new UpdateListener();
        mActivity = activity;
    }

    public UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    public boolean isAnnualSubscription() {
        return mAnnualSubscription;
    }

    /**
     * Handler to billing updates
     */
    private class UpdateListener implements Billing.BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {
            // mActivity.onBillingManagerSetupFinished();
            Log.d(TAG, "onBillingClientSetupFinished.");
        }

        @Override
        public void onConsumeFinished(String token, @BillingResponse int result) {
            Log.d(TAG, "Consumption finished. Purchase token: " + token + ", result: " + result);

            // Note: We know this is the SKU_GAS, because it's the only one we consume, so we don't
            // check if token corresponding to the expected sku was consumed.
            // If you have more than one sku, you probably need to validate that the token matches
            // the SKU you expect.
            // It could be done by maintaining a map (updating it every time you call consumeAsync)
            // of all tokens into SKUs which were scheduled to be consumed and then looking through
            // it here to check which SKU corresponds to a consumed token.
            if (result == BillingResponse.OK) {
                // Successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit

            } else {

            }

            //mActivity.showRefreshedUi();
            Log.d(TAG, "End consumption flow.");
        }

        @Override
        public void onPurchasesUpdated(@NotNull List<? extends Purchase> purchases) {
            mAnnualSubscription = false;

            for (Purchase purchase : purchases) {
                switch (purchase.getSku()) {
                    case "ru.nekit.android.qls.annual_subscription":
                        mAnnualSubscription = true;
                        break;
                }
            }
        }
    }
}