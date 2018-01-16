package ru.nekit.android.qls.quest.mediator.types.coins;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.mediator.content.AbstractQuestContentMediator;
import ru.nekit.android.qls.quest.model.CoinModel;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class CoinQuestContentMediator extends AbstractQuestContentMediator {

    private RelativeLayout mContentContainer;
    private List<CoinViewHolder> mCoinViewHolderList;

    private static int getXPositionShiftByWidth(int width, int coinCount) {
        float sizeMultiplier = coinCount > 4 ? 0.6f : 0.7f;
        return (int) (width * sizeMultiplier);
    }

    private NumberSummandQuest getQuest() {
        return (NumberSummandQuest) mQuest;
    }

    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        super.onCreate(questContext);
        NumberSummandQuest quest = getQuest();
        mContentContainer = new RelativeLayout(questContext);
        mCoinViewHolderList = new ArrayList<>();
        //sort for beauty
        Arrays.sort(quest.leftNode);
        QuestionType questionType = quest.getQuestionType();
        switch (questionType) {

            case SOLUTION:
            case UNKNOWN_MEMBER:

                if (quest.leftNode != null) {
                    for (int i = quest.leftNode.length - 1; i >= 0; i--) {
                        if (questionType == QuestionType.UNKNOWN_MEMBER && i == quest.unknownMemberIndex) {
                            continue;
                        }
                        CoinModel coinModel = CoinModel.getById(quest.leftNode[i]);
                        if (coinModel != null) {
                            CoinViewHolder coinViewHolder = CoinViewBuilder.createView(questContext,
                                    coinModel);
                            mContentContainer.addView(coinViewHolder.view);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                            coinViewHolder.view.setLayoutParams(layoutParams);
                            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                            coinViewHolder.view.requestLayout();
                            mCoinViewHolderList.add(coinViewHolder);
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
        //updateSizeInternal();
    }

    private void updateSizeInternal() {
        int width = mRootContentContainer.getWidth();
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
            View coinView = coinViewHolder.view;
            LayoutParams coinLayoutParams = coinView.getLayoutParams();
            coinSize = (int) (coinViewHolder.getAdaptiveWidth(width) * scale);
            coinLayoutParams.width = coinLayoutParams.height = coinSize;
            coinView.setX(coinXPosition);
            coinXPosition += getXPositionShiftByWidth(coinSize, coinCount);
            coinView.requestLayout();
        }
    }

    @Override
    public void onQuestPause() {
        super.onQuestPause();
        updateSize();
    }
}