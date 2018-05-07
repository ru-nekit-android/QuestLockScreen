package ru.nekit.android.qls.domain.model

enum class AccessType {

    TRIAL,
    PREMIUM,
    EXPIRED

}


data class AccessInfo(val trial: TrialAccessInfo?, val premium: PremiumAccessInfo?)

data class TrialAccessInfo(val startTimestamp: Long?,
                           val stopTimestamp: Long?,
                           val periodTime: PeriodTime)

data class PremiumAccessInfo(val skuDetails: SKUDetails,
                             val skuPurchase: SKUPurchase,
                             val startTimestamp: Long,
                             val stopTimestamp: Long,
                             val periodTime: PeriodTime
)
