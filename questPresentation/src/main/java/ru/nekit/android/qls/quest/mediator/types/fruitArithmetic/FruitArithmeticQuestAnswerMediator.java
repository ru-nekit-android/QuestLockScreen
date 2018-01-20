package ru.nekit.android.qls.quest.mediator.types.fruitArithmetic;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.mediator.answer.ButtonsQuestAnswerMediator;

public class FruitArithmeticQuestAnswerMediator extends ButtonsQuestAnswerMediator {

    public FruitArithmeticQuestAnswerMediator() {
        super();
    }

    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        super.onCreate(questContext);
        switch (mQuest.getQuestionType()) {

            case SOLUTION:

                fillButtonListWithAvailableVariants(true);

                break;

        }
    }

    @NonNull
    @Override
    protected View createButton(String label, Object answerVariant,
                                @NonNull LinearLayout.LayoutParams layoutParams, boolean isFirst, boolean isLast) {
        TextView button = mQuestContext.setUpFonts(
                (TextView) LayoutInflater.from(mQuestContext).
                        inflate(R.layout.button_fruit_arithmetic, null),
                R.style.Quest_FruitArithmetic_Button);
        button.setBackgroundResource(R.drawable.background_button_green);
        int margin = mQuestContext.getResources().getDimensionPixelSize(R.dimen.base_gap) / 4;
        layoutParams.setMargins(isFirst ? 0 : margin, margin, isLast ? 0 : margin, margin);
        return button;
    }
}