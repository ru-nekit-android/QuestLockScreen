package ru.nekit.android.qls.quest.mediator.types.coins;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.model.CoinModel;
import ru.nekit.android.qls.utils.ViewHolder;

class CoinViewHolder extends ViewHolder {

    private static int PADDING = -1;
    ImageView coinView;
    private CoinModel coinModel;
    private View coinShadowView;

    CoinViewHolder(@NonNull Context context, @NonNull CoinModel coinModel) {
        super(context, R.layout.ql_coin);
        this.coinModel = coinModel;
        coinView = (ImageView) getView().findViewById(R.id.view_coin);
        coinView.setImageResource(coinModel.background);
        if (PADDING == -1) {
            TypedArray ta = mContext.obtainStyledAttributes(R.style.Quest_Coin_Button_Background,
                    R.styleable.PaddingStyle);
            PADDING = ta.getDimensionPixelOffset(R.styleable.PaddingStyle_android_padding, 0);
            ta.recycle();
        }
    }

    int getAdaptiveWidth(int width) {
        return (width / CoinModel.values().length - PADDING * 2)
                * coinModel.relativeSizeValue / CoinModel.getMaxRelativeSizeValue();
    }

    int getAdaptiveHeight(int width) {
        return width / CoinModel.values().length;
    }

    public void setCoinShadowView(View coinShadowView) {
        if (this.coinShadowView == null) {
            this.coinShadowView = coinShadowView;
            ((ViewGroup) getView()).addView(coinShadowView, 0);
        }
    }

    public View getShadowView() {
        return coinShadowView;
    }
}
