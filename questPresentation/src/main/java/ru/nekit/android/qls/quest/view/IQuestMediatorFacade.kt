package ru.nekit.android.qls.quest.view

import ru.nekit.android.qls.quest.view.mediator.IQuestMediator

//ver 1.0
interface IQuestMediatorFacade : IQuestMediator {

    val titleMediator: IQuestMediator

    val contentMediator: IQuestMediator

    val answerMediator: IQuestMediator

}