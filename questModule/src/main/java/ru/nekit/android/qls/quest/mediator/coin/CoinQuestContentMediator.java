package ru.nekit.android.qls.quest.mediator.coin;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.mediator.AbstractQuestContentMediator;
import ru.nekit.android.qls.quest.types.CoinModel;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

public class CoinQuestContentMediator extends AbstractQuestContentMediator {

    private FrameLayout mContent;
    private List<CoinViewHolder> mCoinViewHolderList;

    private static int getXPositionShiftByWidth(int width, int coinCount) {
        float sizeMultiplier = coinCount > 4 ? 0.5f : 0.7f;
        return (int) (width * sizeMultiplier);
    }

    @Override
    public void init(@NonNull QuestContext questContext) {
        NumberSummandQuest numberSummandQuest = (NumberSummandQuest) questContext.getQuest();
        mContent = new FrameLayout(questContext);
        mCoinViewHolderList = new ArrayList<>();
        CoinViewHolder coinViewHolder;
        //sort for beauty
        Arrays.sort(numberSummandQuest.leftNode);
        QuestionType questionType = numberSummandQuest.getQuestionType();
        switch (questionType) {

            case SOLUTION:
            case UNKNOWN_MEMBER:

                if (numberSummandQuest.leftNode != null) {
                    for (int i = numberSummandQuest.leftNode.length - 1; i >= 0; i--) {
                        if (questionType == QuestionType.UNKNOWN_MEMBER &&
                                i == numberSummandQuest.unknownMemberIndex) {
                            continue;
                        }
                        CoinModel coinModel =
                                CoinModel.getByNomination(numberSummandQuest.leftNode[i]);
                        if (coinModel != null) {
                            coinViewHolder = CoinViewBuilder.createView(questContext, coinModel);
                            mContent.addView(coinViewHolder.getView());
                            mCoinViewHolderList.add(coinViewHolder);
                        }
                    }
                }

                break;

        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mCoinViewHolderList.clear();
        mCoinViewHolderList = null;
    }

    @Override
    public View getView() {
        return mContent;
    }

    @Override
    public EditText getAnswerInput() {
        return null;
    }

    @Override
    public boolean includeInLayout() {
        return true;
    }

    @Override
    public void updateSize(int width, int height) {
        final int coinCount = mCoinViewHolderList.size();
        int coinContainerWidth = 0, coinXPosition = 0, coinSize = 0, maxCoinHeight = 0, i = 0;
        for (; i < coinCount; i++) {
            CoinViewHolder coinViewHolder = mCoinViewHolderList.get(i);
            coinSize = coinViewHolder.getAdaptiveWidth(width);
            maxCoinHeight = Math.max(maxCoinHeight, coinSize);
            coinXPosition += getXPositionShiftByWidth(coinSize, coinCount);
            if (i < coinCount - 1) {
                coinContainerWidth = coinXPosition;
            }
        }
        coinContainerWidth += coinSize;
        //scale
        float scale = 1 / (coinCount > 2 ? 1 : 1.3f) * width / coinContainerWidth;
        coinContainerWidth *= scale;
        maxCoinHeight *= scale;
        coinXPosition = 0;
        for (i = 0; i < coinCount; i++) {
            CoinViewHolder coinViewHolder = mCoinViewHolderList.get(i);
            View coinView = coinViewHolder.getView();
            LayoutParams coinLayoutParams = coinView.getLayoutParams();
            coinSize = (int) (coinViewHolder.getAdaptiveWidth(width) * scale);
            coinLayoutParams.width = coinLayoutParams.height = coinSize;
            coinView.setY((maxCoinHeight - coinSize) / 2);
            coinView.setX(coinXPosition);
            coinXPosition += getXPositionShiftByWidth(coinSize, coinCount);
            coinView.requestLayout();
        }
        LayoutParams contentLayoutParams = mContent.getLayoutParams();
        contentLayoutParams.width = coinContainerWidth;
        contentLayoutParams.height = maxCoinHeight;
        mContent.requestLayout();
    }
}
