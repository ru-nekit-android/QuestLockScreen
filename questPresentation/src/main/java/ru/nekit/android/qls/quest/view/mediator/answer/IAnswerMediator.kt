package ru.nekit.android.qls.quest.view.mediator.answer

import io.reactivex.subjects.Subject
import ru.nekit.android.qls.quest.view.mediator.IQuestMediator

//ver 1.0
interface IAnswerMediator : IQuestMediator {

    var answerPublisher: Subject<Any>

}

interface IAlternativeAnswerMediator : IAnswerMediator