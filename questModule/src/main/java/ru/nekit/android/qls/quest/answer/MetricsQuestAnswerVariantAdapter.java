package ru.nekit.android.qls.quest.answer;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.answer.common.IQuestAnswerVariantAdapter;
import ru.nekit.android.qls.quest.math.MathematicalSignComparison;

public class MetricsQuestAnswerVariantAdapter implements
        IQuestAnswerVariantAdapter<MathematicalSignComparison> {

    @Override
    @Nullable
    public String adapt(@NonNull Context context, @NonNull MathematicalSignComparison answerVariant) {
        Resources resources = context.getResources();
        switch (answerVariant) {
            case LESS:
                return resources.getString(R.string.shorter);

            case EQUAL:
                return resources.getString(R.string.equal);

            case GREATER:
                return resources.getString(R.string.longer);

            default:
                return null;
        }
    }
}