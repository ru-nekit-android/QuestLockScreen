package ru.nekit.android.qls.quest.mediator;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.answer.IAlternativeAnswerVariantAdapter;
import ru.nekit.android.qls.quest.answer.IAnswerCallback;
import ru.nekit.android.qls.quest.answer.IAnswerChecker;

public class QuestAlternativeAnswerMediator implements View.OnClickListener,
        IQuestAlternativeAnswerMediator {

    protected QuestContext mQuestContext;
    protected List<View> mButtonList;
    protected IQuest mQuest;
    private IAnswerChecker mAnswerChecker;
    private IAnswerCallback mAnswerCallback;
    @Nullable
    private IAlternativeAnswerVariantAdapter mAdapter;
    private boolean mIsDestroyed;

    public QuestAlternativeAnswerMediator() {
    }

    public QuestAlternativeAnswerMediator(@Nullable IAlternativeAnswerVariantAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void init(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mQuest = questContext.getQuest();
        mButtonList = new ArrayList<>();
    }

    @Override
    final public void setAnswerCallback(@NonNull IAnswerCallback answerCallback) {
        mAnswerCallback = answerCallback;
    }

    @Override
    final public void setAnswerChecker(@NonNull IAnswerChecker answerChecker) {
        mAnswerChecker = answerChecker;
    }

    private View createSimpleButton(String label, Object tag) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        View button = createButton(tag, params);
        button.setLayoutParams(params);
        if (button instanceof TextView) {
            ((TextView) button).setText(label);
        }
        button.setTag(tag);
        button.setOnClickListener(this);
        return button;
    }

    protected View createButton(Object tag, @NonNull LinearLayout.LayoutParams layoutParams) {
        return mQuestContext.createButton();
    }

    protected void fillButtonListWithAvailableVariants() {
        Object[] availableVariants = mQuest.getAvailableAnswerVariants();
        if (availableVariants != null) {
            for (Object variant : availableVariants) {
                String label = mAdapter == null ? variant.toString() :
                        mAdapter.adapt(mQuestContext, variant);
                if (label != null) {
                    mButtonList.add(createSimpleButton(label, variant));
                }
            }
        }
    }

    @Nullable
    @Override
    public List<View> getAnswerButtonList() {
        return mButtonList;
    }

    @Override
    public void onClick(@NonNull View view) {
        if (mAnswerChecker.checkAlternativeInput(mQuest, view.getTag())) {
            mAnswerCallback.rightAnswer();
        } else {
            mAnswerCallback.wrongAnswer();
        }
    }

    @Override
    public void updateSize(int width, int height) {
    }

    @CallSuper
    @Override
    public void destroy() {
        mIsDestroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return mIsDestroyed;
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public void playAnimationOnDelayedStart(int duration, @Nullable View view) {
        if (getView() != null) {
            view = getView();
        }
        view.setScaleX(0.1f);
        view.setScaleY(0.1f);
        view.animate().scaleX(1).scaleY(1).setInterpolator(new BounceInterpolator()).setDuration(duration);
    }
}