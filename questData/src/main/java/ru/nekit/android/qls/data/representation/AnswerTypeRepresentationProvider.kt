package ru.nekit.android.qls.data.representation

import android.content.Context
import ru.nekit.android.qls.domain.model.AnswerType
import ru.nekit.android.qls.domain.model.AnswerType.RIGHT
import ru.nekit.android.qls.domain.model.AnswerType.WRONG
import ru.nekit.android.questData.R.string.*

object AnswerTypeRepresentationProvider : StringListIdRepresentationProvider<AnswerType>() {

    init {
        createRepresentation(WRONG, answer_type_wrong_title, answer_type_wrong_title2)
        createRepresentation(RIGHT, answer_type_right_title, answer_type_right_title2)
    }

}

fun AnswerType.getStringRepresentation(context: Context) = AnswerTypeRepresentationProvider.getRepresentation(this).getRandomString(context)