package ru.nekit.android.utils

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View

open class ViewHolder(protected val context: Context, @LayoutRes layoutId: Int) {

    val view: View = LayoutInflater.from(context).inflate(layoutId, null)

}