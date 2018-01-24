package ru.nekit.android.qls.quest.view.mediator.types.coins;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.resources.collections.CoinVisualResourceCollection;
import ru.nekit.android.qls.quest.view.mediator.answer.ButtonsQuestAnswerMediator;

public class CoinQuestAnswerMediator extends ButtonsQuestAnswerMediator {

    private List<CoinButtonHolder> mCoinButtonHostList;

    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        super.onCreate(questContext);
        mCoinButtonHostList = new ArrayList<>();
        switch (mQuest.getQuestionType()) {

            case UNKNOWN_MEMBER:

                Object[] availableVariants = mQuest.getAvailableAnswerVariants();
                if (availableVariants != null) {
                    for (Object item : availableVariants) {
                        CoinVisualResourceCollection coinVisualResourceCollection = CoinVisualResourceCollection.getById((int) item);
                        if (coinVisualResourceCollection != null) {
                            CoinButtonHolder coinButtonHost =
                                    CoinViewBuilder.createButton(mQuestContext, coinVisualResourceCollection, this);
                            mCoinButtonHostList.add(coinButtonHost);
                            getAnswerButtonList().add(coinButtonHost.view);
                        }
                    }
                }

                break;
        }
    }

    @Override
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        super.onQuestAttach(rootContentContainer);
        updateSizeInternal();
    }

    @Override
    public void onQuestStart(boolean delayedPlay) {
        super.onQuestStart(delayedPlay);
        updateSizeInternal();
    }

    @Override
    public void onQuestPlay(boolean delayedPlay) {
        super.onQuestPlay(delayedPlay);
    }

    @Override
    public void detachView() {
        mCoinButtonHostList.clear();
        mCoinButtonHostList = null;
        super.detachView();
    }

    @Override
    public void updateSize() {
        updateSizeInternal();
    }

    private void updateSizeInternal() {
        int width = mRootContentContainer.getWidth();
        switch (mQuest.getQuestionType()) {

            case UNKNOWN_MEMBER:

                final int coinCount = mCoinButtonHostList.size();
                int coinsWidth = 0, i = 0;
                for (; i < coinCount; i++) {
                    CoinButtonHolder coinButtonHost = mCoinButtonHostList.get(i);
                    LayoutParams coinLayoutParams = coinButtonHost.coinViewHolder.view.getLayoutParams();
                    int coinSize = coinButtonHost.coinViewHolder.getAdaptiveWidth(width);
                    coinLayoutParams.width = coinLayoutParams.height = coinSize;
                    coinsWidth += coinSize;
                }
                int spaceBetweenCoins = (width - coinsWidth) / coinCount / 2;
                for (i = 0; i < coinCount; i++) {
                    CoinButtonHolder coinButtonHost = mCoinButtonHostList.get(i);
                    LayoutParams coinButtonLayoutParams = coinButtonHost.view.getLayoutParams();
                    coinButtonLayoutParams.width = coinButtonHost.coinViewHolder.getAdaptiveWidth(width)
                            + spaceBetweenCoins * 2;
                    coinButtonLayoutParams.height = coinButtonHost.coinViewHolder.getAdaptiveHeight(width);
                    coinButtonHost.view.requestLayout();
                }

                mRootContentContainer.requestLayout();
                break;
        }
    }
}