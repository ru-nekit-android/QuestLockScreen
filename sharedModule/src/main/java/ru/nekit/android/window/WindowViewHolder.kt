package ru.nekit.android.window

import android.content.Context
import android.view.ViewGroup
import ru.nekit.android.shared.R
import ru.nekit.android.utils.ViewHolder

internal class WindowViewHolder(context: Context) : ViewHolder(context, R.layout.layout_window) {

    var rootContainer: ViewGroup = view.findViewById(R.id.container_root)
    var contentContainer: ViewGroup = view.findViewById(R.id.container_content)

    fun destroy() {
        rootContainer.removeAllViews()
        contentContainer.removeAllViews()
    }
}
