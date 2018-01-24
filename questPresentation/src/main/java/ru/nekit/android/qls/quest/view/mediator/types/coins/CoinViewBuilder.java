package ru.nekit.android.qls.quest.view.mediator.types.coins;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.resources.collections.CoinVisualResourceCollection;

class CoinViewBuilder {

    static CoinViewHolder createView(@NonNull Context context, @NonNull CoinVisualResourceCollection coinVisualResourceCollection) {
        Resources resources = context.getResources();
        CoinViewHolder coinViewHolder = new CoinViewHolder(context, coinVisualResourceCollection);
        ImageView coinShadowView = new AppCompatImageView(context);
        coinShadowView.setImageDrawable(ContextCompat.getDrawable(context,
                R.drawable.shadow_coin));
        coinViewHolder.setCoinShadowView(coinShadowView);
        int shadowWidth = resources.getDimensionPixelSize(R.dimen.coin_shadow_width);
        int shadowHeight = resources.getDimensionPixelSize(R.dimen.coin_shadow_height);
        coinViewHolder.coinView.setX(shadowWidth);
        coinShadowView.setY(shadowHeight);
        coinViewHolder.coinView.setPadding(0, 0, shadowWidth, 0);
        coinShadowView.setPadding(0, 0, 0, shadowHeight);
        return coinViewHolder;
    }

    static CoinButtonHolder createButton(@NonNull Context context, @NonNull CoinVisualResourceCollection coinModelVisualResourceCollection,
                                         @NonNull View.OnClickListener listener) {
        CoinButtonHolder coinButtonHost = new CoinButtonHolder(context, coinModelVisualResourceCollection);
        coinButtonHost.container.setOnClickListener(listener);
        coinButtonHost.container.setTag(coinModelVisualResourceCollection);
        return coinButtonHost;
    }

}