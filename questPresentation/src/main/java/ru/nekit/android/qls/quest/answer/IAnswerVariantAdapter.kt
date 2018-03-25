package ru.nekit.android.qls.quest.answer

import android.content.Context

interface IAnswerVariantAdapter<in T : Any> {

    fun adapt(context: Context, answerVariant: T): String?

}
