package ru.nekit.android.qls.quest.answer;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.answer.common.IQuestAnswerVariantAdapter;
import ru.nekit.android.qls.quest.model.TrafficLightModel;

public class TrafficLightQuestAnswerVariantAdapter implements IQuestAnswerVariantAdapter<Integer> {

    @Override
    @Nullable
    public String adapt(@NonNull Context context, @NonNull Integer answerVariant) {
        TrafficLightModel value = TrafficLightModel.fromOrdinal(answerVariant);
        Resources resources = context.getResources();
        switch (value) {
            case RED:
                return resources.getString(R.string.wait);

            case GREEN:
                return resources.getString(R.string.go);

            default:
                return null;
        }
    }


}
