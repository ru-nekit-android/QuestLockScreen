package ru.nekit.android.qls.parentControl.billing;

import com.android.billingclient.api.BillingClient.SkuType;

import java.util.Arrays;
import java.util.List;

public final class BillingConstants {
    public static final String SKU_CAR = "ru.nekit.android.qls.car";
    public static final String SKU_TEST = "android.test.purchased";


    private static final String[] IN_APP_SKUS = {/*SKU_CAR*/SKU_TEST};
    private static final String[] SUBSCRIPTIONS_SKUS = {};

    private BillingConstants() {
    }

    public static List<String> getSkuList(@SkuType String billingType) {
        return (SkuType.INAPP.equals(billingType)) ? Arrays.asList(IN_APP_SKUS)
                : Arrays.asList(SUBSCRIPTIONS_SKUS);
    }
}

