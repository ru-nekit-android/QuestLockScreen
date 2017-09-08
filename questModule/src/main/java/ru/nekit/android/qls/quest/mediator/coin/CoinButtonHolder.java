package ru.nekit.android.qls.quest.mediator.coin;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.types.CoinModel;
import ru.nekit.android.qls.utils.ViewHolder;

class CoinButtonHolder extends ViewHolder {

    final CoinViewHolder coinViewHolder;
    final ViewGroup container;

    CoinButtonHolder(@NonNull Context context, @NonNull CoinModel coinModel) {
        super(context, R.layout.layout_button_coin);
        coinViewHolder = new CoinViewHolder(context, coinModel);
        container = (ViewGroup) getView().findViewById(R.id.container_icon_button);
        container.addView(coinViewHolder.getView());
    }

    View getCoinView() {
        return coinViewHolder.getView();
    }

}
