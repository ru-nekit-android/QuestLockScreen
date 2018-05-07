package ru.nekit.android.qls.domain.model

import ru.nekit.android.qls.domain.model.SKUType.SUBSCRIPTION

enum class SKU(val enabled: Boolean, val skuType: SKUType) {

    ANNUAL_SUBSCRIPTION(false, SUBSCRIPTION),
    MONTH_SUBSCRIPTION(true, SUBSCRIPTION),
    WEEKLY_SUBSCRIPTION(true, SUBSCRIPTION)

}

enum class SKUType {

    SUBSCRIPTION,
    PRESENT

}

interface ISKUHolder {
    val sku: SKU
}

data class SKUDetails(override val sku: SKU,
                      val price: String,
                      val priceAmountMicros: Long,
                      val priceCurrencyCode: String,
                      val title: String,
                      val description: String,
                      val subscriptionPeriod: String,
                      val freeTrialPeriod: String,
                      val introductoryPrice: String,
                      val introductoryPriceAmountMicros: String,
                      val introductoryPricePeriod: String
) : ISKUHolder

data class SKUPurchase(override val sku: SKU,
                       val orderId: String,
                       val time: Long,
                       val token: String,
                       val autoRenewing: Boolean,
                       val signature: String
) : ISKUHolder

enum class PeriodTime {

    P1W,
    P1M,
    P3M,
    P6M,
    P1Y;
}