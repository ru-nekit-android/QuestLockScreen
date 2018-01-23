package ru.nekit.android.qls.quest.mediator.types.fruitArithmetic;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatImageView;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.mediator.content.AbstractQuestContentMediator;
import ru.nekit.android.qls.quest.resources.QuestResourceLibrary;
import ru.nekit.android.qls.quest.types.FruitArithmeticQuest;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static ru.nekit.android.qls.quest.QuestionType.SOLUTION;
import static ru.nekit.android.qls.quest.resources.collections.SimpleVisualResourceCollection.EQUAL;
import static ru.nekit.android.qls.quest.resources.collections.SimpleVisualResourceCollection.MINUS;
import static ru.nekit.android.qls.quest.resources.collections.SimpleVisualResourceCollection.PLUS;

public class FruitArithmeticQuestContentMediator extends AbstractQuestContentMediator {

    private LinearLayout mContentContainer;
    private FruitArithmeticQuest mFruitArithmeticQuest;
    private SparseArray<Drawable> mFruitImageDrawableCache;
    private SparseArray<Drawable> mFruitShadowImageDrawableCache;
    private QuestResourceLibrary mQuestResourceLibrary;

    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        super.onCreate(questContext);
        mQuestResourceLibrary = questContext.getQuestResourceLibrary();
        mFruitImageDrawableCache = new SparseArray<>();
        mFruitShadowImageDrawableCache = new SparseArray<>();
        mFruitArithmeticQuest = (FruitArithmeticQuest) mQuest;
        if (mFruitArithmeticQuest.getQuestionType() == SOLUTION) {
            mContentContainer = new LinearLayout(questContext);
            final int length = mFruitArithmeticQuest.getVisualRepresentationList().size();
            for (int i = 0; i < length; i++) {
                mContentContainer.addView(createFruitView(questContext,
                        mFruitArithmeticQuest.getVisualRepresentationList().get(i)));
            }
            LayoutParams contentLayoutParams = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
            mContentContainer.setOrientation(LinearLayout.HORIZONTAL);
            mContentContainer.setGravity(Gravity.CENTER_VERTICAL);
            mContentContainer.setLayoutParams(contentLayoutParams);
        }
    }

    @Override
    public void deactivate() {
        mFruitImageDrawableCache.clear();
        mFruitShadowImageDrawableCache.clear();
        super.deactivate();
    }

    @Override
    public void detachView() {
        mContentContainer.removeAllViews();
    }

    private Drawable getFruitImageDrawable(@NonNull QuestContext questContext,
                                           int visualRepresentationId) {
        return ContextCompat.getDrawable(questContext,
                mQuestResourceLibrary.getVisualQuestResource(visualRepresentationId)
                        .getDrawableResourceId());
    }

    private View createFruitView(@NonNull QuestContext questContext,
                                 int visualRepresentationId) {
        AppCompatImageView fruitImageView = new AppCompatImageView(questContext);
        AppCompatImageView fruitShadowImageView = new AppCompatImageView(questContext);
        fruitImageView.setTag(visualRepresentationId);
        Drawable fruitImageDrawable = mFruitImageDrawableCache.get(visualRepresentationId);
        if (fruitImageDrawable == null) {
            fruitImageDrawable = getFruitImageDrawable(questContext, visualRepresentationId);
            mFruitImageDrawableCache.append(visualRepresentationId, fruitImageDrawable);
        }
        fruitImageView.setImageDrawable(fruitImageDrawable);
        Drawable fruitShadowImageDrawable = mFruitShadowImageDrawableCache.get(visualRepresentationId);
        if (fruitShadowImageDrawable == null) {
            ColorStateList csl = AppCompatResources.getColorStateList(questContext, R.color.semi_black);
            fruitShadowImageDrawable = DrawableCompat.wrap(getFruitImageDrawable(questContext,
                    visualRepresentationId).mutate());
            DrawableCompat.setTintList(fruitShadowImageDrawable, csl);
            mFruitShadowImageDrawableCache.append(visualRepresentationId, fruitShadowImageDrawable);
        }
        FrameLayout fruitContainer = new FrameLayout(questContext);
        fruitShadowImageView.setImageDrawable(fruitShadowImageDrawable);
        fruitShadowImageView.setY(questContext.getResources().getDimensionPixelSize(R.dimen.fruit_shadow_height));
        fruitContainer.addView(fruitShadowImageView);
        fruitImageView.setX(questContext.getResources().getDimensionPixelSize(R.dimen.fruit_shadow_width));
        fruitContainer.addView(fruitImageView);
        return fruitContainer;
    }

    @Override
    public View getView() {
        return mContentContainer;
    }

    @Override
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        super.onQuestAttach(rootContentContainer);
        updateSizeInternal();
    }

    @Override
    public void onQuestStart(boolean delayedPlay) {
        super.onQuestStart(delayedPlay);
        if (!delayedPlay) {
            updateSizeInternal();
        }
    }

    @Override
    public void onQuestPlay(boolean delayedPlay) {
        if (delayedPlay) {
            updateSizeInternal();
        }
        super.onQuestPlay(delayedPlay);
    }

    @Override
    public void updateSize() {
        //none
    }

    private void updateSizeInternal() {
        if (mFruitArithmeticQuest.getQuestionType() == SOLUTION) {
            final int length = mFruitArithmeticQuest.getVisualRepresentationList().size();
            int index = 0;
            for (int i = 0; i < length; i++) {
                View visualRepresentationItemView = mContentContainer.getChildAt(i);
                int baseSize = getView().getWidth() / length;
                int visualRepresentationId = mFruitArithmeticQuest.getVisualRepresentationList().get(i);
                int visualRepresentationIdNext = visualRepresentationId;
                if (i < length - 1) {
                    visualRepresentationIdNext =
                            mFruitArithmeticQuest.getVisualRepresentationList().get(i + 1);
                }
                LayoutParams visualRepresentationItemLayoutParams =
                        new LayoutParams(0, MATCH_PARENT, 1);
                float marginRight = -baseSize / 2,
                        marginLeft = 0,
                        marginTop;
                index++;
                if (visualRepresentationIdNext != visualRepresentationId) {
                    marginRight = 0;
                    index = 0;
                }
                marginTop = Math.min(2, index) * baseSize / 4;
                if (visualRepresentationId == mQuestResourceLibrary.getQuestVisualResourceId(MINUS)
                        || visualRepresentationId == mQuestResourceLibrary.getQuestVisualResourceId(PLUS)
                        || visualRepresentationId == mQuestResourceLibrary.getQuestVisualResourceId(EQUAL)) {

                    marginRight = 0;
                    marginLeft = 0;
                    marginTop = 0;

                }
                if (index > 0) {
                    visualRepresentationItemLayoutParams.setMargins(
                            (int) marginLeft,
                            (int) marginTop,
                            (int) marginRight,
                            0
                    );
                }
                visualRepresentationItemView.setLayoutParams(visualRepresentationItemLayoutParams);
                visualRepresentationItemView.requestLayout();
            }
        }
    }

    @Override
    public EditText getAnswerInput() {
        return null;
    }

    @Override
    public boolean includeInLayout() {
        return true;
    }
}