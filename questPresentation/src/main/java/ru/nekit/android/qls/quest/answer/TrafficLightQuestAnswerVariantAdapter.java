package ru.nekit.android.qls.quest.answer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.nekit.android.qls.quest.answer.common.IQuestAnswerVariantAdapter;
import ru.nekit.android.qls.quest.model.TrafficLightModel;

public class TrafficLightQuestAnswerVariantAdapter implements IQuestAnswerVariantAdapter<Integer> {

    @Override
    @Nullable
    public String adapt(@NonNull Context context, @NonNull Integer answerVariant) {
        return TrafficLightModel.fromOrdinal(answerVariant).getString(context);
    }


}
