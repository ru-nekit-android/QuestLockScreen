package ru.nekit.android.qls.quest.mediator.coin;

import android.support.annotation.NonNull;
import android.view.ViewGroup.LayoutParams;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.mediator.QuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.types.CoinModel;

/**
 * Created by nekit on 18.01.17.
 */

public class CoinQuestAlternativeAnswerMediator extends QuestAlternativeAnswerMediator {

    private List<CoinButtonHolder> mCoinButtonHostList;

    @Override
    public void init(@NonNull QuestContext questContext) {
        super.init(questContext);
        mCoinButtonHostList = new ArrayList<>();
        switch (mQuest.getQuestionType()) {

            case UNKNOWN_MEMBER:

                Object[] availableVariants = mQuest.getAvailableAnswerVariants();
                if (availableVariants != null) {
                    for (Object variant : availableVariants) {
                        CoinModel coinModel = CoinModel.getByNomination((int) variant);
                        if (coinModel != null) {
                            CoinButtonHolder coinButtonHost =
                                    CoinViewBuilder.createButton(questContext, coinModel, this);
                            mCoinButtonHostList.add(coinButtonHost);
                            mButtonList.add(coinButtonHost.getView());
                        }
                    }
                }

                break;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mCoinButtonHostList.clear();
        mCoinButtonHostList = null;
    }

    @Override
    public void updateSize(int width, /*Not use*/int height) {
        switch (mQuest.getQuestionType()) {

            case UNKNOWN_MEMBER:

                final int coinCount = mCoinButtonHostList.size();
                int coinsWidth = 0, i = 0;
                for (; i < coinCount; i++) {
                    CoinButtonHolder coinButtonHost = mCoinButtonHostList.get(i);
                    LayoutParams coinLayoutParams = coinButtonHost.coinViewHolder.getView().getLayoutParams();
                    int coinSize = coinButtonHost.coinViewHolder.getAdaptiveWidth(width);
                    coinLayoutParams.width = coinLayoutParams.height = coinSize;
                    coinsWidth += coinSize;
                }
                int spaceBetweenCoins = (width - coinsWidth) / coinCount / 2;
                for (i = 0; i < coinCount; i++) {
                    CoinButtonHolder coinButtonHost = mCoinButtonHostList.get(i);
                    LayoutParams coinButtonLayoutParams = coinButtonHost.getView().getLayoutParams();
                    coinButtonLayoutParams.width = coinButtonHost.coinViewHolder.getAdaptiveWidth(width)
                            + spaceBetweenCoins * 2;
                    coinButtonLayoutParams.height = coinButtonHost.coinViewHolder.getAdaptiveHeight(width);
                }


                break;
        }
    }
}
