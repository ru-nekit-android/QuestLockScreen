package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.setupWizard.view.viewModel.AccessInfoViewModel
import ru.nekit.android.qls.setupWizard.view.viewModel.SubscriptionViewItem
import ru.nekit.android.qls.view.adapters.SubscriptionsAdapter
import ru.nekit.android.utils.ParameterlessSingletonHolder

class SubscriptionsFragment : QuestSetupWizardFragment() {

    private lateinit var subscriptionsListView: RecyclerView
    private lateinit var subscriptionAdapter: SubscriptionsAdapter
    private lateinit var subscriptionTitleView: TextView
    private lateinit var subscriptionExpiredTimeView: TextView
    private lateinit var titleContainer: ViewGroup

    private lateinit var viewModel: AccessInfoViewModel

    override fun onSetupStart(view: View) {
        viewModel = AccessInfoViewModel(setupWizard, activity!!)
        title = R.string.title_subscriptions
        subscriptionsListView = view.findViewById(R.id.list_subscriptions)
        subscriptionTitleView = view.findViewById(R.id.tv_subscription_info_description)
        subscriptionExpiredTimeView = view.findViewById(R.id.tv_subscription_expired_time)
        titleContainer = view.findViewById(R.id.container_title)
        subscriptionsListView.layoutManager = LinearLayoutManager(context)
        subscriptionAdapter = SubscriptionsAdapter(viewModel.actionListener)
        subscriptionsListView.adapter = subscriptionAdapter
        viewModel.listenAvailableSubscriptions { setAvailableSubscriptions(it) }
        viewModel.listenAccessInfo { info ->
            subscriptionTitleView.text = info.title
            subscriptionExpiredTimeView.text = info.expiredTimeText
            titleContainer.setBackgroundColor(ContextCompat.getColor(context!!,
                    info.backgroundColor))
        }
        nextButtonText(R.string.label_ok)
    }

    private fun setAvailableSubscriptions(list: List<SubscriptionViewItem>) {
        subscriptionAdapter.data = list
        subscriptionsListView.visibility = if (list.isEmpty()) GONE else VISIBLE
        subscriptionAdapter.notifyDataSetChanged()
    }

    override val addToBackStack: Boolean = true

    override fun onDestroy() {
        viewModel.dispose()
        super.onDestroy()
    }

    override fun nextAction(): Single<Boolean> = backAction()

    @LayoutRes
    override fun getLayoutId(): Int = R.layout.sw_subscriptions

    companion object : ParameterlessSingletonHolder<SubscriptionsFragment>(::SubscriptionsFragment)
}