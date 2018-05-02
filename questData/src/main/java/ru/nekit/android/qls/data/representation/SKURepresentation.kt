package ru.nekit.android.qls.data.representation

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.SkuType.INAPP
import com.android.billingclient.api.BillingClient.SkuType.SUBS
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import ru.nekit.android.qls.domain.model.SKU
import ru.nekit.android.qls.domain.model.SKUDetails
import ru.nekit.android.qls.domain.model.SKUPurchase
import ru.nekit.android.qls.domain.model.SKUType
import ru.nekit.android.qls.domain.model.SKUType.PRESENT
import ru.nekit.android.qls.domain.model.SKUType.SUBSCRIPTION
import ru.nekit.android.questData.R.string.*

private object SKURepresentationProvider : StringIdRepresentationProvider<SKU>() {

    init {
        createRepresentation(SKU.ANNUAL_SUBSCRIPTION, sku_annual_subscription_title)
        createRepresentation(SKU.WEEKLY_SUBSCRIPTION, sku_weekly_subscription_title)
        createRepresentation(SKU.MONTH_SUBSCRIPTION, sku_month_subscription_title)
    }

}

@BillingClient.SkuType
fun SKUType.getSkuType() = when (this) {
    SUBSCRIPTION -> SUBS
    PRESENT -> INAPP
}

fun SKU.getSkuId(context: Context) = SKURepresentationProvider.getRepresentation(this).getString(context)

object SKURepresentation {

    fun getSKUType(@BillingClient.SkuType skuType: String): SKUType {
        return if (skuType == SUBS) SUBSCRIPTION else PRESENT
    }

    private fun getRepresentationList(context: Context, skuType: String) =
            SKU.values().filter { it.skuType.getSkuType() == skuType && it.enabled }.map { it.getSkuId(context) }

    private fun getSubscriptionList(context: Context) = getRepresentationList(context,
            SUBS)

    fun getSubscriptionSkuIdList(context: Context) = getSubscriptionList(context)

    fun getInAppList(context: Context) = getRepresentationList(context, INAPP)

    fun getFromSkuId(context: Context, skuId: String) = SKU.values().first { it.getSkuId(context) == skuId }
}

fun SkuDetails.toSKUDetails(context: Context): SKUDetails {
    with(this) {
        return SKUDetails(SKURepresentation.getFromSkuId(context, sku),
                price,
                priceAmountMicros,
                priceCurrencyCode,
                title,
                description,
                subscriptionPeriod,
                freeTrialPeriod,
                introductoryPrice,
                introductoryPriceAmountMicros,
                introductoryPricePeriod)
    }
}

fun Purchase.toSKUPurchase(context: Context): SKUPurchase {
    with(this) {
        return SKUPurchase(SKURepresentation.getFromSkuId(context, sku),
                orderId,
                purchaseTime,
                purchaseToken,
                isAutoRenewing,
                signature)
    }
}