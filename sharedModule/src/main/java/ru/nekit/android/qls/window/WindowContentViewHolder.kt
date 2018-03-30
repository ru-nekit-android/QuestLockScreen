package ru.nekit.android.qls.window

import android.content.Context
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.View
import ru.nekit.android.shared.R
import ru.nekit.android.utils.ViewHolder

open class WindowContentViewHolder(context: Context, @LayoutRes layoutId: Int) : ViewHolder(context, layoutId) {

    internal val closeButton: View
        get() = view.findViewById(closeButtonId)

    @get:IdRes
    protected open val closeButtonId: Int
        get() = R.id.btn_close
}