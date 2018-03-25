package ru.nekit.android.qls.quest.answer

import android.content.Context
import ru.nekit.android.qls.domain.model.math.MathematicalSignComparison
import ru.nekit.android.qls.quest.resources.representation.getRepresentationAsWord

class MetricsQuestAnswerVariantAdapter : IAnswerVariantAdapter<MathematicalSignComparison> {

    override fun adapt(context: Context, answerVariant: MathematicalSignComparison): String? =
            answerVariant.getRepresentationAsWord().getString(context)

}