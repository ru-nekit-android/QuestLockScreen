package ru.nekit.android.qls.quest.answer

import android.content.Context
import ru.nekit.android.qls.domain.model.resources.TrafficLightResourceCollection
import ru.nekit.android.qls.quest.resources.representation.getRepresentation

class TrafficLightQuestAnswerVariantAdapter : IAnswerVariantAdapter<Int> {

    override fun adapt(context: Context, answerVariant: Int): String? {
        return TrafficLightResourceCollection.getById(answerVariant).getRepresentation().getRandomString(context)
    }

}
