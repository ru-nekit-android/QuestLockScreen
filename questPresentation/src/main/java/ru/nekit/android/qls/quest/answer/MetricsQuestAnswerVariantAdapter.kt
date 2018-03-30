package ru.nekit.android.qls.quest.answer

import android.content.Context
import ru.nekit.android.qls.data.representation.getRepresentationAsWord
import ru.nekit.android.qls.domain.model.math.MathematicalSignComparison

class MetricsQuestAnswerVariantAdapter : IAnswerVariantAdapter<MathematicalSignComparison> {

    override fun adapt(context: Context, answerVariant: MathematicalSignComparison): String? =
            answerVariant.getRepresentationAsWord().getString(context)

}