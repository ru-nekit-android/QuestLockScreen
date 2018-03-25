package ru.nekit.android.qls.setupWizard.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.PhoneContact

//ver 1.0
class PhoneContactsAdapterForModification(private val data: List<PhoneContact>,
                                          private val phoneContactListener: PhoneContactListener) : RecyclerView.Adapter<PhoneContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneContactViewHolder {
        return PhoneContactViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.ill_phone_contact_for_modification, parent, false))
    }

    override fun onBindViewHolder(holder: PhoneContactViewHolder, position: Int) {
        val (_, name, phoneNumber) = data[position]
        holder.titleView.text = name
        holder.informationView.text = phoneNumber
        holder.actionButton.setOnClickListener { phoneContactListener.onAction(holder.adapterPosition) }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}