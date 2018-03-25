package ru.nekit.android.qls.quest.view.mediator.answer

import android.view.View
import io.reactivex.subjects.ReplaySubject
import ru.nekit.android.domain.model.Optional

//ver 1.0
interface IButtonListAnswerMediator {

    val answerButtonPublisher: ReplaySubject<Optional<View>>

}
