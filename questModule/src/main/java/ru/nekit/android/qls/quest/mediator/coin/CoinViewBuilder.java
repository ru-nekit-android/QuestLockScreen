package ru.nekit.android.qls.quest.mediator.coin;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.types.CoinModel;

class CoinViewBuilder {

    static CoinViewHolder createView(@NonNull Context context, @NonNull CoinModel coinModel) {
        Resources resources = context.getResources();
        CoinViewHolder coinViewHolder = new CoinViewHolder(context, coinModel);
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

    static CoinButtonHolder createButton(@NonNull Context context, @NonNull CoinModel coinModelModel,
                                         @NonNull View.OnClickListener listener) {
        CoinButtonHolder coinButtonHost = new CoinButtonHolder(context, coinModelModel);
        coinButtonHost.container.setOnClickListener(listener);
        coinButtonHost.container.setTag(coinModelModel);
        return coinButtonHost;
    }

}