package ru.nekit.android.qls.data.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.nekit.android.qls.domain.model.SKU

@Entity
data class SKUDetailsEntity(@Id var id: Long = 0,
                            @Convert(converter = SKUConverter::class, dbType = String::class)
                            val sku: SKU,
                            val price: String,
                            val priceAmountMicros: Long,
                            val priceCurrencyCode: String,
                            val title: String,
                            val description: String,
                            val subscriptionPeriod: String,
                            val freeTrialPeriod: String,
                            val introductoryPrice: String,
                            val introductoryPriceAmountMicros: String,
                            val introductoryPricePeriod: String)


@Entity
data class SKUPurchaseEntity(@Id var id: Long = 0,
                             @Convert(converter = SKUConverter::class, dbType = String::class)
                             val sku: SKU,
                             val orderId: String,
                             val time: Long,
                             val token: String,
                             val autoRenewing: Boolean,
                             val signature: String)