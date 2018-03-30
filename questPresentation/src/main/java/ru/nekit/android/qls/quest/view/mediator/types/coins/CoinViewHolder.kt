package ru.nekit.android.qls.quest.view.mediator.types.coins

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ru.nekit.android.qls.R
import ru.nekit.android.qls.data.representation.getDrawableId
import ru.nekit.android.qls.domain.model.resources.CoinVisualResourceCollection
import ru.nekit.android.utils.ViewHolder

//ver 1.0
internal class CoinViewHolder(context: Context,
                              private val coinVisualResourceItem: CoinVisualResourceCollection) :
        ViewHolder(context, R.layout.ql_coin) {

    var coinView: ImageView = view.findViewById(R.id.view_coin) as ImageView
    private var shadowView: View? = null

    init {
        coinView.setImageResource(coinVisualResourceItem.getDrawableId())
        if (PADDING == -1) {
            val ta = context.obtainStyledAttributes(R.style.Quest_Coin_Button_Background,
                    R.styleable.PaddingStyle)
            PADDING = ta.getDimensionPixelOffset(R.styleable.PaddingStyle_android_padding, 0)
            ta.recycle()
        }
    }

    fun getAdaptiveWidth(width: Int): Int {
        return (width / CoinVisualResourceCollection.values().size - PADDING * 2) * coinVisualResourceItem.relativeSizeValue / CoinVisualResourceCollection.maxRelativeSizeValue
    }

    fun getAdaptiveHeight(width: Int): Int {
        return width / CoinVisualResourceCollection.values().size
    }

    fun setCoinShadowView(coinShadowView: View) {
        if (shadowView == null) {
            shadowView = coinShadowView
            (view as ViewGroup).addView(shadowView, 0)
        }
    }

    companion object {
        private var PADDING = -1
    }
}