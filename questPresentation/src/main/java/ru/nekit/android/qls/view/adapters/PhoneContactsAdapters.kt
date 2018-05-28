package ru.nekit.android.qls.view.adapters

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.Subject
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.PhoneContact
import ru.nekit.android.utils.IAutoDispose
import ru.nekit.android.utils.throttleClicks

//ver 1.1
abstract class PhoneContactsAdapter(private val data: List<PhoneContact>,
                                    @LayoutRes
                                    private val listItemLayoutId: Int,
                                    private val phoneContactListener: Subject<Int>) :
        RecyclerView.Adapter<PhoneContactViewHolder>(), IAutoDispose {

    override var disposableMap: MutableMap<String, CompositeDisposable> = HashMap()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneContactViewHolder {
        return PhoneContactViewHolder(LayoutInflater.from(parent.context)
                .inflate(listItemLayoutId, parent, false))
    }

    override fun onBindViewHolder(holder: PhoneContactViewHolder, position: Int) {
        val (id, name, phoneNumber) = data[position]
        with(holder) {
            titleView.text = name
            informationView.text = phoneNumber
            val isEmergency = id <= PhoneContact.EMERGENCY_PHONE_NUMBER.contactId &&
                    this@PhoneContactsAdapter is PhoneContactsAdapterForModification
            if (!isEmergency) {
                autoDispose {
                    actionButton.throttleClicks {
                        phoneContactListener.onNext(holder.adapterPosition)
                    }
                }
            }
            actionButton.visibility = if (isEmergency) GONE else VISIBLE
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        dispose()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun getItemCount(): Int = data.size

}

class PhoneContactsAdapterForReading(data: List<PhoneContact>,
                                     phoneContactListener: Subject<Int>) : PhoneContactsAdapter(data,
        R.layout.ill_phone_contact_for_reading, phoneContactListener)


class PhoneContactsAdapterForModification(data: List<PhoneContact>,
                                          phoneContactListener: Subject<Int>) : PhoneContactsAdapter(data,
        R.layout.ill_phone_contact_for_modification, phoneContactListener)

class PhoneContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var actionButton: View = view.findViewById(R.id.btn_action)
    var titleView: TextView = view.findViewById(R.id.tv_title)
    var informationView: TextView = view.findViewById(R.id.tv_information)

}