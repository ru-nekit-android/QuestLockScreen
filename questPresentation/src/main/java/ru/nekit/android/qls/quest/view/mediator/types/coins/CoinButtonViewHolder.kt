package ru.nekit.android.qls.quest.view.mediator.types.coins

import android.content.Context
import android.view.ViewGroup
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.resources.CoinVisualResourceCollection
import ru.nekit.android.utils.ViewHolder

//ver 1.0
internal class CoinButtonViewHolder(context: Context,
                                    coinVisualResourceCollection: CoinVisualResourceCollection) :
        ViewHolder(context, R.layout.layout_button_coin) {

    val coinViewHolder: CoinViewHolder = CoinViewHolder(context, coinVisualResourceCollection)
    val container: ViewGroup = view.findViewById(R.id.container_icon_button) as ViewGroup

    init {
        container.addView(coinViewHolder.view)
    }

}
