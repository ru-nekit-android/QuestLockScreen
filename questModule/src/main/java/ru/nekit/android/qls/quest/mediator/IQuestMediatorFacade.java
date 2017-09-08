package ru.nekit.android.qls.quest.mediator;

/**
 * Created by nekit on 18.01.17.
 */

public interface IQuestMediatorFacade extends IQuestMediator {

    IQuestMediator getTitleMediator();

    IQuestMediator getContentMediator();

    IQuestMediator getAlternativeAnswerMediator();

}
