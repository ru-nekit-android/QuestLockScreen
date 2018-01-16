package ru.nekit.android.qls.parentControl.skulist.row;

import com.android.billingclient.api.BillingClient.SkuType;

import ru.nekit.android.qls.parentControl.R;
import ru.nekit.android.qls.parentControl.billing.BillingProvider;

public class TestDelegate extends UiManagingDelegate {
    public static final String SKU_ID = "android.test.purchased";

    public TestDelegate(BillingProvider billingProvider) {
        super(billingProvider);
    }

    @Override
    public @SkuType
    String getType() {
        return SkuType.INAPP;
    }

    @Override
    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        super.onBindViewHolder(data, holder);
        holder.button.setText("Купить");
        holder.skuIcon.setImageResource(R.drawable.ic_notification);
    }
}

