// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package ru.nekit.android.qls.parentControl.skulist.row;

import android.widget.Toast;

import com.android.billingclient.api.BillingClient.SkuType;

import ru.nekit.android.qls.parentControl.billing.BillingProvider;

/**
 * Implementations of this abstract class are responsible to render UI and handle user actions for
 * skulist rows to render RecyclerView with AcquirePresentFragment's specific UI
 */
public abstract class UiManagingDelegate {

    protected final BillingProvider mBillingProvider;

    public UiManagingDelegate(BillingProvider billingProvider) {
        mBillingProvider = billingProvider;
    }

    public abstract @SkuType
    String getType();

    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        holder.description.setText(data.getDescription());
        holder.price.setText(data.getPrice());
        holder.button.setEnabled(true);
    }

    public void onButtonClicked(SkuRowData data) {
        // mBillingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
        //         data.getSkuType());
    }

    protected void showAlreadyPurchasedToast() {
        Toast.makeText(mBillingProvider.getBillingManager().getContext(),
                "Уже куплен", Toast.LENGTH_SHORT).show();
    }
}
