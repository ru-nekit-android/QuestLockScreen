package ru.nekit.android.qls.quest.mediator.coins;

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
import ru.nekit.android.qls.quest.types.FruitArithmeticQuest;
import ru.nekit.android.qls.quest.types.model.CoinModel;

import static ru.nekit.android.qls.quest.QuestionType.UNKNOWN_MEMBER;

public class CoinQuestContentMediator extends AbstractQuestContentMediator {

    private FrameLayout mContentContainer;
    private List<CoinViewHolder> mCoinViewHolderList;

    private static int getXPositionShiftByWidth(int width, int coinCount) {
        float sizeMultiplier = coinCount > 4 ? 0.6f : 0.7f;
        return (int) (width * sizeMultiplier);
    }

    private FruitArithmeticQuest getQuest() {
        return (FruitArithmeticQuest) mQuest;
    }

    @Override
    public void onCreateQuest(@NonNull QuestContext questContext, @NonNull ViewGroup rootContentContainer) {
        super.onCreateQuest(questContext, rootContentContainer);
        FruitArithmeticQuest quest = getQuest();
        mContentContainer = new FrameLayout(questContext);
        mCoinViewHolderList = new ArrayList<>();
        //sort for beauty
        Arrays.sort(quest.leftNode);
        QuestionType questionType = mQuest.getQuestionType();
        switch (questionType) {

            case SOLUTION:
            case UNKNOWN_MEMBER:

                if (quest.leftNode != null) {
                    for (int i = quest.leftNode.length - 1; i >= 0; i--) {
                        if (questionType == UNKNOWN_MEMBER && i == quest.unknownMemberIndex) {
                            continue;
                        }
                        CoinModel coinModel = CoinModel.getByNomination(quest.leftNode[i]);
                        if (coinModel != null) {
                            CoinViewHolder coinViewHolder = CoinViewBuilder.createView(questContext,
                                    coinModel);
                            mContentContainer.addView(coinViewHolder.getView());
                            mCoinViewHolderList.add(coinViewHolder);
                        }
                    }
                }

                break;

        }
        updateSize();
    }

    @Override
    public void onStartQuest(boolean playAnimationOnDelayedStart) {
        super.onStartQuest(playAnimationOnDelayedStart);
        updateSize();
    }

    @Override
    public void onRestartQuest() {
        super.onRestartQuest();
        updateSize();
    }

    @Override
    public void deactivate() {
        mCoinViewHolderList.clear();
        mCoinViewHolderList = null;
        super.deactivate();
    }

    @Override
    public void detachView() {
        mContentContainer.removeAllViews();
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
        coinXPosition = 0;
        for (i = 0; i < coinCount; i++) {
            CoinViewHolder coinViewHolder = mCoinViewHolderList.get(i);
            View coinView = coinViewHolder.getView();
            LayoutParams coinLayoutParams = coinView.getLayoutParams();
            coinSize = (int) (coinViewHolder.getAdaptiveWidth(width) * scale);
            coinLayoutParams.width = coinLayoutParams.height = coinSize;
            coinView.setY((height - coinSize) / 2);
            coinView.setX(coinXPosition);
            coinXPosition += getXPositionShiftByWidth(coinSize, coinCount);
        }
    }

    @Override
    public void onPauseQuest() {
        super.onPauseQuest();
        updateSize();
    }
}