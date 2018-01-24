package ru.nekit.android.qls.quest.view.mediator.types.coins;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.resources.collections.CoinVisualResourceCollection;
import ru.nekit.android.qls.utils.ViewHolder;

class CoinViewHolder extends ViewHolder {

    private static int PADDING = -1;
    ImageView coinView;
    private CoinVisualResourceCollection coinVisualResourceCollection;
    private View coinShadowView;

    CoinViewHolder(@NonNull Context context, @NonNull CoinVisualResourceCollection coinVisualResourceCollection) {
        super(context, R.layout.ql_coin);
        this.coinVisualResourceCollection = coinVisualResourceCollection;
        coinView = (ImageView) view.findViewById(R.id.view_coin);
        coinView.setImageResource(coinVisualResourceCollection.mDrawableResourceId);
        if (PADDING == -1) {
            TypedArray ta = mContext.obtainStyledAttributes(R.style.Quest_Coin_Button_Background,
                    R.styleable.PaddingStyle);
            PADDING = ta.getDimensionPixelOffset(R.styleable.PaddingStyle_android_padding, 0);
            ta.recycle();
        }
    }

    int getAdaptiveWidth(int width) {
        return (width / CoinVisualResourceCollection.values().length - PADDING * 2)
                * coinVisualResourceCollection.relativeSizeValue / CoinVisualResourceCollection.getMaxRelativeSizeValue();
    }

    int getAdaptiveHeight(int width) {
        return width / CoinVisualResourceCollection.values().length;
    }

    void setCoinShadowView(View coinShadowView) {
        if (this.coinShadowView == null) {
            this.coinShadowView = coinShadowView;
            ((ViewGroup) view).addView(coinShadowView, 0);
        }
    }

    public View getShadowView() {
        return coinShadowView;
    }
}
