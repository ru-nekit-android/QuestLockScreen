package ru.nekit.android.qls.quest.answer;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.answer.shared.IAlternativeAnswerVariantAdapter;
import ru.nekit.android.qls.quest.types.TrafficLightType;

public class TrafficLightAlternativeAnswerVariantAdapter implements IAlternativeAnswerVariantAdapter {

    @Override
    @Nullable
    public String adapt(@NonNull Context context, @NonNull Object answerVariant) {
        TrafficLightType value = TrafficLightType.fromOrdinal((int) answerVariant);
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
