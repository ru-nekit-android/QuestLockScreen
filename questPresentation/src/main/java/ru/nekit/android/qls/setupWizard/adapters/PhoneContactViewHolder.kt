package ru.nekit.android.qls.setupWizard.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import ru.nekit.android.qls.R

//ver 1.0
class PhoneContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var actionButton: View = view.findViewById(R.id.btn_action)
    var titleView: TextView = view.findViewById<View>(R.id.tv_title) as TextView
    var informationView: TextView = view.findViewById<View>(R.id.tv_information) as TextView

}
