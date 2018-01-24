package ru.nekit.android.qls.quest.view.mediator.types.coins;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.resources.collections.CoinVisualResourceCollection;
import ru.nekit.android.qls.utils.ViewHolder;

class CoinButtonHolder extends ViewHolder {

    final CoinViewHolder coinViewHolder;
    final ViewGroup container;

    CoinButtonHolder(@NonNull Context context, @NonNull CoinVisualResourceCollection coinVisualResourceCollection) {
        super(context, R.layout.layout_button_coin);
        coinViewHolder = new CoinViewHolder(context, coinVisualResourceCollection);
        container = (ViewGroup) view.findViewById(R.id.container_icon_button);
        container.addView(coinViewHolder.view);
    }

    View getCoinView() {
        return coinViewHolder.view;
    }

}
