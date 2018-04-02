package ru.nekit.android.utils

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

open class ViewHolder(protected val context: Context, @LayoutRes layoutId: Int, root: ViewGroup? = null) {

    val view: View = LayoutInflater.from(context).inflate(layoutId, root)

}