package ru.nekit.android.qls.quest.view.mediator.answer

import ru.nekit.android.qls.domain.model.resources.DirectionResourceCollection

//ver 1.0
interface ISwipeAnswerMediator : IAlternativeAnswerMediator {

    fun onSwipe(direction: DirectionResourceCollection)

}