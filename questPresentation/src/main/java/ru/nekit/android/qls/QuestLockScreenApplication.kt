package ru.nekit.android.qls

import android.content.Context
import android.support.multidex.MultiDex
import ru.nekit.android.qls.data.entity.QuestHistoryEntity
import ru.nekit.android.qls.data.entity.QuestStatisticsReportEntity
import ru.nekit.android.qls.data.entity.SKUDetailsEntity
import ru.nekit.android.qls.data.entity.SKUPurchaseEntity
import ru.nekit.android.qls.dependences.DependenciesProvider

class QuestLockScreenApplication : DependenciesProvider() {

    override fun onCreate() {
        super.onCreate()
        createAndInjectDependencies()
        ///
        val clear = false
        if (clear) {
            boxStore.boxFor(QuestStatisticsReportEntity::class.java).removeAll()
            boxStore.boxFor(QuestHistoryEntity::class.java).removeAll()
            boxStore.boxFor(SKUDetailsEntity::class.java).removeAll()
            boxStore.boxFor(SKUPurchaseEntity::class.java).removeAll()
            repositoryHolder.getRewardRepository().clear()
        }
        getRemoteSettings().fetchConfig()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}