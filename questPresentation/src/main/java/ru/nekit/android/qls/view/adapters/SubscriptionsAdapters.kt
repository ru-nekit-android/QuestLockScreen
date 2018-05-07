package ru.nekit.android.qls.view.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.Subject
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.SKU
import ru.nekit.android.qls.setupWizard.view.viewModel.SubscriptionViewItem
import ru.nekit.android.utils.IAutoDispose
import ru.nekit.android.utils.throttleClicks

//ver 1.0
class SubscriptionsAdapter(private val actionListener: Subject<SKU>) :
        RecyclerView.Adapter<SubscriptionsViewHolder>(), IAutoDispose {

    override var disposableMap: MutableMap<String, CompositeDisposable> = HashMap()
    var data: List<SubscriptionViewItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionsViewHolder {
        return SubscriptionsViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.ill_subsciptions, parent, false))
    }

    override fun onBindViewHolder(holder: SubscriptionsViewHolder, position: Int) {
        data?.let {
            val skuDetail = it[position]
            with(holder) {
                titleView.text = skuDetail.title
                descriptionView.text = skuDetail.description
                priceView.text = skuDetail.price
                actionButton.text = skuDetail.actionButtonLabel
                autoDispose {
                    actionButton.throttleClicks { actionListener.onNext(skuDetail.sku) }
                }
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        dispose()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun getItemCount(): Int = data?.size ?: 0

}

class SubscriptionsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val actionButton: Button = view.findViewById(R.id.btn_action_button)
    val titleView: TextView = view.findViewById(R.id.tv_title)
    val descriptionView: TextView = view.findViewById(R.id.tv_description)
    val priceView: TextView = view.findViewById(R.id.tv_price)

}