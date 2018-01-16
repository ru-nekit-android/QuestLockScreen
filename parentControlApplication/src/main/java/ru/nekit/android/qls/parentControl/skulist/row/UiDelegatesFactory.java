package ru.nekit.android.qls.parentControl.skulist.row;

import com.android.billingclient.api.BillingClient.SkuType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.nekit.android.qls.parentControl.billing.BillingProvider;

public class UiDelegatesFactory {
    private final Map<String, UiManagingDelegate> uiDelegates;

    public UiDelegatesFactory(BillingProvider provider) {
        uiDelegates = new HashMap<>();
        //uiDelegates.put(CarDelegate.SKU_ID, new CarDelegate(provider));
        uiDelegates.put(TestDelegate.SKU_ID, new TestDelegate(provider));
    }

    /**
     * Returns the list of all SKUs for the billing type specified
     */
    public final List<String> getSkuList(@SkuType String billingType) {
        List<String> result = new ArrayList<>();
        for (String skuId : uiDelegates.keySet()) {
            UiManagingDelegate delegate = uiDelegates.get(skuId);
            if (delegate.getType().equals(billingType)) {
                result.add(skuId);
            }
        }
        return result;
    }

    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        uiDelegates.get(data.getSku()).onBindViewHolder(data, holder);
    }

    public void onButtonClicked(SkuRowData data) {
        uiDelegates.get(data.getSku()).onButtonClicked(data);
    }
}
