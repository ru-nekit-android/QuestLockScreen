package ru.nekit.android.qls.quest;

import ru.nekit.android.qls.quest.mediator.IQuestMediator;

public interface IQuestMediatorFacade extends IQuestMediator {

    IQuestMediator getTitleMediator();

    IQuestMediator getContentMediator();

    IQuestMediator getAlternativeAnswerMediator();

}