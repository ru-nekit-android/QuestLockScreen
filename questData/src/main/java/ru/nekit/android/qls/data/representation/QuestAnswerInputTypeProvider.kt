package ru.nekit.android.qls.data.representation

import android.text.InputType
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest
import ru.nekit.android.qls.domain.model.quest.Quest

fun Quest.getAnswerInputType(): Int {
    return when (this) {
        is NumberSummandQuest -> InputType.TYPE_CLASS_NUMBER
    //is TextQuest -> InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        else -> InputType.TYPE_TEXT_VARIATION_NORMAL
    }
}