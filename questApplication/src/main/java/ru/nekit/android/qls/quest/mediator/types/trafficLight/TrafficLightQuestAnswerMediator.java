package ru.nekit.android.qls.quest.mediator.types.trafficLight;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.answer.TrafficLightQuestAnswerVariantAdapter;
import ru.nekit.android.qls.quest.mediator.answer.ButtonsQuestAnswerMediator;
import ru.nekit.android.qls.quest.model.TrafficLightModel;

public class TrafficLightQuestAnswerMediator extends ButtonsQuestAnswerMediator {

    public TrafficLightQuestAnswerMediator() {
        super(new TrafficLightQuestAnswerVariantAdapter());
    }

    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        super.onCreate(questContext);
        switch (mQuest.getQuestionType()) {

            case SOLUTION:

                fillButtonListWithAvailableVariants();

                break;

        }
    }

    @NonNull
    @Override
    protected View createButton(Object answerVariant,
                                @NonNull LinearLayout.LayoutParams layoutParams) {
        TrafficLightModel value = TrafficLightModel.fromOrdinal((int) answerVariant);
        Button button = (Button) mQuestContext.setUpFonts(
                (TextView) LayoutInflater.from(mQuestContext).
                        inflate(R.layout.button_traffic_light, null),
                R.style.Quest_TrafficLight_Button);
        button.setBackgroundResource(value == TrafficLightModel.GREEN ?
                R.drawable.background_button_green : R.drawable.background_button_red);
        int margin = mQuestContext.getResources().getDimensionPixelSize(R.dimen.base_semi_gap);
        layoutParams.setMargins(margin, 0, margin, 0);
        return button;
    }
}