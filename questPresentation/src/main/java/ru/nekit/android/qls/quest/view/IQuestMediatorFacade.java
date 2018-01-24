package ru.nekit.android.qls.quest.view;

import ru.nekit.android.qls.quest.view.mediator.IQuestMediator;

public interface IQuestMediatorFacade extends IQuestMediator {

    IQuestMediator getTitleMediator();

    IQuestMediator getContentMediator();

    IQuestMediator getAlternativeAnswerMediator();

}