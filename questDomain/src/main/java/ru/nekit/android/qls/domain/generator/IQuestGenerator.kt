package ru.nekit.android.qls.domain.quest.generator

import ru.nekit.android.qls.domain.model.quest.Quest

interface IQuestGenerator<out T : Quest> {

    fun generate(): T

}
