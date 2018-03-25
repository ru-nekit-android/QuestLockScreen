package ru.nekit.android.qls.domain.quest.creator.common

import ru.nekit.android.qls.domain.model.quest.Quest
import ru.nekit.android.qls.shared.model.QuestionType

interface IQuestCreator<out T : Quest> {

    fun create(questionType: QuestionType): T

}