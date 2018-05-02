package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.ColorRes
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.SKU
import ru.nekit.android.qls.domain.useCases.AccessUseCases
import ru.nekit.android.qls.domain.useCases.PeriodTime
import ru.nekit.android.qls.domain.useCases.PremiumAccessUseCases
import ru.nekit.android.qls.setupWizard.QuestSetupWizard
import ru.nekit.android.qls.view.adapters.SubscriptionsAdapter
import ru.nekit.android.utils.IAutoDispose

class SubscriptionsFragment : QuestSetupWizardFragment() {

    private lateinit var subscriptionsListView: RecyclerView
    private lateinit var subscriptionAdapter: SubscriptionsAdapter
    private lateinit var subscriptionTitleView: TextView
    private lateinit var subscriptionExpiredTimeView: TextView
    private lateinit var titleContainer: ViewGroup

    private lateinit var viewModel: AccessInfoViewModel

    override fun onSetupStart(view: View) {
        subscriptionsListView = view.findViewById(R.id.list_subscriptions)
        subscriptionTitleView = view.findViewById(R.id.tv_subscription_info_description)
        subscriptionExpiredTimeView = view.findViewById(R.id.tv_subscription_expired_time)
        titleContainer = view.findViewById(R.id.container_title)
        subscriptionsListView.layoutManager = LinearLayoutManager(context)
        viewModel = AccessInfoViewModel(setupWizard)
        subscriptionAdapter = SubscriptionsAdapter(viewModel.actionListener)
        subscriptionsListView.adapter = subscriptionAdapter
        viewModel.listenAvailableSubscriptions { setAvailableSubscriptions(it) }
        viewModel.listenAccessInfo { info ->
            subscriptionTitleView.text = info.title
            subscriptionExpiredTimeView.text = info.expiredTimeText
            titleContainer.setBackgroundColor(ContextCompat.getColor(setupWizard.application,
                    info.backgroundColor))
        }
        setAltButtonText(R.string.label_back)
        setNextButtonVisibility(false)
    }

    private fun setAvailableSubscriptions(list: List<AccessInfoViewModel.SubscriptionViewItem>) {
        subscriptionAdapter.data = list
        subscriptionsListView.visibility = if (list.isEmpty()) GONE else VISIBLE
        subscriptionAdapter.notifyDataSetChanged()
    }

    override val addToBackStack: Boolean = true

    override fun onDestroy() {
        viewModel.dispose()
        super.onDestroy()
    }

    @LayoutRes
    override fun getLayoutId(): Int = R.layout.sw_access_info

    companion object {

        val instance: SubscriptionsFragment
            get() = SubscriptionsFragment()
    }
}

class AccessInfoViewModel(val setupWizard: QuestSetupWizard) : IAutoDispose {

    override var disposableMap: MutableMap<String, CompositeDisposable> = HashMap()

    val actionListener = PublishSubject.create<SKU>().toSerialized()

    init {
        autoDispose {
            actionListener.subscribe {
                setupWizard.initiateSKUPurchaseFlow(it)
            }
        }
    }

    fun listenAvailableSubscriptions(body: (List<SubscriptionViewItem>) -> Unit) = autoDispose {
        PremiumAccessUseCases.listenAvailableSubscriptions()
                .map { data ->
                    data.first.map {
                        it.let {
                            SubscriptionViewItem(
                                    it.sku,
                                    it.title,
                                    it.description,
                                    it.price,
                                    if (data.second) "Сменить подписку" else "Оформить подписку")

                        }
                    }
                }
                .subscribe(body)
    }

    fun listenAccessInfo(body: (AccessInfoViewItem) -> Unit) = autoDispose {
        AccessUseCases.listenAccessInfo().map { info ->
            when {
                info.premium != null -> info.premium?.let {
                    val expiredTime = DateFormat.format("d MMMM yyyy",
                            it.skuPurchase.time + setupWizard.timeProvider.getPeriodTime(
                                    PeriodTime.valueOf(it.skuDetails.subscriptionPeriod)))
                    AccessInfoViewItem(it.skuDetails.description, if (it.skuPurchase.autoRenewing)
                        "Следующий платеж: $expiredTime"
                    else
                        "Подписка будет отменена $expiredTime",
                            if (it.skuPurchase.autoRenewing) R.color.gold else R.color.silver)
                }
                info.trial != null -> info.trial?.let { trial ->
                    val expiredTime = DateFormat.format("d MMMM yyyy",
                            (trial.startTimestamp
                                    ?: setupWizard.timeProvider.getCurrentTime()) +
                                    setupWizard.timeProvider.getPeriodTime(trial.periodTime))
                    AccessInfoViewItem("Пробный период",
                            "Доступен до $expiredTime",
                            R.color.green_light)
                }
                else -> AccessInfoViewItem("Пробный период закончился", "", R.color.red)
            }
        }.subscribe { body(it!!) }
    }

    data class SubscriptionViewItem(val sku: SKU,
                                    val title: String,
                                    val description: String,
                                    val price: String,
                                    val actionButtonLabel: String)

    data class AccessInfoViewItem(val title: String,
                                  val expiredTimeText: String,
                                  @ColorRes val backgroundColor: Int)

}