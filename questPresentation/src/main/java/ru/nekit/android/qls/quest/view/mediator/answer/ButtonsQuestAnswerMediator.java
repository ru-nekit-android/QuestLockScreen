package ru.nekit.android.qls.quest.view.mediator.answer;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.answer.common.IQuestAnswerVariantAdapter;
import ru.nekit.android.qls.utils.MathUtils;

public class ButtonsQuestAnswerMediator extends AbstractQuestAnswerMediator
        implements View.OnClickListener, IButtonsQuestAnswerMediator {

    private List<View> mButtonList;
    @Nullable
    private IQuestAnswerVariantAdapter mButtonListAdapter;

    public ButtonsQuestAnswerMediator() {
    }

    public ButtonsQuestAnswerMediator(@Nullable IQuestAnswerVariantAdapter buttonListAdapter) {
        mButtonListAdapter = buttonListAdapter;
    }

    @CallSuper
    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        super.onCreate(questContext);
        mButtonList = new ArrayList<>();
    }

    private View createButtonInternal(String label, Object tag, boolean isFirst, boolean isLast) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        View button = createButton(label, tag, params, isFirst, isLast);
        button.setLayoutParams(params);
        if (button instanceof TextView) {
            ((TextView) button).setText(label);
        }
        button.setTag(tag);
        button.setOnClickListener(this);
        return button;
    }

    @Override
    public void deactivate() {
        if (mButtonList != null) {
            for (View button : mButtonList) {
                button.setOnClickListener(null);
            }
        }
        mButtonList.clear();
        super.deactivate();
    }

    @NonNull
    protected View createButton(String label, Object tag, @NonNull LinearLayout.LayoutParams layoutParams, boolean isFirst, boolean isLast) {
        return mQuestContext.createButton();
    }

    protected void fillButtonListWithAvailableVariants() {
        fillButtonListWithAvailableVariants(false);
    }

    @SuppressWarnings("unchecked")
    protected void fillButtonListWithAvailableVariants(boolean shuffle) {
        Object[] availableVariants = mQuest.getAvailableAnswerVariants();
        if (shuffle) {
            availableVariants = MathUtils.shuffleArray(availableVariants);
        }

        if (availableVariants != null) {
            int length = availableVariants.length;
            for (int i = 0; i < length; i++) {
                Object variant = availableVariants[i];
                String label = mButtonListAdapter == null ? variant.toString() :
                        mButtonListAdapter.adapt(mQuestContext, variant);
                if (label != null) {
                    mButtonList.add(createButtonInternal(label, variant,
                            i == 0,
                            i == length - 1));
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
    @SuppressWarnings("unchecked")
    public void onClick(@NonNull View view) {
        if (mAnswerChecker.checkAlternativeInput(mQuest, view.getTag())) {
            mAnswerCallback.rightAnswer();
        } else {
            mAnswerCallback.wrongAnswer();
        }
    }
}