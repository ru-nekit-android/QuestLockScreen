package ru.nekit.android.qls.quest.view.mediator.types.textCamouflage

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import ru.nekit.android.qls.R

//ver 1.0
internal class TextCamouflageAdapter(private val data: List<String>) :
        RecyclerView.Adapter<TextCamouflageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): TextCamouflageAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.ill_text_camouflage, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTextView.text = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal var mTextView: TextView

        init {
            mTextView = view.findViewById<View>(R.id.tv_label) as TextView
        }
    }
}
