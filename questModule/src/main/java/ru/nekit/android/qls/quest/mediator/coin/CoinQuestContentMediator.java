package ru.nekit.android.qls.quest.mediator.coin;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.mediator.shared.content.AbstractQuestContentMediator;
import ru.nekit.android.qls.quest.types.CoinModel;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

public class CoinQuestContentMediator extends AbstractQuestContentMediator {

    private FrameLayout mContentContainer;
    private List<CoinViewHolder> mCoinViewHolderList;

    private static int getXPositionShiftByWidth(int width, int coinCount) {
        float sizeMultiplier = coinCount > 4 ? 0.6f : 0.7f;
        return (int) (width * sizeMultiplier);
    }

    @Override
    public void onCreateQuest(@NonNull QuestContext questContext, @NonNull ViewGroup rootContentContainer) {
        super.onCreateQuest(questContext, rootContentContainer);
        NumberSummandQuest numberSummandQuest = (NumberSummandQuest) mQuest;
        mContentContainer = new FrameLayout(questContext);
        mCoinViewHolderList = new ArrayList<>();
        CoinViewHolder coinViewHolder;
        //sort for beauty
        Arrays.sort(numberSummandQuest.leftNode);
        QuestionType questionType = mQuest.getQuestionType();
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
                            mContentContainer.addView(coinViewHolder.getView());
                            mCoinViewHolderList.add(coinViewHolder);
                        }
                    }
                }

                break;

        }
    }

    @Override
    public void detachView() {
        mContentContainer.removeAllViews();
    }

    @Override
    public void deactivate() {
        mCoinViewHolderList.clear();
        mCoinViewHolderList = null;
        super.deactivate();
    }

    @Override
    public View getView() {
        return mContentContainer;
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
    public void updateSize() {
        int width = mRootContentContainer.getWidth();
        int height = mRootContentContainer.getHeight();
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
        LayoutParams contentLayoutParams = mContentContainer.getLayoutParams();
        contentLayoutParams.width = coinContainerWidth;
        contentLayoutParams.height = maxCoinHeight;
        mContentContainer.setY((height - maxCoinHeight) / 2);
        mContentContainer.requestLayout();
    }

    @Override
    public void onPauseQuest() {
        super.onPauseQuest();
        updateSize();
    }
}