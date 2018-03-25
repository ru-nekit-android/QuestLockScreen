package ru.nekit.android.qls.quest.view.mediator.types.coins

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.resources.CoinVisualResourceCollection

//ver 1.0
internal object CoinViewBuilder {

    fun createView(context: Context,
                   coinVisualResourceItem: CoinVisualResourceCollection): CoinViewHolder =
            CoinViewHolder(context, coinVisualResourceItem).let {
                with(it) {
                    val coinShadowView = AppCompatImageView(context)
                    coinShadowView.setImageDrawable(ContextCompat.getDrawable(context,
                            R.drawable.shadow_coin))
                    setCoinShadowView(coinShadowView)
                    val resources = context.resources
                    val shadowWidth = resources.getDimensionPixelSize(R.dimen.coin_shadow_width)
                    val shadowHeight = resources.getDimensionPixelSize(R.dimen.coin_shadow_height)
                    coinView.x = shadowWidth.toFloat()
                    coinShadowView.y = shadowHeight.toFloat()
                    coinView.setPadding(0, 0, shadowWidth, 0)
                    coinShadowView.setPadding(0, 0, 0, shadowHeight)
                }
                it
            }

    fun createButton(context: Context, data: CoinVisualResourceCollection): CoinButtonViewHolder =
            CoinButtonViewHolder(context, data).apply {
                container.tag = data
            }
}
